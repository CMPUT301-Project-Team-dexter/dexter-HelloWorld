package com.example.helloworldproject.ui.activities.event;

import android.net.Uri;

import java.util.List;

public interface EventEditListener {
    void updateTitle(String newTitle);

    void updateLocation(String newLocation);

    void updateRegBeginDate(long dateInMilli);

    void updateRegEndDate(long dateInMilli);

    void updateEventBeginDate(long dateInMilli);

    void updateEventEndDate(long dateInMilli);

    void updateEventCapacity(int newCapacity);

    void updateWaitingListCapacity(int newCapacity);

    void updateDetail(String newDetail);

    void updateTags(List<String> newTags);

    void updateImgUri(Uri uri);

    void updateImgUrl(String url);

    void updateImgUrlEnable(Boolean imgUrlEnable);
}
