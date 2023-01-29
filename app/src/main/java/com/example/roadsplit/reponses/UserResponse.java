package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reisender;

public class UserResponse {
    private String message;
    private Reisender reisender;
    private long timestamp;

    public UserResponse(String message, Reisender reisender, long timestamp) {
        this.message = message;
        this.reisender = reisender;
        this.timestamp = timestamp;
    }

    public UserResponse() {
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
