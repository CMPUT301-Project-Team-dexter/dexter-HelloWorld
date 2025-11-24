package com.example.helloworldproject.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.ui.activities.HomeActivity;
import com.example.helloworldproject.ui.activities.LoginActivity;
import com.example.helloworldproject.ui.activities.ProfileEditActivity;
import com.example.helloworldproject.ui.activities.SettingsActivity;
import com.example.helloworldproject.util.CurrentProfile;

public class AccountFragment extends Fragment {

    private final ProfileRepository profileRepo = new ProfileRepository();

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

        Button deleteBtn = view.findViewById(R.id.delete_account_button);
        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(v ->
                    deleteProfileButtonFunction()
            );
        }

        return view;
    }

    private void deleteProfileButtonFunction() {
        profileRepo.deleteProfile(CurrentProfile.get(), new ProfileRepository.CompleteCallback() {
            @Override
            public void onComplete() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure you would like to delete this account permanently?")
                        .setPositiveButton("Affirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(requireContext(), "Profile successfully deleted.", Toast.LENGTH_LONG).show();
                        startActivity(LoginActivity.newIntent(requireContext()));
                        requireActivity().finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create().show();
            }

            @Override
            public void onError(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
