// IBooksChangedListener.aidl
package com.icedcap.aidlpro;

// Declare any non-default types here with import statements
//一开始使用notify命名的方法，结果编译失败，这里怀疑notify是AIDL的关键字

interface IBooksChangedListener {
    void notifySubcribe();
}
