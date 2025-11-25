package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.DialogInterface;

public class EventBeginFrag extends AbstractDateFrag {
    public EventBeginFrag(String preSetTimestamp) {
        super("Event Start Date", preSetTimestamp);
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, long dateInMilli) {
        listener.updateEventBeginDate(dateInMilli);
    }
}
