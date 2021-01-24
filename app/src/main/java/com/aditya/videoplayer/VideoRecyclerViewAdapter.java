package com.aditya.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.ViewHolder> {

    List<Video> videoList;
    Context context;

    public VideoRecyclerViewAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,null);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Video video = videoList.get(position);

            holder.mTitle.setText(video.getTitle());
            holder.mId.setText("http://www.youtube.com/watch?v=" + video.getId());
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTitle,mId;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            mTitle = itemView.findViewById(R.id.rTitle);
            mId = itemView.findViewById(R.id.rDescp);
            imageView = itemView.findViewById(R.id.rimageview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ctx, "Video can't be played currently.\nThis Feature is not available", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
