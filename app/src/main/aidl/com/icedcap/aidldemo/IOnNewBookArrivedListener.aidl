// IOnNewBookArrivedListener.aidl
package com.icedcap.aidldemo;

import com.icedcap.aidldemo.model.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}
