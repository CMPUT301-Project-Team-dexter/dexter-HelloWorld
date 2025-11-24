package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.helloworldproject.databinding.FragAdminConsoleBinding;
import com.example.helloworldproject.ui.activities.event.AdminEventListActivity;

public class AdminConsoleFragment extends Fragment {
    FragAdminConsoleBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragAdminConsoleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.adminConsToolbar);
        // TODO: bind onclick for other buttons
        binding.adminConsAllEventsButton.setOnClickListener(
            v -> startActivity(AdminEventListActivity.newIntent(getContext()))
        );
    }
}
