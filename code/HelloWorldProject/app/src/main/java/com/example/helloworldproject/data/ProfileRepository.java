package com.example.helloworldproject.data;

import com.example.helloworldproject.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Firestore repository for Profile. */
public class ProfileRepository {
    /** Callback for loading a list of profiles (for admin browse). */
    public interface ListCallback {
        void onLoaded(List<Profile> profiles);
        void onError(Exception e);
    }


    public interface LoadCallback {
        void onLoaded(Profile profile);
        void onNotFound(); // document does not exist
        void onError(Exception e);
    }

    public interface CompleteCallback {
        void onComplete();
        void onError(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadByDeviceId(String deviceId, final LoadCallback cb) {
        db.collection("profiles").document(deviceId).get()
                .addOnSuccessListener(ds -> {
                    if (ds.exists()) {
                        Profile p = ds.toObject(Profile.class);
                        cb.onLoaded(p);
                    } else {
                        cb.onNotFound();
                    }
                })
                .addOnFailureListener(cb::onError);
    }

    /** Create or update (merge) the profile by deviceId. */
    public void saveOrUpdate(Profile profile, final CompleteCallback cb) {
        String docId = profile.getDeviceId();
        profile.setId(docId);
        db.collection("profiles")
                .document(docId)
                .set(profile, SetOptions.merge())
                .addOnSuccessListener(unused -> cb.onComplete())
                .addOnFailureListener(cb::onError);
    }

    public void deleteProfile(Profile profile, final CompleteCallback cb) {
        String thisId = profile.getId();
        String thisName = profile.getName();
        WriteBatch batch = db.batch();

        // 1. Delete the profile document itself
        DocumentReference profileRef = db.collection("profiles").document(thisId);
        batch.delete(profileRef);

        // 2. Get all 'events' documents to process deletions
        db.collection("events")
                .get()
                .addOnSuccessListener(snap -> {
                    List<Task<QuerySnapshot>> subcollectionTasks = new ArrayList<>();

                    for (QueryDocumentSnapshot eventDoc : snap) {
                        DocumentReference eventRef = eventDoc.getReference();

                        String creator = eventDoc.getString("creator");
                        boolean isCreator = thisName.equals(creator);

                        if (isCreator) {
                            // If the profile is the creator, mark the whole event document for deletion
                            batch.delete(eventRef);

                            Task<QuerySnapshot> waitlistTask = eventRef.collection("waitlist").get();

                            Task<QuerySnapshot> invitesTask = eventRef.collection("invites").get();

                            subcollectionTasks.add(waitlistTask);
                            subcollectionTasks.add(invitesTask);
                        } else {
                            Query waitlistQuery = eventRef.collection("waitlist").whereEqualTo("profileId", thisId);
                            Task<QuerySnapshot> waitlistTask = waitlistQuery.get();

                            Query invitesQuery = eventRef.collection("invites").whereEqualTo("profileId", thisId);
                            Task<QuerySnapshot> invitesTask = invitesQuery.get();

                            subcollectionTasks.add(waitlistTask);
                            subcollectionTasks.add(invitesTask);
                        }
                    }

                    // 3. Wait for all subcollection fetches to complete
                    Tasks.whenAllComplete(subcollectionTasks).addOnSuccessListener(completedTasks -> {

                        for (Task<?> t : completedTasks) {
                            if (!t.isSuccessful()) {
                                cb.onError(t.getException());
                                return;
                            }

                            QuerySnapshot subSnap = (QuerySnapshot) t.getResult();

                            for (DocumentSnapshot subDoc : subSnap.getDocuments()) {
                                batch.delete(subDoc.getReference());
                            }
                        }

                        batch.commit().addOnSuccessListener(unused -> cb.onComplete())
                                .addOnFailureListener(cb::onError);

                    }).addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    /**
     * Load all profiles in the system (for admin browse).
     */
    public void loadAllProfiles(final ListCallback cb) {
        db.collection("profiles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Profile> result = new ArrayList<>();
                    querySnapshot.getDocuments().forEach(doc -> {
                        Profile p = doc.toObject(Profile.class);
                        if (p != null) {
                            // Ensure the in-memory model has its ID set to the document ID
                            p.setId(doc.getId());
                            result.add(p);
                        }
                    });
                    cb.onLoaded(result);
                })
                .addOnFailureListener(cb::onError);
    }

}
