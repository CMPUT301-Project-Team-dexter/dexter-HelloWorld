package com.example.helloworldproject.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helloworldproject.R;

import java.util.List;

/**
 *
 * This is the adapter for the ListView in RegisterHistoryFragment.
 *
 */
public class EventCardAdapter extends BaseAdapter {
    private Context context;
    private List<EventCardView> items;

    public EventCardAdapter(Context context, List<EventCardView> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.poster_image);
            holder.eventNameTextView = convertView.findViewById(R.id.event_name);
            holder.statusTextView = convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EventCardView item = items.get(position);
        holder.eventNameTextView.setText(item.eventName);
        holder.statusTextView.setText(item.status);
        holder.imageView.setImageResource(item.imgSrcId);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView eventNameTextView;
        TextView statusTextView;
    }
}
