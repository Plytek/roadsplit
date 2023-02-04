package com.example.roadsplit.model.finanzen;

import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;

import java.math.BigDecimal;
import java.util.List;

public class AusgabenReport {

    private Reise reise;
    private List<Reisender> reisende;
    //Ausgaben der Gesamten Gruppe
    private BigDecimal gruppenGesamtausgabe;
    //Ausgaben für die Gruppe
    private BigDecimal persoenlicheGruppenausgaben;
    //Nur für einen selbst
    private BigDecimal privateAusgaben;
    private BigDecimal budget;
    private long nutzerId;
    private String nutzerName;

    private List<Ausgabe> ausgabenFuerGruppe;
    private List<Ausgabe> ausgabenFuerSelbst;
    private List<Schulden> schuldenReport;

    public AusgabenReport(Reise reise, List<Reisender> reisende, BigDecimal gruppenGesamtausgabe, BigDecimal persoenlicheGruppenausgaben, BigDecimal privateAusgaben, BigDecimal budget, long nutzerId, String nutzerName, List<Ausgabe> ausgabenFuerGruppe, List<Ausgabe> ausgabenFuerSelbst, List<Schulden> schuldenReport) {
        this.reise = reise;
        this.reisende = reisende;
        this.gruppenGesamtausgabe = gruppenGesamtausgabe;
        this.persoenlicheGruppenausgaben = persoenlicheGruppenausgaben;
        this.privateAusgaben = privateAusgaben;
        this.budget = budget;
        this.nutzerId = nutzerId;
        this.nutzerName = nutzerName;
        this.ausgabenFuerGruppe = ausgabenFuerGruppe;
        this.ausgabenFuerSelbst = ausgabenFuerSelbst;
        this.schuldenReport = schuldenReport;
    }

    public AusgabenReport() {
    }

    public AusgabenReport(BigDecimal gruppenGesamtausgabe, BigDecimal persoenlicheGruppenausgaben, BigDecimal privateAusgaben, BigDecimal budget, long nutzerId, String nutzerName, List<Ausgabe> ausgabenFuerGruppe, List<Ausgabe> ausgabenFuerSelbst, List<Schulden> schuldenReport) {
        this.gruppenGesamtausgabe = gruppenGesamtausgabe;
        this.persoenlicheGruppenausgaben = persoenlicheGruppenausgaben;
        this.privateAusgaben = privateAusgaben;
        this.budget = budget;
        this.nutzerId = nutzerId;
        this.nutzerName = nutzerName;
        this.ausgabenFuerGruppe = ausgabenFuerGruppe;
        this.ausgabenFuerSelbst = ausgabenFuerSelbst;
        this.schuldenReport = schuldenReport;
    }

    public BigDecimal getGruppenGesamtausgabe() {
        return gruppenGesamtausgabe;
    }

    public void setGruppenGesamtausgabe(BigDecimal gruppenGesamtausgabe) {
        this.gruppenGesamtausgabe = gruppenGesamtausgabe;
    }

    public BigDecimal getPersoenlicheGruppenausgaben() {
        return persoenlicheGruppenausgaben;
    }

    public void setPersoenlicheGruppenausgaben(BigDecimal persoenlicheGruppenausgaben) {
        this.persoenlicheGruppenausgaben = persoenlicheGruppenausgaben;
    }

    public BigDecimal getPrivateAusgaben() {
        return privateAusgaben;
    }

    public void setPrivateAusgaben(BigDecimal privateAusgaben) {
        this.privateAusgaben = privateAusgaben;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public long getNutzerId() {
        return nutzerId;
    }

    public void setNutzerId(long nutzerId) {
        this.nutzerId = nutzerId;
    }

    public String getNutzerName() {
        return nutzerName;
    }

    public void setNutzerName(String nutzerName) {
        this.nutzerName = nutzerName;
    }

    public List<Ausgabe> getAusgabenFuerGruppe() {
        return ausgabenFuerGruppe;
    }

    public void setAusgabenFuerGruppe(List<Ausgabe> ausgabenFuerGruppe) {
        this.ausgabenFuerGruppe = ausgabenFuerGruppe;
    }

    public List<Ausgabe> getAusgabenFuerSelbst() {
        return ausgabenFuerSelbst;
    }

    public void setAusgabenFuerSelbst(List<Ausgabe> ausgabenFuerSelbst) {
        this.ausgabenFuerSelbst = ausgabenFuerSelbst;
    }

    public List<Schulden> getSchuldenReport() {
        return schuldenReport;
    }

    public void setSchuldenReport(List<Schulden> schuldenReport) {
        this.schuldenReport = schuldenReport;
    }

    public Reise getReise() {
        return reise;
    }

    public void setReise(Reise reise) {
        this.reise = reise;
    }

    public List<Reisender> getReisende() {
        return reisende;
    }

    public void setReisende(List<Reisender> reisende) {
        this.reisende = reisende;
    }
}
