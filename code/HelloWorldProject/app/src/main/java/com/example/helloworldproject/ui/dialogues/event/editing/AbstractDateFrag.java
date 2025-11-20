package com.example.helloworldproject.ui.dialogues.event.editing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Event;

import java.util.Calendar;
import java.util.Objects;

public abstract class AbstractDateFrag extends AbstractEditFrag {
    private final String preSetTimestamp;
    public AbstractDateFrag(String title, String preSetTimestamp) {
        super(title);
        this.preSetTimestamp = preSetTimestamp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.frag_event_edit_date, null);
        CalendarView datePicker = view.findViewById(R.id.date_picker_view);
        datePicker.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(year, month, dayOfMonth);
            datePicker.setDate(cal.getTimeInMillis(), false, true);
        });
        long date = 0;
        try {
            date = Objects.requireNonNull(
                Event.DATE_FORMATTER.parse(preSetTimestamp)
            ).getTime();
        } catch (Exception ignored) {  }
        datePicker.setDate(date);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
            .setTitle(dialogTitle)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", (dialog, which) ->
                positiveCallback(dialog, which, datePicker.getDate())
            )
            .create();
    }

    protected abstract void positiveCallback(DialogInterface dialog, int which, long dateInMilli);
}
