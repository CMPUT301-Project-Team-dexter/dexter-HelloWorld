package com.example.helloworldproject.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.ImageRepository;
import com.example.helloworldproject.databinding.ItemEventCardBinding;
import com.example.helloworldproject.model.Event;

import java.util.List;

/**
 * This is the adapter for showing the event cards in a list view.
 */
public class EventCardAdapter extends ArrayAdapter<Event> {
    private final LayoutInflater inflater;

    public EventCardAdapter(Context context, List<Event> items) {
        super(context, 0, items);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemEventCardBinding cardBinding;
        View cardView;
        if (convertView == null) {
            cardBinding = ItemEventCardBinding.inflate(inflater, parent, false);
            cardView = cardBinding.getRoot();
            cardView.setTag(R.id.global_binding_cache_key, cardBinding);
        } else {
            cardView = convertView;
            cardBinding = (ItemEventCardBinding) cardView.getTag(R.id.global_binding_cache_key);
        }
        Event item = getItem(position);
        if (item == null) {
            throw new IllegalStateException("Event at position " + position + " is null");
        }
        cardBinding.eventName.setText(item.getTitle());
//        ImageRepository.INSTANCE.readImageIntoView(getContext(), cardBinding.posterImage, item.getImgId(), new ImageRepository.UrlCallback() {
//            @Override
//            public void onSuccess() {
//                ;
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });

        cardBinding.posterImage.setImageResource(R.drawable.debug_card_image);
        // region Event Status
        switch (item.getRealTimeStatus()) {
            case NOT_OPEN:
                cardBinding.eventStatus.setText("Not Open Yet");
                break;
            case REGISTRATION_OPEN:
                cardBinding.eventStatus.setText("Registration open until " + Event.formatDate(item.getRegistrationCloseAt()));
                break;
            case REGISTRATION_CLOSED:
                cardBinding.eventStatus.setText("Registration Closed.\nEvent starts on " + Event.formatDate(item.getEventStartAt()));
                break;
            case ONGOING:
                cardBinding.eventStatus.setText("Ongoing. Ends on " + Event.formatDate(item.getEventEndAt()));
                break;
            case ENDED:
                cardBinding.eventStatus.setText("Event Ended");
                break;
        }
        // endregion
        return cardView;
    }
}
