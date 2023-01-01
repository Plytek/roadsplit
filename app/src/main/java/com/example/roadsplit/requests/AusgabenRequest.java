package com.example.roadsplit.requests;

import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;

public class AusgabenRequest {
    private Reisender reisender;
    private Reise reise;

    public AusgabenRequest(Reisender reisender, Reise reise) {
        this.reisender = reisender;
        this.reise = reise;
    }

    public AusgabenRequest() {
    }

    public Reisender getReisender() {
        return reisender;
    }

    public void setReisender(Reisender reisender) {
        this.reisender = reisender;
    }

    public Reise getReise() {
        return reise;
    }

    public void setReise(Reise reise) {
        this.reise = reise;
    }
}
