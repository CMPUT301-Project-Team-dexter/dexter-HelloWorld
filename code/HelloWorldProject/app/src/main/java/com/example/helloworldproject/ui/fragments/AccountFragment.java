package com.example.helloworldproject.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.ui.activities.HomeActivity;
import com.example.helloworldproject.ui.activities.LoginActivity;
import com.example.helloworldproject.ui.activities.ProfileEditActivity;
import com.example.helloworldproject.ui.activities.RegisterHistoryActivity;
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

        Button regHisBtn = view.findViewById(R.id.register_history_button1);
        if (regHisBtn != null) {
            regHisBtn.setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), RegisterHistoryActivity.class))
            );
        }

        Button logOutBtn = view.findViewById(R.id.logout_button);
        if (logOutBtn != null) {
            logOutBtn.setOnClickListener(v -> {
                Intent intent = LoginActivity.newIntent(requireContext());
                intent.putExtra("skip_auto_login", true);
                startActivity(intent);
                requireActivity().finish();
            }
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are you sure you would like to delete this profile permanently?")
                .setTitle("Profile Deletion Warning")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        profileRepo.deleteProfile(CurrentProfile.get(), new ProfileRepository.CompleteCallback() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(requireContext(), "Profile successfully deleted.", Toast.LENGTH_LONG).show();
                                startActivity(LoginActivity.newIntent(requireContext()));
                                requireActivity().finish();
                            }

                            @Override
                            public void onError(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).create().show();
    }
}
