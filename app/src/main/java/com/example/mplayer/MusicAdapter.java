package com.example.mplayer;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.Model;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyVieHolder>
{
    private Context musContext;
    static ArrayList <MusicFiles> musFiles;

    MusicAdapter (Context musContext, ArrayList<MusicFiles> musFiles)
    {
        this.musFiles = musFiles;
        this.musContext = musContext;
    }

    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(musContext).inflate(R.layout.music_items, parent, false);
        return new MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder holder, int position) {
        holder.file_name.setText(musFiles.get(position).getTitle());
        byte[] image = getAlbumArt(musFiles.get(position).getPath());
        if (image != null)
        {
            Glide.with(musContext).asBitmap().load(image).into(holder.album_pic);
        }
        else
        {
            Glide.with(musContext).load(R.drawable.noimg).into(holder.album_pic);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(musContext, PlayerActivity.class);
                intent.putExtra("position", position);
                musContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return musFiles.size();
    }

    public class MyVieHolder extends RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView album_pic, optionMenu;
        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_pic = itemView.findViewById(R.id.music_img);
            optionMenu = itemView.findViewById(R.id.more_options);
        }
    }

    private byte[] getAlbumArt (String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        //retriever.release(); ????
        return art;
    }

    void updateList(ArrayList<MusicFiles> musicFilesArrayList)
    {
        musFiles = new ArrayList<>();
        musFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
