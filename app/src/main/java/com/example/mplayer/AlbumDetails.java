package com.example.mplayer;

import static com.example.mplayer.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumImage;
    String albumName;
    ArrayList<MusicFiles> albumTracks  =new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerAlbumView);
        albumImage = findViewById(R.id.album_image_albums);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0 ;
        for (int i =0; i< musicFiles.size();i++)
        {
            if (albumName.equals(musicFiles.get(i).getAlbum()))
            {
                albumTracks.add(j, musicFiles.get(i));
                j++;
            }
        }
        byte[] image = getAlbumArt(albumTracks.get(0).getPath());
        if (image!=null)
        {
            Glide.with(this).load(image).into(albumImage);
        }
        else
        {
            Glide.with(this).load(R.drawable.noimg).into(albumImage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumTracks.size()<1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumTracks);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        }
    }

    private byte[] getAlbumArt (String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}