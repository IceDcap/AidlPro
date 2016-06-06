package com.icedcap.aidlpro.service;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.icedcap.aidlpro.Book;
import com.icedcap.aidlpro.IBookInterface;
import com.icedcap.aidlpro.IBooksChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: doushuqi
 * Date: 16-6-6
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class BookImpl extends IBookInterface.Stub {

    private List<Book> mBooks = new ArrayList<>();
//    private ArrayList<IBooksChangedListener> mListeners = new ArrayList<>();
    private RemoteCallbackList<IBooksChangedListener> mListeners = new RemoteCallbackList<>();

    @Override
    public List<Book> getBookList() throws RemoteException {
        return mBooks;
    }

    @Override
    public Book query(int id) throws RemoteException {
        return id >= 0 && id < mBooks.size() ? mBooks.get(id) : null;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
//        try {
//            Thread.sleep(50000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        mBooks.add(book);
    }

    @Override
    public void register(IBooksChangedListener listener) throws RemoteException {
//        mListeners.add(listener);
        mListeners.register(listener);
    }

    @Override
    public void unRegister(IBooksChangedListener listener) throws RemoteException {
//        mListeners.remove(listener);
        mListeners.unregister(listener);
    }

    @Override
    public void addBookByName(String name) throws RemoteException {
        Book book = new Book();
        book.setName(name);
        mBooks.add(book);
    }

//    public ArrayList<IBooksChangedListener> getListeners() {
//        return mListeners;
//    }

    public RemoteCallbackList<IBooksChangedListener> getListeners() {
        return mListeners;
    }
}
