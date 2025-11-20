package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helloworldproject.R;

public class TitleFrag extends AbstractPlainTextFrag {
    public TitleFrag() {
        super(
            "Title",
            R.layout.frag_event_edit_text,
            R.id.edit_text_input,
            "Title text"
        );
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
