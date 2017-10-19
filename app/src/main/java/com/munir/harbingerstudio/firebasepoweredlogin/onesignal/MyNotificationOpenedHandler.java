package com.munir.harbingerstudio.firebasepoweredlogin.onesignal;

import android.content.Intent;
import android.util.Log;

import com.munir.harbingerstudio.firebasepoweredlogin.OnesignalApp;
import com.munir.harbingerstudio.firebasepoweredlogin.ProfileActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by munirul.hoque on 10/17/2017.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    String bigPicture;

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        bigPicture = result.notification.payload.bigPicture;
        JSONObject data = result.notification.payload.additionalData;
        String link = result.notification.payload.launchURL;
        String activityToBeOpened;

        //While sending a Push notification from OneSignal dashboard
        // you can send an addtional data named "activityToBeOpened" and retrieve the value of it and do necessary operation
        //If key is "activityToBeOpened" and value is "AnotherActivity", then when a user clicks
        //on the notification, AnotherActivity will be opened.
        //Else, if we have not set any additional data MainActivity is opened.
        if(data!= null) {
            activityToBeOpened = data.optString("activityToBeOpened", null);
            // String title = data.optString("t", null);
            // String body = data.optString("b", null);
            String str1 = result.notification.payload.title;
            String str2 = result.notification.payload.body;

            if(activityToBeOpened != null && activityToBeOpened.equals("ProfileActivity")){
                Log.i("OneSignalExample", "customkey set with value: " + activityToBeOpened);
                Intent intent = new Intent(OnesignalApp.getContext(), ProfileActivity.class);
                intent.putExtra("SYSTRAY","systray");
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

                // intent.putExtra("title", title);
                //intent.putExtra("body", body);
                intent.putExtra("title", str1);
                intent.putExtra("body", str2);
                if(bigPicture != null){
                    intent.putExtra("IMAGEURL", bigPicture);
                }
                OnesignalApp.getContext().startActivity(intent);
            }
        }
    }
}
