package com.example.roadsplit.model;

import java.math.BigDecimal;

public class Ausgabe {
    private long id;

    private AusgabenTyp ausgabenTyp;
    private BigDecimal betrag;
    private boolean paidForHimself;
    private long zahler;
    private long schuldner;
    private int anzahlReisende;


    public Ausgabe(long id, AusgabenTyp ausgabenTyp, BigDecimal betrag, boolean paidForHimself, long zahler, long schuldner, int anzahlReisende) {
        this.id = id;
        this.ausgabenTyp = ausgabenTyp;
        this.betrag = betrag;
        this.paidForHimself = paidForHimself;
        this.zahler = zahler;
        this.schuldner = schuldner;
        this.anzahlReisende = anzahlReisende;
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

    public boolean isPaidForHimself() {
        return paidForHimself;
    }

    public void setPaidForHimself(boolean paidForHimself) {
        this.paidForHimself = paidForHimself;
    }

    public long getZahler() {
        return zahler;
    }

    public void setZahler(long zahler) {
        this.zahler = zahler;
    }

    public long getSchuldner() {
        return schuldner;
    }

    public void setSchuldner(long schuldner) {
        this.schuldner = schuldner;
    }

    public int getAnzahlReisende() {
        return anzahlReisende;
    }

    public void setAnzahlReisende(int anzahlReisende) {
        this.anzahlReisende = anzahlReisende;
    }

    @Override
    public String toString() {
        return "Ausgabe{" +
                "id=" + id +
                ", ausgabenTyp=" + ausgabenTyp +
                ", betrag=" + betrag +
                ", schuldner=" + schuldner +
                ", zahler=" + zahler +
                '}';
    }
}
