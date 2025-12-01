package com.example.helloworldproject.ui.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.NotificationRecord;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Simple adapter that displays in-app notifications in the
 * RegisterHistoryFragment.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotificationRecord> items = new ArrayList<>();

    /**
     * Replace the current items and refresh the UI.
     */
    public void setItems(List<NotificationRecord> records) {
        items.clear();
        if (records != null) {
            items.addAll(records);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        NotificationRecord record = items.get(position);

        String title;
        String message;

        if (NotificationRecordTypeHelper.isLotteryNotChosen(record)) {
            // "You were not chosen" message for this event.
            String eventTitle = record.getEventTitle() != null
                    ? record.getEventTitle()
                    : "this event";
            title = "Lottery result";
            message = "You were not chosen for \"" + eventTitle + "\".";
        } else if (NotificationRecordTypeHelper.isLotteryChosen(record)) {
            String eventTitle = record.getEventTitle() != null
                    ? record.getEventTitle()
                    : "this event";
            title = "Lottery result";
            message = "You were selected for \"" + eventTitle + "\". Please respond.";
        } else {
            // Generic fallback message.
            title = "Notification";
            message = "You have a new notification.";
        }

        holder.titleView.setText(title);
        holder.messageView.setText(message);

        // Optional: show createdAt as a short date/time.
        if (record.getCreatedAt() != null) {
            String time = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,
                    DateFormat.SHORT
            ).format(new Date(record.getCreatedAt().getSeconds() * 1000));
            holder.timeView.setText(time);
        } else {
            holder.timeView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Helper class to centralise type-specific checks.
     */
    private static class NotificationRecordTypeHelper {
        static boolean isLotteryNotChosen(NotificationRecord record) {
            return "LOTTERY_NOT_CHOSEN".equals(record.getType());
        }

        static boolean isLotteryChosen(NotificationRecord record) {
            return "LOTTERY_CHOSEN".equals(record.getType());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView;
        final TextView messageView;
        final TextView timeView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.tvNotificationTitle);
            messageView = itemView.findViewById(R.id.tvNotificationMessage);
            timeView = itemView.findViewById(R.id.tvNotificationTime);
        }
    }
}
