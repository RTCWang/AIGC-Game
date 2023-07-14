package com.proj.avatar.utils;

import android.os.Handler;
import android.os.Looper;

public abstract class CB {
    private static Handler handler = new Handler(Looper.getMainLooper());

    public abstract void onResult(boolean succ, String msg);

    public void _complete(boolean succ, String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onResult(succ, msg);
            }
        });
    }

    ;
}
