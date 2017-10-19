package com.munir.harbingerstudio.firebasepoweredlogin;

import android.app.Application;
import android.content.Context;

import com.munir.harbingerstudio.firebasepoweredlogin.onesignal.MyNotificationOpenedHandler;
import com.munir.harbingerstudio.firebasepoweredlogin.onesignal.MyNotificationReceivedHandler;
import com.munir.harbingerstudio.firebasepoweredlogin.receiver.ConnectivityReceiver;
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
        mInstance = this;

        context = getApplicationContext();

        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler( new MyNotificationReceivedHandler() )
                .init();

    }

    public static synchronized OnesignalApp getmInstance(){
        return mInstance;
    }
    public void setConnectiviyListner(ConnectivityReceiver.ConnectivityReceiverListner listner){
        ConnectivityReceiver.connectivityReceiverListner = listner;
    }

}
