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
    private BigDecimal budget;
    private BigDecimal gesamtausgaben;
    private long latitude;
    private long longitude;

    private List<Ausgabe> ausgaben;

    public Stop(long id, String name, BigDecimal budget, BigDecimal gesamtausgaben, long latitude, long longitude, List<Ausgabe> ausgaben) {
        this.id = id;
        this.name = name;
        this.budget = budget;
        this.gesamtausgaben = gesamtausgaben;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ausgaben = ausgaben;
    }

    public Stop() {
        ausgaben = new ArrayList<>();
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

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getGesamtausgaben() {
        return gesamtausgaben;
    }

    public void setGesamtausgaben(BigDecimal gesamtausgaben) {
        this.gesamtausgaben = gesamtausgaben;
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

    public List<Ausgabe> getAusgaben() {
        return ausgaben;
    }

    public void setAusgaben(List<Ausgabe> ausgaben) {
        this.ausgaben = ausgaben;
    }


    @Override
    public String toString() {
        return "Stop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", gesamtausgaben=" + gesamtausgaben +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", ausgaben=" + ausgaben +
                '}';
    }
}
