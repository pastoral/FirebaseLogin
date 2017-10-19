package com.munir.harbingerstudio.firebasepoweredlogin.onesignal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.munir.harbingerstudio.firebasepoweredlogin.OnesignalApp;
import com.munir.harbingerstudio.firebasepoweredlogin.R;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by munirul.hoque on 10/16/2017.
 */

public class MyNotificationExtenderService extends NotificationExtenderService {
    String bigPicture;
    String activityToBeOpened;
    String link;
    String modelSWVersion;
    String title, body, t, b, notificationID, notificationType;
    JSONObject data;
    int totalSize, rowId;
    List<Integer> rowsToDelete;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        data = notification.payload.additionalData;
        link = notification.payload.launchURL;
        bigPicture = notification.payload.bigPicture;
       // modelSWVersion = data.optString("modelSWVersion", null);
        title = notification.payload.title;
        body = notification.payload.body;
        activityToBeOpened = data.optString("activityToBeOpened", null);
      //  t = data.optString("t", null); // useless
        //b = data.optString("b", null); // useless
        notificationID = notification.payload.notificationID.toString();

        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                Bitmap icon = BitmapFactory.decodeResource(OnesignalApp.getContext().getResources(), R.mipmap.ic_launcher);
                builder.setLargeIcon(icon);
                return builder.setColor(new BigInteger("FF0000FF", 16).intValue());
            }
        };


        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d("OneSignalExample","Notification displayed with id: "+displayedResult.androidNotificationId);
        return true;
    }
}
