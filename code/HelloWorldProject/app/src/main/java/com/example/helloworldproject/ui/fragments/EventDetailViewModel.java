package com.example.helloworldproject.ui.fragments;// File: EventDetailViewModel.java

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helloworldproject.ui.fragments.EntrantState;

public class EventDetailViewModel extends ViewModel {

    public EventDetailViewModel() {
        entrantState.setValue(EntrantState.UNRELATED);
        updateButtonText(entrantState.getValue());
    }

    private final MutableLiveData<EntrantState> entrantState = new MutableLiveData<>();

    private final MutableLiveData<String> buttonText = new MutableLiveData<>();

    private final MutableLiveData<Boolean> joinWaitlistFlag = new MutableLiveData<>(false);

    public LiveData<EntrantState> getEntrantState() {
        return entrantState;
    }

    public LiveData<String> getButtonText() {
        return buttonText;
    }

    public void loadState(EntrantState initialState) {
        entrantState.setValue(initialState);
        updateButtonText(initialState);
    }

    // Logic to map state to button text (removed from Fragment!)
    private void updateButtonText(EntrantState status) {
        switch(status) {
            case CANCELLED:
                buttonText.setValue("WAIT FOR NEXT TIME");
                break;
            case UNRELATED:
                buttonText.setValue("JOIN");
                break;
            case ACCEPTED:
                buttonText.setValue("CANCEL INVITATION");
                break;
            case WAITLISTED:
                buttonText.setValue("LEAVE");
                break;
        }
    }

    // Event Handler (The click logic is here, not in the Fragment/Activity)
    public void onButtonClicked() {
        EntrantState currentStatus = entrantState.getValue();
        switch (currentStatus) {
            case UNRELATED:
                updateButtonText(EntrantState.WAITLISTED);
                requestJoinWaitlist();
                entrantState.setValue(EntrantState.WAITLISTED);
            default:
                ;
        }
        this.updateButtonText(entrantState.getValue());
    }

    public void onButton2Clicked() {
        ;
    }

    public LiveData<Boolean> getJoinWaitlistFlag() {
        return joinWaitlistFlag;
    }

    public void requestJoinWaitlist() {
        joinWaitlistFlag.setValue(true); // signal Activity to act
    }

    public void resetFlag() {
        joinWaitlistFlag.setValue(false); // reset after handling
    }
}