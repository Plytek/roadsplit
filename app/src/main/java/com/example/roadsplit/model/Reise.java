package com.example.roadsplit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public class Reise {

    private Long id;
    private String name;
    private String uniquename;
    private String reiseErsteller;
    private boolean ongoing;
    private List<Stop> stops;
    private List<PacklistenItem> packliste;
    private long createDate;

    public Reise(Long id, String name, String uniquename, String reiseErsteller, boolean ongoing, List<Stop> stops, List<PacklistenItem> packliste, long createDate) {
        this.id = id;
        this.name = name;
        this.uniquename = uniquename;
        this.reiseErsteller = reiseErsteller;
        this.ongoing = ongoing;
        this.stops = stops;
        this.packliste = packliste;
        this.createDate = createDate;
    }

    public Reise(Long id, String name, String uniquename, boolean ongoing, List<Stop> stops, List<PacklistenItem> packliste, long createDate) {
        this.id = id;
        this.name = name;
        this.uniquename = uniquename;
        this.ongoing = ongoing;
        this.stops = stops;
        this.packliste = packliste;
        this.createDate = createDate;
    }

    public Reise(Long id, String name, String uniquename, boolean ongoing, List<Stop> stops, List<PacklistenItem> packliste) {
        this.id = id;
        this.name = name;
        this.uniquename = uniquename;
        this.ongoing = ongoing;
        this.stops = stops;
        this.packliste = packliste;
    }

    public Reise(Long id, String name, boolean ongoing, List<Stop> stops) {
        this.id = id;
        this.name = name;
        this.ongoing = ongoing;
        this.stops = stops;
    }

    public Reise(Long id, String name, String uniquename, boolean ongoing, List<Stop> stops) {
        this.id = id;
        this.name = name;
        this.uniquename = uniquename;
        this.ongoing = ongoing;
        this.stops = stops;
    }

    public Reise(String name, String uniquename, boolean ongoing, List<Stop> stops) {
        this.name = name;
        this.uniquename = uniquename;
        this.ongoing = ongoing;
        this.stops = stops;
    }

    public Reise() {
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

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }

    public List<PacklistenItem> getPackliste() {
        return packliste;
    }

    public void setPackliste(List<PacklistenItem> packliste) {
        this.packliste = packliste;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getReiseErsteller() {
        return reiseErsteller;
    }

    public void setReiseErsteller(String reiseErsteller) {
        this.reiseErsteller = reiseErsteller;
    }

}
