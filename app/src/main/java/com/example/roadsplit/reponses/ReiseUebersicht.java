package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reise;

import java.math.BigDecimal;

public class ReiseUebersicht {
    private Reise reise;
    private String reiseersteller;
    private int anzahlReisende;
    private BigDecimal gesamtAusgaben;
    private BigDecimal budget;

    public ReiseUebersicht() {
    }

    public ReiseUebersicht(Reise reise, String reiseersteller, int anzahlReisende, BigDecimal gesamtAusgaben, BigDecimal budget) {
        this.reise = reise;
        this.reiseersteller = reiseersteller;
        this.anzahlReisende = anzahlReisende;
        this.gesamtAusgaben = gesamtAusgaben;
        this.budget = budget;
    }

    public Reise getReise() {
        return reise;
    }

    public void setReise(Reise reise) {
        this.reise = reise;
    }

    public String getReiseersteller() {
        return reiseersteller;
    }

    public void setReiseersteller(String reiseersteller) {
        this.reiseersteller = reiseersteller;
    }

    public int getAnzahlReisende() {
        return anzahlReisende;
    }

    public void setAnzahlReisende(int anzahlReisende) {
        this.anzahlReisende = anzahlReisende;
    }

    public BigDecimal getGesamtAusgaben() {
        return gesamtAusgaben;
    }

    public void setGesamtAusgaben(BigDecimal gesamtAusgaben) {
        this.gesamtAusgaben = gesamtAusgaben;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
}
