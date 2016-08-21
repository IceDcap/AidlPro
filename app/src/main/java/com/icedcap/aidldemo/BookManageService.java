package com.icedcap.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.icedcap.aidldemo.model.Book;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by shuqi on 16-8-20.
 */
public class BookManageService extends Service {
    private static final String TAG = "BookManageService";
    private AtomicBoolean mIsServiceDestroy = new AtomicBoolean(false);
    private final CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        mBooks.add(new Book(1, "深入理解Linux内核", "BOVET&CESATI"));
        mBooks.add(new Book(2, "Python基础教程", "Magnus Lie Hetland"));
        mBooks.add(new Book(3, "深入理解Java虚拟机", "周志明"));
        mBooks.add(new Book(4, "Android开发艺术探索", "任玉刚"));
        new Thread(new WorkService()).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        mIsServiceDestroy.set(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("com.icedcap.aidldemo.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            Log.i(TAG, "No permission to access service");
            return null;
        }
        return new BookManagerImpl();
    }

    class BookManagerImpl extends IBookManager.Stub {

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int check = checkCallingOrSelfPermission("com.icedcap.aidldemo.permission.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            String packageName = "";
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length >= 1) {
                packageName = packages[0];
            }
            if (!packageName.startsWith("com.icedcap")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public List getBooks() throws RemoteException {
            synchronized (mBooks) {//需要同步，因为这里的操作是在Binder线程池中
//                SystemClock.sleep(100000);
                Log.i(TAG, "Thread => " + Thread.currentThread());
                return mBooks;
            }
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            synchronized (mBooks) {
                mBooks.add(book);
            }
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }

        @Override
        public Book queryBook(int id) throws RemoteException {
            return null;
        }
    }

    private void onNewBookArrived(Book book) {
        mBooks.add(book);
        final int N = mListenerList.beginBroadcast();
        Log.d(TAG, "A new book arrived, notify listeners: " + N);
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            if (null != listener) {
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }

    private class WorkService implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestroy.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBooks.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId, "author#" + bookId);
                onNewBookArrived(newBook);
            }
        }
    }
}
