package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.DialogInterface;

public class RegistrationEndFrag extends AbstractDateFrag {
    public RegistrationEndFrag(String preSetTimestamp) {
        super("Registration End Date", preSetTimestamp);
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, long dateInMilli) {
        listener.updateRegEndDate(dateInMilli);
    }
}
