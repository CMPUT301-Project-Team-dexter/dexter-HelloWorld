package com.example.helloworldproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.model.UserGroup;
import com.example.helloworldproject.ui.activities.event.EventDetailActivity;
import com.example.helloworldproject.util.CurrentProfile;
import com.example.helloworldproject.util.EventCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminEventDeleteUiTest {

    @Before
    public void setUpAdminProfile() {
        // Create a minimal admin profile and set it as the current user
        Profile admin = new Profile();
        admin.setId("admin-device");
        admin.setDeviceId("admin-device");
        admin.setName("Test Admin");
        admin.setUserGroup(UserGroup.ADMIN);

        CurrentProfile.init(admin);
    }

    /**
     * Verifies that when an admin opens EventDetailActivity for an event:
     * - the admin delete button is visible
     * - clicking it shows the confirmation dialog.
     * <p>
     * We use EventCache to provide a fake event so EventDetailActivity
     * doesn't need to hit Firestore in onCreate().
     */
    @Test
    public void eventDetail_showsAdminDeleteDialog() {
        // Prepare a fake event and cache it
        Event event = new Event();
        event.setId("test-event-id");
        event.setTitle("Test Event");
        EventCache.refresh(event);

        Context appContext = ApplicationProvider.getApplicationContext();
        ActivityScenario<EventDetailActivity> scenario =
            ActivityScenario.launch(
                EventDetailActivity.newIntent(appContext, event.getId())
            );

        // As admin, delete button should be visible
        onView(withId(R.id.evt_dtl_admin_delete_btn))
            .check(matches(isDisplayed()));

        // Click delete -> confirmation dialog appears (but do not press "Delete")
        onView(withId(R.id.evt_dtl_admin_delete_btn)).perform(click());

        onView(withText("Delete event")).check(matches(isDisplayed()));
        onView(withText("Are you sure you want to delete this event?"))
            .check(matches(isDisplayed()));
    }
}
