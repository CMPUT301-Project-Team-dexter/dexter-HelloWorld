package com.example.helloworldproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.helloworldproject.R;
import com.example.helloworldproject.ui.activities.ProfileEditActivity;
import com.example.helloworldproject.ui.activities.SettingsActivity;

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);

        Button profileBtn = view.findViewById(R.id.profile_button);
        if (profileBtn != null) {
            profileBtn.setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), ProfileEditActivity.class))
            );
        }

        Button settingsBtn = view.findViewById(R.id.settings_button);
        if (settingsBtn != null) {
            settingsBtn.setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), SettingsActivity.class))
            );
        }

        return view;
    }
}
