package com.example.roadsplit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class Stop {

    private long id;
    private String name;

    public Stop(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + '\n';
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
}
