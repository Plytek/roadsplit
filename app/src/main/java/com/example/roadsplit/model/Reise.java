package com.example.roadsplit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public class Reise {

    private Long id;
    private String name;
    private List<Stop> stops;

    public Reise(Long id, String name, List<Stop> stops) {
        this.id = id;
        this.name = name;
        this.stops = stops;
    }

    @Override
    public String toString() {
        return name + '\n';
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

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }
}
