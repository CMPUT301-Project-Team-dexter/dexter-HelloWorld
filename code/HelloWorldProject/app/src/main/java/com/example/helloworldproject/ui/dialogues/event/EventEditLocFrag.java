package com.example.helloworldproject.ui.dialogues.event;

import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helloworldproject.R;

public class EventEditLocFrag extends AbstractEventEditPlainTextFrag {
    public EventEditLocFrag() {
        super(
            "Location",
            R.layout.event_edit_text_frag,
            R.id.edit_text_input,
            "Location text"
        );
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
