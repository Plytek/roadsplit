package com.example.roadsplit.model;

import java.math.BigDecimal;
import java.util.List;


public class Reise {
    private Long id;
    private String name;
    private String uniquename;
    private String reiseErsteller;
    private boolean ongoing;
    private long createDate;

    private List<Stop> stops;
    private List<PacklistenItem> packliste;
    private List<AusgabenSumme> gesamtBudgetProReisender;
    private List<AusgabenSumme> gesamtAusgabeProReisender;
    private List<Schulden> schuldens;
    private List<ReiseTeilnehmer> reiseTeilnehmer;
    private BigDecimal gesamtAusgabe;
    private BigDecimal gesamtBudget;


    public Reise() {
    }

    public Reise(Long id, String name, String uniquename, String reiseErsteller, boolean ongoing, long createDate, List<Stop> stops, List<PacklistenItem> packliste, List<AusgabenSumme> gesamtBudgetProReisender, List<AusgabenSumme> gesamtAusgabeProReisender, List<Schulden> schuldens, List<ReiseTeilnehmer> reiseTeilnehmer, BigDecimal gesamtAusgabe, BigDecimal gesamtBudget) {
        this.id = id;
        this.name = name;
        this.uniquename = uniquename;
        this.reiseErsteller = reiseErsteller;
        this.ongoing = ongoing;
        this.createDate = createDate;
        this.stops = stops;
        this.packliste = packliste;
        this.gesamtBudgetProReisender = gesamtBudgetProReisender;
        this.gesamtAusgabeProReisender = gesamtAusgabeProReisender;
        this.schuldens = schuldens;
        this.reiseTeilnehmer = reiseTeilnehmer;
        this.gesamtAusgabe = gesamtAusgabe;
        this.gesamtBudget = gesamtBudget;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }

    public String getReiseErsteller() {
        return reiseErsteller;
    }

    public void setReiseErsteller(String reiseErsteller) {
        this.reiseErsteller = reiseErsteller;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public List<PacklistenItem> getPackliste() {
        return packliste;
    }

    public void setPackliste(List<PacklistenItem> packliste) {
        this.packliste = packliste;
    }

    public List<AusgabenSumme> getGesamtBudgetProReisender() {
        return gesamtBudgetProReisender;
    }

    public void setGesamtBudgetProReisender(List<AusgabenSumme> gesamtBudgetProReisender) {
        this.gesamtBudgetProReisender = gesamtBudgetProReisender;
    }

    public List<Schulden> getDebts() {
        return schuldens;
    }

    public void setDebts(List<Schulden> schuldens) {
        this.schuldens = schuldens;
    }

    public BigDecimal getGesamtAusgabe() {
        return gesamtAusgabe;
    }

    public void setGesamtAusgabe(BigDecimal gesamtAusgabe) {
        this.gesamtAusgabe = gesamtAusgabe;
    }

    public BigDecimal getGesamtBudget() {
        return gesamtBudget;
    }

    public void setGesamtBudget(BigDecimal gesamtBudget) {
        this.gesamtBudget = gesamtBudget;
    }

    public List<AusgabenSumme> getGesamtAusgabeProReisender() {
        return gesamtAusgabeProReisender;
    }

    public void setGesamtAusgabeProReisender(List<AusgabenSumme> gesamtAusgabeProReisender) {
        this.gesamtAusgabeProReisender = gesamtAusgabeProReisender;
    }

    public List<Schulden> getSchuldens() {
        return schuldens;
    }

    public void setSchuldens(List<Schulden> schuldens) {
        this.schuldens = schuldens;
    }

    public List<ReiseTeilnehmer> getReiseTeilnehmer() {
        return reiseTeilnehmer;
    }

    public void setReiseTeilnehmer(List<ReiseTeilnehmer> reiseTeilnehmer) {
        this.reiseTeilnehmer = reiseTeilnehmer;
    }
}
