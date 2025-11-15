package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.DialogInterface;
import android.widget.EditText;

import com.example.helloworldproject.R;

public class DetailFrag extends AbstractPlainTextFrag {
    public DetailFrag() {
        super(
            "Event Detail",
            R.layout.event_edit_multi_line_text_frag,
            R.id.edit_multi_line_input,
            "sample\ntext"
        );
    }

    @Override
    protected void positiveCallback(DialogInterface dialog, int which, EditText inputField) {
        String detailStr = inputField.getText().toString().strip();
        listener.updateDetail(detailStr.isEmpty() ? "N/A" : detailStr);
    }
}
