package com.example.roadsplit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
public class Stop {

    private long id;
    private String name;

    @Override
    public String toString() {
        return name + '\n';
    }
}
