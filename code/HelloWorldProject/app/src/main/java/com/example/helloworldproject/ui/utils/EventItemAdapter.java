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
    private final Context context;
    private final ArrayList<String> userName;
    private final ArrayList<String> userId;
    private final ArrayList<String> eventName;
    private final LayoutInflater inflater;

    public EventItemAdapter(Context context,
                            ArrayList<String> userName,
                            ArrayList<String> userId,
                            ArrayList<String> eventName) {
        this.context = context;
        this.userName = userName;
        this.userId = userId;
        this.eventName = eventName;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userName == null ? 0 : userName.size();
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
            // Inflate the row layout
            convertView = inflater.inflate(R.layout.event_item, parent, false);

            // Create and bind the ViewHolder
            holder = new ViewHolder();
            holder.userNameView = convertView.findViewById(R.id.username);
            holder.userIdView = convertView.findViewById(R.id.userid);
            holder.eventNameView = convertView.findViewById(R.id.eventname);

            // Store holder in the view tag for reuse
            convertView.setTag(holder);
        } else {
            // Reuse existing holder
            holder = (ViewHolder) convertView.getTag();
        }

        // Now holder is guaranteed non-null
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
