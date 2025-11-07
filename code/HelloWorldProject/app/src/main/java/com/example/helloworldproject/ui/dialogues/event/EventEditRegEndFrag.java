package com.example.helloworldproject.ui.dialogues.event;

import android.content.DialogInterface;

public class EventEditRegEndFrag extends AbstractEventEditDateFrag {
    public EventEditRegEndFrag(String preSetTimestamp) {
        super("Registration End Date", preSetTimestamp);
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, long dateInMilli) {
        listener.updateRegEndDate(dateInMilli);
    }
}
