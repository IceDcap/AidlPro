package com.icedcap.aidlpro.service;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.icedcap.aidlpro.IBooksChangedListener;

/**
 * Author: doushuqi
 * Date: 16-6-6
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class BookChangedImpl extends IBooksChangedListener.Stub {
    private static final String TAG = "BookChangedImpl";

    @Override
    public void notifySubcribe() throws RemoteException {
        Log.d(TAG, "receive the notification");
    }

}
