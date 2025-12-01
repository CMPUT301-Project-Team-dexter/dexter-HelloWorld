package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.helloworldproject.R;
import com.example.helloworldproject.databinding.ActivityHomeBinding;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.ui.fragments.AccountFragment;
import com.example.helloworldproject.ui.fragments.AdminConsoleFragment;
import com.example.helloworldproject.ui.fragments.HomeEventCardListFragment;
import com.example.helloworldproject.util.CurrentProfile;
import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CurrentProfile.isInitialized()) {
            startActivity(LoginActivity.newIntent(this));
            finish();
            return;
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        Profile currentUser = CurrentProfile.get();
        Snackbar.make(
            binding.homeNavView,
            "Logged in as " + currentUser.getUserGroup().name() + ": " + currentUser.getName(),
            Snackbar.LENGTH_LONG
        ).show();

        binding.homeNavView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                if (CurrentProfile.isAdmin()) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AdminConsoleFragment())
                        .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeEventCardListFragment())
                        .commit();
                }
                return true;
//            } else if (id == R.id.nav_events) {
//                getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new AllEventsFragment())
//                    .commit();
//                return true;
            } else if (id == R.id.nav_account) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AccountFragment())
                    .commit();
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            binding.homeNavView.setSelectedItemId(R.id.nav_home);
        }
    }
}
