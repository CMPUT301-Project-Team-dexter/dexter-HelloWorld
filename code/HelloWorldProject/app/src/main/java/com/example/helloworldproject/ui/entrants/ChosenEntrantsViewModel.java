package com.example.helloworldproject.ui.entrants;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.helloworldproject.data.EntrantRepository;
import com.example.helloworldproject.model.Entrant;
import java.util.ArrayList;
import java.util.List;

public class ChosenEntrantsViewModel extends ViewModel {
    private final EntrantRepository repo;
    private String currentEventId;
    private final MutableLiveData<List<Entrant>> entrantsLive = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorLive = new MutableLiveData<>(null);

    public ChosenEntrantsViewModel(@NonNull EntrantRepository repo) { this.repo = repo; }

    public LiveData<List<Entrant>> entrants() { return entrantsLive; }
    public LiveData<String> error() { return errorLive; }

    public void setEventId(String eventId) {
        if (eventId == null) return;
        if (!eventId.equals(currentEventId)) {
            currentEventId = eventId;
            reattach();
        }
    }

    private void reattach() {
        if (currentEventId == null) return;
        repo.listenChosenPending(currentEventId, new EntrantRepository.ListenCallback() {
            @Override public void onChanged(List<Entrant> list) { entrantsLive.postValue(list); }
            @Override public void onError(Exception e) { errorLive.postValue(e != null ? e.getMessage() : "Unknown error"); }
        });
    }

    @Override protected void onCleared() { super.onCleared(); repo.stop(); }
}
