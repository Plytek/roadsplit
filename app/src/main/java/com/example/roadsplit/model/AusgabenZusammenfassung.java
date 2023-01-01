package com.example.roadsplit.model;

import java.util.HashMap;
import java.util.List;

public class AusgabenZusammenfassung {
    private String message;
    private Reisender zahler;
    private HashMap<Reisender, List<Ausgabe>> bezahltVon = new HashMap<>();
    private HashMap<Reisender, List<Ausgabe>> bezahltFuer = new HashMap<>();

    public AusgabenZusammenfassung(String message, Reisender zahler, HashMap<Reisender, List<Ausgabe>> bezahltVon, HashMap<Reisender, List<Ausgabe>> bezahltFuer) {
        this.message = message;
        this.zahler = zahler;
        this.bezahltVon = bezahltVon;
        this.bezahltFuer = bezahltFuer;
    }

    public AusgabenZusammenfassung() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Reisender getZahler() {
        return zahler;
    }

    public void setZahler(Reisender zahler) {
        this.zahler = zahler;
    }

    public HashMap<Reisender, List<Ausgabe>> getBezahltVon() {
        return bezahltVon;
    }

    public void setBezahltVon(HashMap<Reisender, List<Ausgabe>> bezahltVon) {
        this.bezahltVon = bezahltVon;
    }

    public HashMap<Reisender, List<Ausgabe>> getBezahltFuer() {
        return bezahltFuer;
    }

    public void setBezahltFuer(HashMap<Reisender, List<Ausgabe>> bezahltFuer) {
        this.bezahltFuer = bezahltFuer;
    }


    @Override
    public String toString() {
        return "AusgabenZusammenfassung{" +
                "message='" + message + '\'' +
                ", zahler=" + zahler +
                ", bezahltVon=" + bezahltVon +
                ", bezahltFuer=" + bezahltFuer +
                '}';
    }
}
