package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Profile;
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

        // This is the fragment to be displayed upon opening the app.
        // If you want to try other fragments, replace the corresponding keywords.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeEventCardListFragment())
                    .commit();
        }
    }
}
