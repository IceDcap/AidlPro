// IBookManager.aidl
package com.icedcap.aidldemo;

import com.icedcap.aidldemo.model.Book;
import com.icedcap.aidldemo.IOnNewBookArrivedListener;
// Declare any non-default types here with import statements

interface IBookManager {

    List getBooks();

    void addBook(in Book book);

    Book queryBook(int id);

    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);

}
