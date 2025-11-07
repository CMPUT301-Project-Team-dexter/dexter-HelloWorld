package com.example.helloworldproject.ui.dialogues.event;

import android.content.DialogInterface;

public class EventEditRegBeginFrag extends AbstractEventEditDateFrag {
    public EventEditRegBeginFrag(String preSetTimestamp) {
        super("Registration Begin Date", preSetTimestamp);
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, long dateInMilli) {
        listener.updateRegBeginDate(dateInMilli);
    }
}
