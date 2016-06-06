package com.icedcap.aidlpro.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.icedcap.aidlpro.IBooksChangedListener;

import java.util.ArrayList;

/**
 * Author: doushuqi
 * Date: 16-6-6
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class RemoteService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        final IBinder binder = new BookImpl();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 10;
                while (count > 0) {
                    count -= 1;
                    try {
                        Thread.sleep(5000);
//                        ArrayList<IBooksChangedListener> listeners = ((BookImpl) binder).getListeners();
//                        for (int i = 0; i < listeners.size(); i++) {
//                            IBooksChangedListener listener = listeners.get(i);
//                            listener.notifySubcribe();
//                        }

                        RemoteCallbackList<IBooksChangedListener> listeners = ((BookImpl) binder).getListeners();
                        int size = listeners.beginBroadcast();//
                        for (int i = 0; i < size; i++) {
                            IBooksChangedListener listener = listeners.getBroadcastItem(i);
                            if (null!=listener) {
                                try {
                                    listener.notifySubcribe();
                                }catch (RemoteException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        listeners.finishBroadcast();//
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return binder;
    }
}
