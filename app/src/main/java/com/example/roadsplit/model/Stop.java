package com.example.roadsplit.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class Stop {

    private long id;
    private String name;
    private long latitude;
    private long longitude;
    private BigDecimal gesamtBudget;
    private List<AusgabenSumme> budgetProReisender;
    private List<AusgabenSumme> gesamtAusgabeProReisender;
    private List<Ausgabe> ausgaben;

    public Stop(long id, String name, long latitude, long longitude, BigDecimal gesamtBudget, List<AusgabenSumme> budgetProReisender, List<AusgabenSumme> gesamtAusgabeProReisender, List<Ausgabe> ausgaben) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gesamtBudget = gesamtBudget;
        this.budgetProReisender = budgetProReisender;
        this.gesamtAusgabeProReisender = gesamtAusgabeProReisender;
        this.ausgaben = ausgaben;
    }

    public Stop() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getGesamtBudget() {
        return gesamtBudget;
    }

    public void setGesamtBudget(BigDecimal gesamtBudget) {
        this.gesamtBudget = gesamtBudget;
    }

    public List<AusgabenSumme> getBudgetProReisender() {
        return budgetProReisender;
    }

    public void setBudgetProReisender(List<AusgabenSumme> budgetProReisender) {
        this.budgetProReisender = budgetProReisender;
    }

    public List<AusgabenSumme> getGesamtAusgabeProReisender() {
        return gesamtAusgabeProReisender;
    }

    public void setGesamtAusgabeProReisender(List<AusgabenSumme> gesamtAusgabeProReisender) {
        this.gesamtAusgabeProReisender = gesamtAusgabeProReisender;
    }

    public List<Ausgabe> getAusgaben() {
        return ausgaben;
    }

    public void setAusgaben(List<Ausgabe> ausgaben) {
        this.ausgaben = ausgaben;
    }
}
