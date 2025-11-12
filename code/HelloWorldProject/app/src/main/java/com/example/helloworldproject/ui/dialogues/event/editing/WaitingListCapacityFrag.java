package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helloworldproject.R;

public class WaitingListCapacityFrag extends AbstractPlainTextFrag {
    public WaitingListCapacityFrag() {
        super(
            "Event Capacity",
            R.layout.event_edit_num_frag,
            R.id.edit_num_input,
            "waiting list capacity number"
        );
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, EditText inputField) {
        String value = inputField.getText().toString().strip();
        if (value.isEmpty()) {
            Toast.makeText(getContext(), "Number can not be empty", Toast.LENGTH_SHORT).show();
        } else {
            listener.updateWaitingListCapacity(Integer.parseInt(value));
        }
    }
}
