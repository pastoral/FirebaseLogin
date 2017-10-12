package com.munir.harbingerstudio.firebasepoweredlogin;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;

/**
 * Created by munirul.hoque on 10/12/2017.
 */

public class OnesignalApp extends Application {
    private static OnesignalApp mInstance;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
