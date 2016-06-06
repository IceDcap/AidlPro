// IBookInterface.aidl
package com.icedcap.aidlpro;
import com.icedcap.aidlpro.Book;
import com.icedcap.aidlpro.IBooksChangedListener;

// Declare any non-default types here with import statements
// 注意在AIDL中的方法名不能相同即使传入参数或者返回类型不同时也不能声明相同名称的方法
// 对于Parcelable对象作为参数时要使用关键字in，例如第12行所示
interface IBookInterface {
    List<Book> getBookList();
    Book query(int id);
    void addBook(in Book book);
    void addBookByName(String name);
    void register(IBooksChangedListener listener);
    void unRegister(IBooksChangedListener listener);
}
