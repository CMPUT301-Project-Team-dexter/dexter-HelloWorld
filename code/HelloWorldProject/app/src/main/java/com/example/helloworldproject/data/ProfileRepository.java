package com.example.helloworldproject.data;

import com.example.helloworldproject.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworldproject.model.UserGroup;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
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
    public interface ProfileListener {
        void onLoaded(Profile profile);
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
        String thisDeviceId = profile.getDeviceId();
        WriteBatch batch = db.batch();

        DocumentReference profileDeviceIdRef = db.collection("profiles").document(thisDeviceId);
        batch.delete(profileDeviceIdRef);

        db.collection("events")
                .get()
                .addOnSuccessListener(snap -> {
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    List<QueryDocumentSnapshot> parentEvents = new ArrayList<>(); // keep track of parent events

                    for (QueryDocumentSnapshot d : snap) {
                        parentEvents.add(d); // save the parent event
                        tasks.add(d.getReference().collection("waitlist").get());
                        tasks.add(d.getReference().collection("invites").get());
                    }

                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {

                        for (int i = 0; i < results.size(); i++) {
                            QuerySnapshot subSnap = (QuerySnapshot) results.get(i);

                            for (DocumentSnapshot dd : subSnap.getDocuments()) {
                                String profileId = dd.getString("profileId");
                                if (thisDeviceId.equals(profileId)) {
                                    batch.delete(dd.getReference());
                                }
                            }
                        }
                        batch.commit();
                        cb.onComplete();
                    });
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

    public void loadProfileById(@NonNull String profileId,
                                @NonNull final ProfileListener listener) {
        db.collection("profiles")
                .document(profileId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Profile p = fromDoc(doc);
                        listener.onLoaded(p);
                    } else {
                        listener.onError(new IllegalStateException(
                                "Profile not found for id: " + profileId));
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    private Profile fromDoc(DocumentSnapshot doc) {
        Profile p = new Profile();
        p.setId(doc.getId());
        p.setName(doc.getString("name"));
        // other fields...
        p.setDeviceId(doc.getString("deviceId")); // <<< new line
        return p;
    }

    private Map<String, Object> toMap(Profile profile) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", profile.getName());
        // other fields...
        map.put("deviceId", profile.getDeviceId()); // <<< new line
        return map;
    }

}
