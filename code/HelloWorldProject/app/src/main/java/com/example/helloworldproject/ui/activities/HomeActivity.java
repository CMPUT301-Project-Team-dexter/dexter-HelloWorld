package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.ui.fragments.AccountFragment;
import com.example.helloworldproject.ui.fragments.AllEventsFragment;
import com.example.helloworldproject.ui.fragments.HomeEventCardListFragment;
import com.example.helloworldproject.util.CurrentProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Profile currentUser = CurrentProfile.get();
        Snackbar.make(
                findViewById(R.id.home_nav_view),
                "Log in as " + currentUser.getUserGroup().name() + " " + currentUser.getName(),
                Snackbar.LENGTH_LONG
        ).show();

        BottomNavigationView nav = findViewById(R.id.home_nav_view);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeEventCardListFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_events) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AllEventsFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_account) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AccountFragment())
                        .commit();
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_home);
        }
    }
}
