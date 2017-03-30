package com.awei.photopicker;

import android.content.Context;

/**
 *
 */
public class GlobalApplication extends android.app.Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
