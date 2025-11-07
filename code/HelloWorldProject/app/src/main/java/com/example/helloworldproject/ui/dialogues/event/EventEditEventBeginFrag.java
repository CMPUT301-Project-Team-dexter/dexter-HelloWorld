package com.example.helloworldproject.ui.dialogues.event;

import android.content.DialogInterface;

public class EventEditEventBeginFrag extends AbstractEventEditDateFrag {
    public EventEditEventBeginFrag(String preSetTimestamp) {
        super("Event Start Date", preSetTimestamp);
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, long dateInMilli) {
        listener.updateEventBeginDate(dateInMilli);
    }
}
