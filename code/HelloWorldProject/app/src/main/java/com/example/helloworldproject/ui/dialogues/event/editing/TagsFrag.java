package com.example.helloworldproject.ui.dialogues.event.editing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.helloworldproject.ui.activities.event.EventEditListener;
import java.util.ArrayList;
import java.util.List;

public class TagsFrag extends DialogFragment{
    private EventEditListener listener;
    private List<String> currentTags;

    // Hardcoded list of available tags
    private final String[] AVAILABLE_TAGS = new String[]{
            "team sports", "working out", "community events", "music", "food", "culture", "art", "education"
    };

    public TagsFrag(List<String> existingTags) {
        this.currentTags = existingTags != null ? new ArrayList<>(existingTags) : new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EventEditListener) {
            listener = (EventEditListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement EventEditListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Event Tags");

        // Determine which items are already checked
        boolean[] checkedItems = new boolean[AVAILABLE_TAGS.length];
        for (int i = 0; i < AVAILABLE_TAGS.length; i++) {
            if (currentTags.contains(AVAILABLE_TAGS[i])) {
                checkedItems[i] = true;
            }
        }

        builder.setMultiChoiceItems(AVAILABLE_TAGS, checkedItems, (dialog, which, isChecked) -> {
            String tag = AVAILABLE_TAGS[which];
            if (isChecked) {
                if (!currentTags.contains(tag)) currentTags.add(tag);
            } else {
                currentTags.remove(tag);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            listener.updateTags(currentTags);
        });

        builder.setNegativeButton("Cancel", null);

        return builder.create();
    }
}
