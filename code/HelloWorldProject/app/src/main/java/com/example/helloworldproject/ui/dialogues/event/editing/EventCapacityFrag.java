package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helloworldproject.R;

public class EventCapacityFrag extends AbstractPlainTextFrag {
    public EventCapacityFrag() {
        super(
            "Event Capacity",
            R.layout.frag_event_edit_num,
            R.id.edit_num_input,
            "capacity number"
        );
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, EditText inputField) {
        String value = inputField.getText().toString().strip();
        if (value.isEmpty()) {
            Toast.makeText(getContext(), "Number can not be empty", Toast.LENGTH_SHORT).show();
        } else {
            listener.updateEventCapacity(Integer.parseInt(value));
        }
    }
}
