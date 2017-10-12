package com.munir.harbingerstudio.firebasepoweredlogin.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.munir.harbingerstudio.firebasepoweredlogin.MainActivity;
import com.munir.harbingerstudio.firebasepoweredlogin.ProfileActivity;
import com.munir.harbingerstudio.firebasepoweredlogin.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by munirul.hoque on 9/18/2017.
 */

public class NewsFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "NewsMessagingService";
    private Bitmap bitmap;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseMessaging.getInstance().subscribeToTopic("android");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String notificationTitle = null, notificationBody = null, bigImageUrl = null;
       if(remoteMessage.getData().size()>0){
           String temp = remoteMessage.getData().get("imageUrl");
           bitmap = getBitmapfromUrl(remoteMessage.getData().get("imageUrl"));
           showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("author"), bitmap);
       }
    }

    private void showNotification(String title, String author, Bitmap image){
        Intent intent = new Intent(this, ProfileActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0/* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(author)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */,notificationBuilder.build());
    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
