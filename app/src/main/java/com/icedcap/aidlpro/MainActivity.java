package com.icedcap.aidlpro;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.icedcap.aidlpro.service.BookChangedImpl;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ServiceConnection mConnection;
    private IBookInterface mBookInterface;
    private IBooksChangedListener mListener;
    private TextView mTextView;
    private EditText mName, mId, mLocationId;

    //添加服务端死亡通知，可以通过杀死远程服务进程进行测试
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mBookInterface == null){
                return;
            }
            Log.e(TAG, "receive the died notify.");
            mBookInterface.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBookInterface = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
        init();
    }

    private void init() {
        mTextView = (TextView) findViewById(R.id.content);
        mName = (EditText) findViewById(R.id.bookName);
        mId = (EditText) findViewById(R.id.bookId);
        mLocationId = (EditText) findViewById(R.id.locationId);
        findViewById(R.id.bookAdd).setOnClickListener(this);
        findViewById(R.id.bookSearch).setOnClickListener(this);
        findViewById(R.id.search).setOnClickListener(this);
    }

    private void bindService() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBookInterface = IBookInterface.Stub.asInterface(service);
                try {
                    //绑定死亡通知
                    mBookInterface.asBinder().linkToDeath(mDeathRecipient, 0);
                    mListener = new BookChangedImpl();
                    mBookInterface.register(mListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBookInterface = null;
            }
        };

        if (null == mBookInterface) {
            Intent service = new Intent("com.icedcap.BIND_REMOTE_SERVICE");
            service.setPackage("com.icedcap.aidlpro");
            bindService(service, mConnection, Service.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        //以下代码无法注销订阅，原因是注册的mListener和当前的mLister不是一个对象，因为真正注册的代码是在服务端
        //当前传递到服务端的监听将通过Binder机制还原，但是仍然不是客户端传过来的那个对象。
        //解决方法：使用RemoteCallbackList，具体移步到BookImpl这个类
        if (mBookInterface != null && mBookInterface.asBinder().isBinderAlive()) {
            Log.i(TAG, "unregister listener: " + mListener);
            try {
                mBookInterface.unRegister(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    //对于在主线程中进行Binder通信，若服务端业务是一个耗时操作，蒋导致ANR，例如将服务端的addBook中加入sleep
    private void addBook() {
        String name = mName.getText().toString();
        String id = mId.getText().toString();
        if ("".equals(name) || "".equals(id)) {
            Toast.makeText(this, "can not be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBookInterface != null) {
            try {
                mBookInterface.addBook(new Book(Integer.parseInt(id), name));
                mName.setText("");
                mId.setText("");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bookSearch() {
        String str = mLocationId.getText().toString();
        if ("".equals(str)) {
            Toast.makeText(this, "id can not be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBookInterface != null) {
            try {
                int id = Integer.parseInt(str);
                Book book = mBookInterface.query(id);
                mTextView.setText(null == book ? "null" : book.toString());
                mLocationId.setText("");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchBookList() {
        if (mBookInterface != null) {
            try {
                List<Book> books = mBookInterface.getBookList();
                StringBuilder str = new StringBuilder();
                if (books.size() > 0) {
                    for (int i = 0; i < books.size(); i++) {
                        str.append(books.get(i).toString() + "\n");
                    }
                    mTextView.setText(str);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookAdd:
                addBook();
                break;
            case R.id.bookSearch:
                bookSearch();
                break;
            case R.id.search:
                searchBookList();
                break;
        }
    }
}
