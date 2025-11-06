package com.example.helloworldproject.ui.dialogues.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.helloworldproject.R;

public abstract class EventEditPlainTextFrag extends DialogFragment {
    private final String title;
    private final String hint;
    protected EventEditListener listener;

    public EventEditPlainTextFrag(String title, String hint) {
        this.title = title;
        this.hint = hint;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.event_edit_text_frag, null);
        EditText inputField = view.findViewById(R.id.edit_text_input);
        inputField.setHint(hint);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
            .setTitle(title)
            .setNegativeButton("Cancel", null)
            .setPositiveButton(
                "OK",
                (dialog, which) ->
                    positiveCallback(dialog, which, inputField)
            )
            .create();
    }

    protected abstract void positiveCallback(DialogInterface dialog, int which, EditText inputField);
}
