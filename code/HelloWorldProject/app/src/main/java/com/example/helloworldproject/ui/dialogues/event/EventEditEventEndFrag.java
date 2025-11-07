package com.example.helloworldproject.ui.dialogues.event;

import android.content.DialogInterface;

public class EventEditEventEndFrag extends AbstractEventEditDateFrag {
    public EventEditEventEndFrag(String preSetTimestamp) {
        super("Event Start Date", preSetTimestamp);
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, long dateInMilli) {
        listener.updateEventEndDate(dateInMilli);
    }
}
