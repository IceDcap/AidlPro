package com.icedcap.aidlpro;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: doushuqi
 * Date: 16-6-6
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class Book implements Parcelable {
    private int mId;
    private String mName;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public Book(int id, String name) {
        mId = id;
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return " id = " + mId + ", " + "name = " + mName + " ";
    }
}
