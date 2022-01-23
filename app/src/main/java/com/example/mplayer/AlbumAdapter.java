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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyAlbumViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item,parent,false);
        return new MyAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.albumName.setText(albumFiles.get(position).getAlbum());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());
        if (image != null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.albumImage);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.noimg).into(holder.albumImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mContext, AlbumDetails.class);
                intent.putExtra("albumName", albumFiles.get(position).getAlbum());
                mContext.startActivity(intent);
            }
        });
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
            albumImage = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
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
