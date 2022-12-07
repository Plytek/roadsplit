package com.example.roadsplit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
public class Reise {

    private Long id;
    private String name;
    private List<Stop> stops;

    @Override
    public String toString() {
        return name + '\n';
    }
}
