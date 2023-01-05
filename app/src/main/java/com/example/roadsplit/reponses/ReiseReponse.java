package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;

import java.math.BigDecimal;
import java.util.List;

public class ReiseReponse {
    private String message;
    private Reise reise;
    private Reisender reisender;
    private List<Reisender> reisendeList;
    private List<Reisender> schuldner;
    private List<BigDecimal> betraege;
    private BigDecimal gesamtAusgabe;


    public ReiseReponse(String message, Reise reise, Reisender reisender, List<Reisender> reisendeList, List<Reisender> schuldner, List<BigDecimal> betraege, BigDecimal gesamtAusgabe) {
        this.message = message;
        this.reise = reise;
        this.reisender = reisender;
        this.reisendeList = reisendeList;
        this.schuldner = schuldner;
        this.betraege = betraege;
        this.gesamtAusgabe = gesamtAusgabe;
    }

    public ReiseReponse(String message, Reise reise, Reisender reisender, List<Reisender> reisendeList, List<Reisender> schuldner, List<BigDecimal> betraege) {
        this.message = message;
        this.reise = reise;
        this.reisender = reisender;
        this.reisendeList = reisendeList;
        this.schuldner = schuldner;
        this.betraege = betraege;
    }

    public ReiseReponse(String message, Reise reise, Reisender reisender, List<Reisender> reisendeList) {
        this.message = message;
        this.reise = reise;
        this.reisender = reisender;
        this.reisendeList = reisendeList;
    }

    public ReiseReponse() {
    }

    public ReiseReponse(String message, Reise reise, Reisender reisender) {
        this.message = message;
        this.reise = reise;
        this.reisender = reisender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Reise getReise() {
        return reise;
    }

    public void setReise(Reise reise) {
        this.reise = reise;
    }

    public Reisender getReisender() {
        return reisender;
    }

    public void setReisender(Reisender reisender) {
        this.reisender = reisender;
    }

    public List<Reisender> getReisendeList() {
        return reisendeList;
    }

    public void setReisendeList(List<Reisender> reisendeList) {
        this.reisendeList = reisendeList;
    }

    public ReiseReponse(String message, Reise reise, Reisender reisender, List<Reisender> reisendeList, List<Reisender> schuldner) {
        this.message = message;
        this.reise = reise;
        this.reisender = reisender;
        this.reisendeList = reisendeList;
        this.schuldner = schuldner;
    }

    public List<Reisender> getSchuldner() {
        return schuldner;
    }

    public void setSchuldner(List<Reisender> schuldner) {
        this.schuldner = schuldner;
    }

    public List<BigDecimal> getBetraege() {
        return betraege;
    }

    public void setBetraege(List<BigDecimal> betraege) {
        this.betraege = betraege;
    }

    public BigDecimal getGesamtAusgabe() {
        return gesamtAusgabe;
    }

    public void setGesamtAusgabe(BigDecimal gesamtAusgabe) {
        this.gesamtAusgabe = gesamtAusgabe;
    }
}
