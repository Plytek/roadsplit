package com.example.roadsplit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public class Reise {

    private Long id;
    private String name;
    private boolean ongoing;
    private List<Stop> stops;


    @Override
    public String toString() {
        return name + '\n';
    }


    public Reise(Long id, String name, boolean ongoing, List<Stop> stops) {
        this.id = id;
        this.name = name;
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
}
