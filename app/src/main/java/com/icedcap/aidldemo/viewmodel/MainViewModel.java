package com.icedcap.aidldemo.viewmodel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.icedcap.aidldemo.IBookManager;
import com.icedcap.aidldemo.IOnNewBookArrivedListener;
import com.icedcap.aidldemo.model.Book;

import java.util.List;

/**
 * Created by shuqi on 16-8-20.
 */
public class MainViewModel {
    private static final String TAG = "MainViewModel";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private Context mContext;
    private ServiceConnection mConnection;
    private IBookManager mIBookManager;
    private MyHandler mHandler = new MyHandler();

    public MainViewModel(Context context) {
        mContext = context;
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e(TAG, "------onServiceConnected-----------");
                mIBookManager = IBookManager.Stub.asInterface(service);
                Log.e(TAG, "mIBookManager >> " + mIBookManager);
                try {
                    mIBookManager.registerListener(mListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIBookManager = null;
                Log.i(TAG, "binder died, tname : " + Thread.currentThread().toString());
            }
        };
        if (null == mIBookManager) {
            Log.e(TAG, "bind to service.");
            Intent intent = new Intent("com.icedcap.aidldemo.BookManageService");
            intent.setPackage("com.icedcap.aidldemo");
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void unbindService() {
        if (null != mIBookManager && mIBookManager.asBinder().isBinderAlive()) {
            try {
                Log.i(TAG, "unregister listener : " + mListener);
                mIBookManager.unregisterListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mContext.unbindService(mConnection);
        Log.e(TAG, "Binder died");
    }

    public void getBooks() {
        try {
            List<Book> books = mIBookManager.getBooks();
            Log.i(TAG, "Book List type: " + books.getClass().getCanonicalName());
            Log.i(TAG, "Get books is " + books.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void addBook() {
        Book book = new Book(5, "C和指针", "-_-");
        try {
            Log.i(TAG, "Book List is " + mIBookManager.getBooks());
            mIBookManager.addBook(book);
            Log.i(TAG, "Add book ： " + book);
            Log.i(TAG, "Book List is " + mIBookManager.getBooks());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void queryBook(int id) {
        try {
            Book book = mIBookManager.queryBook(id);
            Log.i(TAG, book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IOnNewBookArrivedListener mListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, book).sendToTarget();
        }
    };

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.e(TAG, "Receive a new book : " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }
}
