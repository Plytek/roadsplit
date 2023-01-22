package com.example.roadsplit.model;

import com.example.roadsplit.reponses.ReiseReponse;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;

public class CurrentUserData extends Observable {
    private Reisender currentUser;
    private Reise currentReise;
    private ReiseReponse currentReiseResponse;
    private ReiseReponse[] currentReiseReponses;

    public Reisender getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Reisender currentUser) {
        this.currentUser = currentUser;
        setChanged();
        notifyObservers();
    }

    public ReiseReponse getCurrentReiseResponse() {
        return currentReiseResponse;
    }

    public void setCurrentReiseResponse(ReiseReponse currentReiseResponse) {
        this.currentReiseResponse = currentReiseResponse;
        setChanged();
        notifyObservers();
    }

    public ReiseReponse[] getCurrentReiseReponses() {
        return currentReiseReponses;
    }
    public List<ReiseReponse> getCurrentReiseReponsesAsList() {
        return Arrays.asList(currentReiseReponses);
    }

    public void setCurrentReiseReponses(ReiseReponse[] currentReiseReponses) {
        this.currentReiseReponses = currentReiseReponses;
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
