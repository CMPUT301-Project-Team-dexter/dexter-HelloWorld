package com.example.helloworldproject.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helloworldproject.R;

import java.util.ArrayList;

public class UserItemAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final ArrayList<String> userNames;
    private final ArrayList<String> userIds;
    private final OnDeleteClickListener deleteClickListener;

    public UserItemAdapter(Context context,
                           ArrayList<String> userNames,
                           ArrayList<String> userIds,
                           OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
        this.inflater = LayoutInflater.from(context);
        this.userNames = userNames;
        this.userIds = userIds;
    }

    @Override
    public int getCount() {
        return userNames != null ? userNames.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return userNames != null ? userNames.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate a fresh row
            convertView = inflater.inflate(R.layout.user_item, parent, false);
            holder = new ViewHolder();
            holder.userNameView = convertView.findViewById(R.id.username);
            holder.userIdView = convertView.findViewById(R.id.userid);
            holder.deleteView = convertView.findViewById(R.id.delete_button);
            convertView.setTag(holder);
        } else {
            // Try to reuse the holder; if tag is wrong/null, re-inflate safely
            Object tag = convertView.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            } else {
                convertView = inflater.inflate(R.layout.user_item, parent, false);
                holder = new ViewHolder();
                holder.userNameView = convertView.findViewById(R.id.username);
                holder.userIdView = convertView.findViewById(R.id.userid);
                holder.deleteView = convertView.findViewById(R.id.delete_button);
                convertView.setTag(holder);
            }
        }

        String name = userNames.get(position);
        String id = (userIds != null && userIds.size() > position)
            ? userIds.get(position)
            : "";

        holder.userNameView.setText(name);
        holder.userIdView.setText(id);
        if (holder.deleteView != null && deleteClickListener != null) {
            holder.deleteView.setOnClickListener(v ->
                deleteClickListener.onDeleteClick(position)
            );
        }


        return convertView;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    private static class ViewHolder {
        TextView userNameView;
        TextView userIdView;
        ImageView deleteView;   // NEW
    }
}
