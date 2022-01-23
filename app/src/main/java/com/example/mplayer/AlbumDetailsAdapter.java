package com.example.mplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyAlbumViewHolder> {

    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.albumName.setText(albumFiles.get(position).getTitle());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());
        if (image != null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.albumImage);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.noimg).into(holder.albumImage);
        }

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyAlbumViewHolder extends RecyclerView.ViewHolder{
        ImageView albumImage;
        TextView albumName;
        public MyAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.music_img);
            albumName = itemView.findViewById(R.id.music_file_name);
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
