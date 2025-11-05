package com.example.helloworldproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> userName;
    private ArrayList<String> userId;

    public UserItemAdapter(Context context, ArrayList<String> userName, ArrayList<String> userId) {
        this.context = context;
        this.userName = userName;
        this.userId = userId;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
            holder = new ViewHolder();
            holder.userNameView = convertView.findViewById(R.id.username);
            holder.userIdView = convertView.findViewById(R.id.userid);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userNameView.setText(userName.get(position));
        holder.userIdView.setText(userId.get(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView userNameView;
        TextView userIdView;
    }
}
