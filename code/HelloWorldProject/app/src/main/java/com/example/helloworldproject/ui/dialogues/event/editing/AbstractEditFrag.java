package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.helloworldproject.ui.activities.EventEditListener;

public abstract class AbstractEditFrag extends DialogFragment {
    protected EventEditListener listener;
    protected String dialogTitle;

    public AbstractEditFrag(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EventEditListener) {
            listener = (EventEditListener) context;
        } else {
            throw new RuntimeException(context + " must implements EventEditListener!");
        }
    }
}
