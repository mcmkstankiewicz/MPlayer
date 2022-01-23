package com.example.mplayer;

import static com.example.mplayer.AlbumDetailsAdapter.albumFiles;
import static com.example.mplayer.ApplicationClass.Action_Next;
import static com.example.mplayer.ApplicationClass.Action_Play_Pause;
import static com.example.mplayer.ApplicationClass.Action_Previous;
import static com.example.mplayer.ApplicationClass.Channel_ID_2;
import static com.example.mplayer.MainActivity.musicFiles;
import static com.example.mplayer.MainActivity.repeatBool;
import static com.example.mplayer.MainActivity.shuffleBool;
import static com.example.mplayer.MusicAdapter.musFiles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements  ActionPlayer, ServiceConnection {

    TextView songName, artistName, durPlayed, durTotal;
    ImageView backBtn, coverArt, nextBtn, prevBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listOfSongs  = new ArrayList<>();
    static Uri uri;
    //static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, previousThread, nextThread;
    MusicService musicService;
    MediaSessionCompat mediaSessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"Audio");
        initViews();
        getIntenMethod();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser)
                {
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null)
                {
                    int mCurrentPosition = musicService.getCurrentPosition() /1000;
                    seekBar.setProgress(mCurrentPosition);
                    durPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBool)
                {
                    shuffleBool = false;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle);
                }
                else
                {
                    shuffleBool = true;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBool)
                {
                    repeatBool = false;
                    repeatBtn.setImageResource(R.drawable.ic_baseline_repeat);
                }
                else
                {
                    repeatBool = true;
                    repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        previousThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() {
        if (musicService.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play);
            showNotification(R.drawable.ic_baseline_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() /1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
           playPauseBtn.setImageResource(R.drawable.ic_baseline_pause);
           showNotification(R.drawable.ic_baseline_pause);
           musicService.start();
           seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() /1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listOfSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position + 1) % listOfSongs.size());
            }
            // else pozostaje tylko repeat on i nic sie nie zmienia
            uri  = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() /1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause);
            musicService.start();
        }
        else
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listOfSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position + 1) % listOfSongs.size());
            }
            // else pozostaje tylko repeat on i nic sie nie zmienia
            uri  = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() /1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void previousThreadBtn() {
        previousThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previousBtnClicked();
                    }
                });
            }
        };
        previousThread.start();
    }

    public void previousBtnClicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listOfSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            }
            // else pozostaje tylko repeat on i nic sie nie zmienia
            uri  = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            //mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() /1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause);
            musicService.start();
        }
        else
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listOfSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            }
            // else pozostaje tylko repeat on i nic sie nie zmienia
            uri  = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() /1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play);
        }
    }


    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition %60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" +seconds;
        if (seconds.length() == 1)
        {
            return totalNew;
        }
        else
        {
            return totalOut;
        }
    }

    private void getIntenMethod() {
        position = getIntent().getIntExtra("position", -1);
        ///*
        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("albumDetails"))
        {
            listOfSongs = albumFiles;
        }
        else
        {
            listOfSongs = musFiles;
        }
        //*/
        //listOfSongs = musicFiles;

        if (listOfSongs != null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause);
            uri = Uri.parse(listOfSongs.get(position).getPath());
        }
        showNotification(R.drawable.ic_baseline_pause);
        Intent intent = new Intent (this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);
    }

    private void initViews() {
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.song_artist);
        durPlayed = findViewById(R.id.dur_played);
        durTotal = findViewById(R.id.dur_total);
        backBtn = findViewById(R.id.back_btn);
        coverArt = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.next_button);
        prevBtn = findViewById(R.id.prev_button);
        shuffleBtn = findViewById(R.id.shuffle);
        repeatBtn = findViewById(R.id.repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seek_bar);
    }

    private void metaData (Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listOfSongs.get(position).getDuration()) /1000;
        durTotal.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        //do Palette stare SDK
        Bitmap bitmap;

        if (art != null)
        {
            //Glide.with(this).asBitmap().load(art).into(coverArt);
            ///*
            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this, coverArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null)
                    {
                        ImageView gradient = findViewById(R.id.layout_image_gradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        songName.setTextColor(swatch.getTitleTextColor());
                        artistName.setTextColor(swatch.getBodyTextColor());
                    }
                    else
                    {
                        ImageView gradient = findViewById(R.id.layout_image_gradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        songName.setTextColor(Color.WHITE);
                        artistName.setTextColor(Color.DKGRAY);
                    }
                }

            });
            /*
            */
        }
        else
        {
            Glide.with(this).asBitmap().load(R.drawable.noimg).into(coverArt);
            ///*
            ImageView gradient = findViewById(R.id.layout_image_gradient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            songName.setTextColor(Color.WHITE);
            artistName.setTextColor(Color.DKGRAY);
            /*
            */
        }
    }
    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap)
    {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) { }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        Toast.makeText(this, "StartedPlaying " , Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        songName.setText(listOfSongs.get(position).getTitle());
        artistName.setText(listOfSongs.get(position).getArtist());
        musicService.onCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    void showNotification (int playPauseButton){
        Intent intent = new Intent (this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,0);

        Intent prevIntent = new Intent (this, NotificationReceiver.class).setAction(Action_Previous);
        PendingIntent prevPending = PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent (this, NotificationReceiver.class).setAction(Action_Play_Pause);
        PendingIntent pausePending = PendingIntent.getBroadcast(this,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent (this, NotificationReceiver.class).setAction(Action_Next);
        PendingIntent nextPending = PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;
        picture = getAlbumArt(listOfSongs.get(position).getPath());
        Bitmap thumb = null;
        if (picture!=null)
        {
            thumb = BitmapFactory.decodeByteArray(picture,0, picture.length);
        }
        else
        {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.noimg);
        }
        Notification notification = new NotificationCompat.Builder(this, Channel_ID_2)
                .setSmallIcon(playPauseButton)
                .setLargeIcon(thumb)
                .setContentTitle(listOfSongs.get(position).getTitle())
                .setContentText(listOfSongs.get(position).getArtist())
                .addAction(R.drawable.ic_baseline_arrow_left, "Previous", prevPending)
                .addAction(playPauseButton, "Pause", pausePending)
                .addAction(R.drawable.ic_baseline_arrow_right, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private byte[] getAlbumArt (String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        //retriever.release(); ????
        return art;
    }
}