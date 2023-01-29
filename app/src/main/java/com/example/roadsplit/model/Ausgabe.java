package com.example.roadsplit.model;

import java.math.BigDecimal;
import java.util.List;

public class Ausgabe {
    private long id;
    private long zahler;
    private String zahlername;
    private AusgabenTyp ausgabenTyp;
    private List<AusgabenSumme> ausgabenSumme;


    public Ausgabe(long id, long zahler, String zahlername, AusgabenTyp ausgabenTyp, List<AusgabenSumme> ausgabenSumme) {
        this.id = id;
        this.zahler = zahler;
        this.zahlername = zahlername;
        this.ausgabenTyp = ausgabenTyp;
        this.ausgabenSumme = ausgabenSumme;
    }

    public Ausgabe() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getZahler() {
        return zahler;
    }

    public void setZahler(long zahler) {
        this.zahler = zahler;
    }

    public String getZahlername() {
        return zahlername;
    }

    public void setZahlername(String zahlername) {
        this.zahlername = zahlername;
    }

    public AusgabenTyp getAusgabenTyp() {
        return ausgabenTyp;
    }

    public void setAusgabenTyp(AusgabenTyp ausgabenTyp) {
        this.ausgabenTyp = ausgabenTyp;
    }

    public List<AusgabenSumme> getAusgabenSumme() {
        return ausgabenSumme;
    }

    public void setAusgabenSumme(List<AusgabenSumme> ausgabenSumme) {
        this.ausgabenSumme = ausgabenSumme;
    }
}
