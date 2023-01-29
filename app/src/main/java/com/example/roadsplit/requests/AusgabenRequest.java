package com.example.roadsplit.requests;

import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;

public class AusgabenRequest {
    private Reise reise;
    private Stop stop;
    private Ausgabe ausgabe;

    public AusgabenRequest() {
    }

    public AusgabenRequest(Reise reise, Stop stop, Ausgabe ausgabe) {
        this.reise = reise;
        this.stop = stop;
        this.ausgabe = ausgabe;
    }

    public Reise getReise() {
        return reise;
    }

    public void setReise(Reise reise) {
        this.reise = reise;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public Ausgabe getAusgabe() {
        return ausgabe;
    }

    public void setAusgabe(Ausgabe ausgabe) {
        this.ausgabe = ausgabe;
    }
}
