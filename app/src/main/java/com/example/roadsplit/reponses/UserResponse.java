package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reisender;

public class UserResponse {
        private String message;
    private Reisender reisender;

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
}
