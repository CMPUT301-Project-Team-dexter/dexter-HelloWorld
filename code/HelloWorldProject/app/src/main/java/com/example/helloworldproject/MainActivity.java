package com.example.helloworldproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.ui.fragments.AllEventsFragment;
import com.example.helloworldproject.ui.fragments.EventCardListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This is the fragment to be displayed upon opening the app.
        // If you want to try other fragments, replace the corresponding keywords.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventCardListFragment())
                    .commit();
        }
    }
}
