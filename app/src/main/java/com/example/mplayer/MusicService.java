package com.example.mplayer;

import static com.example.mplayer.PlayerActivity.listOfSongs;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlayer actionPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        musicFiles = listOfSongs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }



    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition",-1);
        String actionName = intent.getStringExtra("ActionName");
        if(myPosition != -1)
        {
            playMedia(myPosition);
        }
        if (actionName != null)
        {
            switch (actionName){
                case "playPause":
                    Toast.makeText(this, "PlayPause", Toast.LENGTH_SHORT).show();
                    break;
                case "next":
                    Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                    break;
                case "previous":
                    Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        //playMedia(position);
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        musicFiles = listOfSongs;
        position = startPosition;
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles!= null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else
        {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start()
    {
        mediaPlayer.start();
    }
    boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }
    void stop ()
    {
        mediaPlayer.stop();
    }
    void release()
    {
        mediaPlayer.release();
    }
    int getDuration()
    {
        return mediaPlayer.getDuration();
    }
    void seekTo(int position)
    {
        mediaPlayer.seekTo(position);
    }
    int getCurrentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }
    void createMediaPlayer(int position)
    {
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }
    void pause()
    {
        mediaPlayer.pause();
    }

    void onCompleted()
    {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlayer != null)
        {
            actionPlayer.nextBtnClicked();
        }
        createMediaPlayer(position);
        mediaPlayer.start();
        onCompleted();

    }

}
