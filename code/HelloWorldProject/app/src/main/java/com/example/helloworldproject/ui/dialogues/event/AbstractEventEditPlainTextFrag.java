package com.example.helloworldproject.ui.dialogues.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AbstractEventEditPlainTextFrag extends AbstractEventEditFrag {
    @LayoutRes
    private final int resId;

    @IdRes
    private final int viewId;

    private final String hint;

    public AbstractEventEditPlainTextFrag(
        String dialogTitle,
        @LayoutRes int resId,
        @IdRes int viewId,
        String hint
    ) {
        super(dialogTitle);
        this.resId = resId;
        this.viewId = viewId;
        this.hint = hint;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(resId, null);
        EditText inputField = view.findViewById(viewId);
        inputField.setHint(hint);
        inputField.requestFocus();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
            .setTitle(dialogTitle)
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
