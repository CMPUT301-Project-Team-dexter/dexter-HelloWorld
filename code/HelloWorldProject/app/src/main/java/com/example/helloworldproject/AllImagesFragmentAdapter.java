package com.example.helloworldproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllImagesFragmentAdapter extends RecyclerView.Adapter<AllImagesFragmentAdapter.ViewHolder> {

    Context context;
    ArrayList<Integer> imgSrcIdList;

    public AllImagesFragmentAdapter(Context context, ArrayList<Integer> imgSrcIdList) {
        this.context = context;
        this.imgSrcIdList = imgSrcIdList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.image);
        }
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
        holder.imgView.setImageResource(imgSrcIdList.get(position));
    }

    @Override
    public int getItemCount() {
        return imgSrcIdList.size();
    }
}
