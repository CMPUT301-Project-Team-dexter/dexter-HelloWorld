package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;
import com.example.helloworldproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {
    public interface FilterListener {
        void onFilterApplied(long selectedDate, List<String> selectedInterests);
        void onFilterCleared();
    }

    private FilterListener listener;
    private long selectedDate = 0;

    public FilterBottomSheetFragment(FilterListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_sheet, container, false);

        CalendarView calendar = view.findViewById(R.id.filter_calendar);
        ChipGroup chipGroup = view.findViewById(R.id.filter_chip_group);
        View applyBtn = view.findViewById(R.id.btn_apply_filter);
        View clearBtn = view.findViewById(R.id.btn_clear_filter);

        // saves date selection
        calendar.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            selectedDate = c.getTimeInMillis();
        });

        // button logic for "apply"
        applyBtn.setOnClickListener(v -> {
            // Collect selected tags
            List<String> interests = new ArrayList<>();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    interests.add(chip.getText().toString());
                }
            }

            // Send data back
            if (listener != null) {
                listener.onFilterApplied(selectedDate, interests);
            }
            dismiss();
        });

        // button logic for "clear"
        clearBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterCleared();
            }
            dismiss();
        });

        return view;
    }
}