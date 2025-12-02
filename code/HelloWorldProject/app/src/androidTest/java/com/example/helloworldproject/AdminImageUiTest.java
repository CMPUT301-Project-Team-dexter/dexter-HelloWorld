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

import com.example.helloworldproject.ui.activities.AllImagesActivity;
import com.example.helloworldproject.ui.activities.ImageDetailActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminImageUiTest {

    /**
     * Smoke test for the AllImagesActivity UI.
     * <p>
     * Launches the activity and verifies that the toolbar and the RecyclerView
     * used for browsing images are displayed.
     */
    @Test
    public void allImagesActivity_showsToolbarAndGrid() {
        Context appContext = ApplicationProvider.getApplicationContext();
        ActivityScenario<AllImagesActivity> scenario =
            ActivityScenario.launch(AllImagesActivity.newIntent(appContext));

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    /**
     * UI-level test for ImageDetailActivity:
     * <p>
     * - Launches the activity with a fake event ID and image URL.
     * - Verifies that the image view and delete button are visible.
     * - Clicks the delete button and checks that the confirmation dialog appears.
     * <p>
     * We do NOT press the dialog's "Delete" button, so performDelete()
     * and Firestore are never hit.
     */
    @Test
    public void imageDetailActivity_showsDeleteConfirmationDialog() {
        Context appContext = ApplicationProvider.getApplicationContext();

        ActivityScenario<ImageDetailActivity> scenario =
            ActivityScenario.launch(
                ImageDetailActivity.newIntent(
                    appContext,
                    "test-event-id",
                    "https://example.com/test.jpg"
                )
            );

        // Detail screen should show image + delete button
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

        // Click delete, expect the confirmation dialog (but do not confirm)
        onView(withId(R.id.delete_button)).perform(click());

        onView(withText("Remove image")).check(matches(isDisplayed()));
        onView(withText("Are you sure you want to remove this image from the event?"))
            .check(matches(isDisplayed()));
    }
}
