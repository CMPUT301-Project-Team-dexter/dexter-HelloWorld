package com.example.helloworldproject.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Event;

import java.util.ArrayList;

public class AllImagesFragmentAdapter extends RecyclerView.Adapter<AllImagesFragmentAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> events;
    private final OnImageClickListener clickListener;

    public AllImagesFragmentAdapter(Context context,
                                    ArrayList<Event> events,
                                    OnImageClickListener clickListener) {
        this.context = context;
        this.events = events;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AllImagesFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllImagesFragmentAdapter.ViewHolder holder, int position) {
        Event e = events.get(position);

        // Decide which URL to use:
        // - If imgUrlEnable == true -> use imgUrl (user entered a URL)
        // - else -> use imgId (download URL from Firebase upload)
        String url = null;
        Boolean enable = e.getImgUrlEnable();
        if (enable != null && enable) {
            url = e.getImgUrl();
        } else {
            url = e.getImgId();
        }

        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.imgView);

        holder.imgView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onImageClick(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface OnImageClickListener {
        void onImageClick(Event event);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.image);
        }
    }
}
