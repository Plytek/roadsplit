package com.example.roadsplit.requests;

public class JoinRequest {
    private long reisenderID;
    private String reiseUniquename;

    public JoinRequest() {
    }

    public JoinRequest(long reisenderID, String reiseUniquename) {
        this.reisenderID = reisenderID;
        this.reiseUniquename = reiseUniquename;
    }

    public long getReisenderID() {
        return reisenderID;
    }

    public void setReisenderID(long reisenderID) {
        this.reisenderID = reisenderID;
    }

    public String getReiseUniquename() {
        return reiseUniquename;
    }

    public void setReiseUniquename(String reiseUniquename) {
        this.reiseUniquename = reiseUniquename;
    }
}
