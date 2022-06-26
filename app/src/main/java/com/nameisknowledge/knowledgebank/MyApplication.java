package com.nameisknowledge.knowledgebank;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Application sApplication;
    private static Application getApplication() {
        return sApplication;
    }
    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}