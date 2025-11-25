package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.ui.fragments.RegisterHistoryFragment;

public class RegisterHistoryActivity extends AppCompatActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, RegisterHistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, new RegisterHistoryFragment())
                    .commit();
        }
    }
}
