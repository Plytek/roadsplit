package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reise;

public class ReiseReponse {
    private String message;
    private Reise reise;

    public ReiseReponse(String message, Reise reise) {
        this.message = message;
        this.reise = reise;
    }

    public ReiseReponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Reise getReise() {
        return reise;
    }

    public void setReise(Reise reise) {
        this.reise = reise;
    }
}
