package com.example.helloworldproject.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.NotificationRepository;
import com.example.helloworldproject.model.NotificationRecord;
import com.example.helloworldproject.ui.utils.NotificationAdapter;
import com.example.helloworldproject.util.CurrentProfile;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class NotificationListActivity extends AppCompatActivity {

    private final NotificationRepository notificationRepository = new NotificationRepository();
    private ListenerRegistration notificationListener;
    private NotificationAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        Toolbar toolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(
            v -> getOnBackPressedDispatcher().onBackPressed()
        );

        RecyclerView recyclerView = findViewById(R.id.notification_list);
        emptyView = findViewById(R.id.notifications_empty);

        adapter = new NotificationAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        listenForNotifications();
    }

    @Override
    protected void onDestroy() {
        if (notificationListener != null) {
            notificationListener.remove();
            notificationListener = null;
        }
        super.onDestroy();
    }

    private void listenForNotifications() {
        String profileId = CurrentProfile.get().getId();
        notificationListener = notificationRepository.observeNotifications(
            profileId,
            new NotificationRepository.NotificationListListener() {
                @Override
                public void onLoaded(List<NotificationRecord> notifications) {
                    adapter.setItems(notifications);
                    updateEmptyState(notifications.isEmpty());
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(
                        NotificationListActivity.this,
                        "Failed to load notifications: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        );
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}