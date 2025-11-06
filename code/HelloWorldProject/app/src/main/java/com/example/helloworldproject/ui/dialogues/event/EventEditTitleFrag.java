package com.example.helloworldproject.ui.dialogues.event;

import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

public class EventEditTitleFrag extends EventEditPlainTextFrag {
    public EventEditTitleFrag() {
        super("Title", "Title text");
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, EditText inputField) {
        String title = inputField.getText().toString().strip();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Title can not be empty", Toast.LENGTH_SHORT).show();
        } else {
            listener.updateTitle(title);
        }
    }
}
