package com.icedcap.aidldemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shuqi on 16-8-20.
 */
public class Book implements Parcelable {
    private int mId;
    private String mName;
    private String mAuthor;

    public Book(int id, String name, String author) {
        mId = id;
        mName = name;
        mAuthor = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mAuthor);
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
        this.mAuthor = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
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
        return "(" + mId + ", " + mName + ", " + mAuthor+")";
    }
}
