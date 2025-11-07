package com.example.helloworldproject.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.util.CurrentProfile;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchOptOut;
    private View progress;
    private final ProfileRepository repo = new ProfileRepository();
    private Profile workingProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchOptOut = findViewById(R.id.switch_opt_out);
        progress = findViewById(R.id.progress);

        Profile cp;
        try {
            cp = CurrentProfile.get();
        } catch (Exception e) {
            Toast.makeText(this, "No current profile. Please log in first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setLoading(true);
        repo.loadByDeviceId(cp.getDeviceId(), new ProfileRepository.LoadCallback() {
            @Override public void onLoaded(Profile profile) {
                setLoading(false);
                if (profile == null) {
                    Toast.makeText(SettingsActivity.this, "Profile is null.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                workingProfile = profile;
                boolean optOut = profile.getNotificationOptOut() != null && profile.getNotificationOptOut();
                switchOptOut.setChecked(optOut);
                bindSwitch();
            }
            @Override public void onNotFound() {
                setLoading(false);
                Toast.makeText(SettingsActivity.this, "Profile not found. Please register first.", Toast.LENGTH_LONG).show();
                finish();
            }
            @Override public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(SettingsActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void bindSwitch() {
        switchOptOut.setOnCheckedChangeListener((btn, isChecked) -> {
            if (workingProfile == null) return;
            setLoading(true);
            workingProfile.setNotificationOptOut(isChecked);
            repo.saveOrUpdate(workingProfile, new ProfileRepository.CompleteCallback() {
                @Override public void onComplete() {
                    setLoading(false);
                    CurrentProfile.init(workingProfile);
                    Toast.makeText(SettingsActivity.this, isChecked ? "Notifications turned OFF." : "Notifications turned ON.", Toast.LENGTH_SHORT).show();
                }
                @Override public void onError(Exception e) {
                    setLoading(false);
                    switchOptOut.setOnCheckedChangeListener(null);
                    switchOptOut.setChecked(!isChecked);
                    bindSwitch();
                    Toast.makeText(SettingsActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        switchOptOut.setEnabled(!loading);
    }
}
