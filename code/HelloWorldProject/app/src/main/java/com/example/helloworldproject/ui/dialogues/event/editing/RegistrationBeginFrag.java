package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.DialogInterface;

public class RegistrationBeginFrag extends AbstractDateFrag {
    public RegistrationBeginFrag(String preSetTimestamp) {
        super("Registration Begin Date", preSetTimestamp);
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, long dateInMilli) {
        listener.updateRegBeginDate(dateInMilli);
    }
}
