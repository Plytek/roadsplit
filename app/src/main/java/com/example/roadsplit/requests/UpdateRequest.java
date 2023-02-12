package com.example.roadsplit.requests;

import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;

public class UpdateRequest {
    private Reisender reisender;
    private Reise reise;

    public UpdateRequest(Reisender reisender, Reise reise) {
        this.reisender = reisender;
        this.reise = reise;
    }

    public UpdateRequest() {
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
