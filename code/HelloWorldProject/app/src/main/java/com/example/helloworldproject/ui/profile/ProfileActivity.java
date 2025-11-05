package com.example.helloworldproject.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.model.UserGroup;
import com.example.helloworldproject.util.DeviceId;

/** Create/Update profile in a single screen. */
public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone;
    private Spinner userGroupSpinner;
    private Button btnSave;
    private ProgressBar progress;

    private ProfileRepository repo;
    private String deviceId; // document ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        userGroupSpinner = findViewById(R.id.user_group_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_text_item, UserGroup.getNameList()
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_text_item);
        userGroupSpinner.setAdapter(spinnerAdapter);
        btnSave = findViewById(R.id.btn_save);
        progress = findViewById(R.id.progress);

        repo = new ProfileRepository();

        setLoading(true);
        DeviceId.getOrFetch(this, new DeviceId.DeviceIdCallback() {
            @Override public void onSuccess(String id) {
                deviceId = id;
                // Try to load existing profile (for update).
                repo.loadByDeviceId(deviceId, new ProfileRepository.LoadCallback() {
                    @Override public void onLoaded(Profile p) {
                        setLoading(false);
                        if (p != null) {
                            etName.setText(p.getName());
                            etEmail.setText(p.getEmail());
                            etPhone.setText(p.getPhone());
                            userGroupSpinner.setSelection(p.getUserGroup().ordinal());
                        }
                    }

                    @Override public void onNotFound() {
                        setLoading(false); // new user: keep fields empty
                    }

                    @Override public void onError(Exception e) {
                        setLoading(false);
                        Toast.makeText(ProfileActivity.this, "Failed to load: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, "Failed to get device ID: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    etName.setError("Please enter your name");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Please enter your email");
                    return;
                }

                setLoading(true);
                Profile p = new Profile(
                    deviceId, deviceId, name, email,
                    TextUtils.isEmpty(phone) ? null : phone,
                    UserGroup.valueOf(
                        spinnerAdapter.getItem(
                            userGroupSpinner.getSelectedItemPosition()
                        )
                    )
                );
                repo.saveOrUpdate(p, new ProfileRepository.CompleteCallback() {
                    @Override public void onComplete() {
                        setLoading(false);
                        Toast.makeText(ProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override public void onError(Exception e) {
                        setLoading(false);
                        Toast.makeText(ProfileActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
        etName.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPhone.setEnabled(!loading);
    }
}
