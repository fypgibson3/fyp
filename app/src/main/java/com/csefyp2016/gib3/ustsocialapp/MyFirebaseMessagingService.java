package com.csefyp2016.gib3.ustsocialapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private static final String loginPreference = "LoginPreference";
    private static final String messagePreference = "MessagePreference";
    private SharedPreferences sharedPreferences;

    private int unreadMessageCount;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String userId = remoteMessage.getData().get("userId");
            String username = remoteMessage.getData().get("username");
            String room = remoteMessage.getData().get("room");
            String message = remoteMessage.getData().get("message");

            EnterRoomEvent stickyEvent = EventBus.getDefault().getStickyEvent(EnterRoomEvent.class);
            if (stickyEvent == null || !stickyEvent.getRoom().equals(room)) {
                // no chat room is entered, or entered room is not the target one
                sendNotification(userId, username, username, message);

                sharedPreferences = getSharedPreferences(messagePreference, Context.MODE_PRIVATE);
                unreadMessageCount = sharedPreferences.getInt(room, -1);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (unreadMessageCount == -1) {
                    editor.putInt(room, 0);
                    editor.putString(room + "_message0", message);
                }
                else {
                    int newCount = unreadMessageCount + 1;
                    editor.putInt(room, newCount);
                    editor.putString(room + "_message" + newCount, message);
                }
                editor.commit();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }


    private void sendNotification(String fromId, String fromName, String messageTitle, String messageBody) {
        Intent intent = new Intent(this, IndividualChat.class);
        intent.putExtra("the_friend_id", fromId);
        intent.putExtra("the_friend_name", fromName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                /*PendingIntent.FLAG_UPDATE_CURRENT*/ PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.valueOf(fromId) /* ID of notification */, notificationBuilder.build());
    }

}
