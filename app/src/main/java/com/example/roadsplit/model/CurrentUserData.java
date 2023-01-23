package com.example.roadsplit.model;

import com.example.roadsplit.reponses.ReiseResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;

public class CurrentUserData extends Observable {
    private Reisender currentUser;
    private Reise currentReise;
    private ReiseResponse currentReiseResponse;
    private ReiseResponse[] currentReiseRepons;

    public Reisender getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Reisender currentUser) {
        this.currentUser = currentUser;
        setChanged();
        notifyObservers();
    }

    public ReiseResponse getCurrentReiseResponse() {
        return currentReiseResponse;
    }

    public void setCurrentReiseResponse(ReiseResponse currentReiseResponse) {
        this.currentReiseResponse = currentReiseResponse;
        setChanged();
        notifyObservers();
    }

    public ReiseResponse[] getCurrentReiseReponses() {
        return currentReiseRepons;
    }
    public List<ReiseResponse> getCurrentReiseReponsesAsList() {
        return Arrays.asList(currentReiseRepons);
    }

    public void setCurrentReiseReponses(ReiseResponse[] currentReiseRepons) {
        this.currentReiseRepons = currentReiseRepons;
        setChanged();
        notifyObservers();
    }

    public Reise getCurrentReise() {
        return currentReise;
    }

    public void setCurrentReise(Reise currentReise) {
        this.currentReise = currentReise;
        setChanged();
        notifyObservers();
    }
}
