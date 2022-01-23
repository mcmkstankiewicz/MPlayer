package com.example.mplayer;

import static com.example.mplayer.ApplicationClass.Action_Next;
import static com.example.mplayer.ApplicationClass.Action_Play_Pause;
import static com.example.mplayer.ApplicationClass.Action_Previous;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if (actionName!= null)
        {
            switch (actionName)
            {
                case Action_Play_Pause:
                    serviceIntent.putExtra("ActionName", "playPause");
                    context.startService(serviceIntent);
                    break;
                case Action_Next:
                    serviceIntent.putExtra("ActionName", "next");
                    context.startService(serviceIntent);
                    break;
                case Action_Previous:
                    serviceIntent.putExtra("ActionName", "previous");
                    context.startService(serviceIntent);
                    break;
            }
        }

    }
}
