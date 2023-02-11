package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reisender;

import java.util.List;

public class UserResponse {
    private String message;
    private Reisender reisender;
    private List<ReiseUebersicht> reisen;

    public UserResponse(String message, Reisender reisender, List<ReiseUebersicht> reisen) {
        this.message = message;
        this.reisender = reisender;
        this.reisen = reisen;
    }

    public UserResponse() {
    }

    public UserResponse(String message, Reisender reisender) {
        this.message = message;
        this.reisender = reisender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Reisender getReisender() {
        return reisender;
    }

    public void setReisender(Reisender reisender) {
        this.reisender = reisender;
    }

    public List<ReiseUebersicht> getReisen() {
        return reisen;
    }

    public void setReisen(List<ReiseUebersicht> reisen) {
        this.reisen = reisen;
    }
}
