package com.example.mplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String Channel_ID_1 = "channel1";
    public static final String Channel_ID_2 = "channel2";
    public static final String Action_Previous = "action_previous";
    public static final String Action_Next = "action_next";
    public static final String Action_Play_Pause = "action_play_pause";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(Channel_ID_1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc");
            NotificationChannel channel2 = new NotificationChannel(Channel_ID_2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 2 Desc");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);

        }
    }
}
