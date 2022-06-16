package com.nameisknowledge.knowledgebank.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.nameisknowledge.knowledgebank.Listeners.GenericListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CounterService extends IntentService {
    private static GenericListener<Void> _genericListener;
    public CounterService() {
        super("CounterService");
    }

    public static void startAction(Context context,GenericListener<Void> genericListener) {
        Intent intent = new Intent(context, CounterService.class);
        _genericListener = genericListener;
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        for (int i=1;i<=10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Test","onDestroy");
        _genericListener.getData(null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Test","onDestroy");
    }
}