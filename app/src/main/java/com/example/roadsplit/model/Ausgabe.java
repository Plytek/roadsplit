package com.example.roadsplit.model;

import java.math.BigDecimal;

public class Ausgabe {
    private long id;

    private AusgabenTyp ausgabenTyp;
    private BigDecimal betrag;
    private long schuldner;
    private long zahler;

    public Ausgabe(long id, AusgabenTyp ausgabenTyp, BigDecimal betrag, long schuldner, long zahler) {
        this.id = id;
        this.ausgabenTyp = ausgabenTyp;
        this.betrag = betrag;
        this.schuldner = schuldner;
        this.zahler = zahler;
    }

    public Ausgabe() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AusgabenTyp getAusgabenTyp() {
        return ausgabenTyp;
    }

    public void setAusgabenTyp(AusgabenTyp ausgabenTyp) {
        this.ausgabenTyp = ausgabenTyp;
    }

    public BigDecimal getBetrag() {
        return betrag;
    }

    public void setBetrag(BigDecimal betrag) {
        this.betrag = betrag;
    }

    public long getSchuldner() {
        return schuldner;
    }

    public void setSchuldner(long schuldner) {
        this.schuldner = schuldner;
    }

    public long getZahler() {
        return zahler;
    }

    public void setZahler(long zahler) {
        this.zahler = zahler;
    }
}
