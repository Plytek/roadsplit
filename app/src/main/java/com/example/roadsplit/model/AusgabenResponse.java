package com.example.roadsplit.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AusgabenResponse {
    private String message;
    private Reisender zahler;
    private List<Reisender> reisende;
    private HashMap<Reisender, List<Ausgabe>> bezahltVon;
    private HashMap<Reisender, List<Ausgabe>> bezahltFuer;
    private HashMap<Reisender, BigDecimal> zusammenfassung;


}
