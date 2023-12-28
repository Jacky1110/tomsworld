package com.jotangi.tomsworld;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by carolyn on 2018/4/9.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;
    public static final String INFO_UPDATE_FILTER = "info_update_filter";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("message"));
        }
        /*
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getBody());
        }
        */
    }

    private void showNotification(String title,String message){
        String channeId = "1";
        String channelName="ChannelName";
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("NOTIFY_EXTRA", "NOTIFY");
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            try{
                NotificationChannel mChannel= new NotificationChannel(channeId, channelName, NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableVibration(true); //震动设置
                mChannel.setSound(defaultSoundUri, null); //设置提示音，IMPORTANCE_DEFAULT及以上才会有声音
                notificationManager.createNotificationChannel(mChannel);
                Notification.Builder builder =
                        new Notification.Builder(this)
                                .setSmallIcon(R.drawable.tomsicon)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setContentIntent(pi)
                                .setAutoCancel(true)
                                .setChannelId(channeId);
                notificationManager.notify(0, builder.build());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this,channeId)
                    .setSmallIcon(R.drawable.tomsicon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setChannelId(channeId);
            notificationManager.notify(0, notificationBuilder.build());
        }

    }

    private void uiRefresh(){
        broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
        Intent intent = new Intent(INFO_UPDATE_FILTER);
        intent.putExtra("BADGE_EXTRA", "BADGE_REFRESH");
        broadcaster.sendBroadcast(intent);
    }
}
