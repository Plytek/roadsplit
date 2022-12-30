package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;

public class ReiseReponse {
    private String message;
    private Reise reise;
    private Reisender reisender;

    public ReiseReponse() {
    }

    public ReiseReponse(String message, Reise reise, Reisender reisender) {
        this.message = message;
        this.reise = reise;
        this.reisender = reisender;
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

    public Reisender getReisender() {
        return reisender;
    }

    public void setReisender(Reisender reisender) {
        this.reisender = reisender;
    }
}
