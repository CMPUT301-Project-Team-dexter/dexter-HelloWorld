package com.example.helloworldproject.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.helloworldproject.R;

import java.util.ArrayList;

public class EventItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> userName;
    private ArrayList<String> userId;

    private ArrayList<String> eventName;

    public EventItemAdapter(Context context, ArrayList<String> userName, ArrayList<String> userId, ArrayList<String> eventName) {
        this.context = context;
        this.userName = userName;
        this.userId = userId;
        this.eventName = eventName;
        assert(userName.size() == userId.size());
    }

    @Override
    public int getCount() {
        return userName.size();
    }

    @Override
    public Object getItem(int position) {
        return userName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
            holder = new ViewHolder();
            holder.userNameView = convertView.findViewById(R.id.username);
            holder.userIdView = convertView.findViewById(R.id.userid);
            holder.eventNameView = convertView.findViewById(R.id.eventname);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userNameView.setText(userName.get(position));
        holder.userIdView.setText(userId.get(position));
        holder.eventNameView.setText(eventName.get(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView userNameView;
        TextView userIdView;
        TextView eventNameView;
    }
}
