package com.example.helloworldproject;

/**
 * This class contains the information needed for each card in the register history fragment.
 * This will be wrapped up in a data list and fed into EventCardAdapter, to create a ListView in register history fragment.
 * NOTE: Feel free to edit this
 */
public class EventCardView {
    String eventName;
    String status;
    int imgSrcId;

    public EventCardView(String eventName, String status, int imgSrcId) {
        this.eventName = eventName;
        this.status = status;
        this.imgSrcId = imgSrcId;
    }
}
