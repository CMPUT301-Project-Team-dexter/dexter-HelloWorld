package com.example.helloworldproject.ui.dialogues.event;

import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

public class EventEditLocFrag extends EventEditPlainTextFrag {
    public EventEditLocFrag() {
        super("Location", "Location text");
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, EditText inputField) {
        String title = inputField.getText().toString().strip();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Location can not be empty", Toast.LENGTH_SHORT).show();
        } else {
            listener.updateLocation(title);
        }
    }
}
