package com.example.roadsplit.model;

import java.util.ArrayList;
import java.util.List;

public enum AusgabenTyp {
    Allgemein,
    Restaurants,
    Transport,
    Tanken,
    Unterkunft,
    Shopping,
    Aktivitaeten,
    Gebuehren;

    public static AusgabenTyp typForPosition(int pos)
    {
        switch (pos){
            case 1:
                return Restaurants;
            case 2:
                return Transport;
            case 3:
                return Tanken;
            case 4:
                return Unterkunft;
            case 5:
                return Shopping;
            case 6:
                return Aktivitaeten;
            case 7:
                return Gebuehren;
            default: return Allgemein;
        }
    }

    public static List<String> ausgabenTypenWithEmoji()
    {
        List<String> ausgabenTyp = new ArrayList<>();
        ausgabenTyp.add("\uD83D\uDCB8 " + Allgemein.name());
        ausgabenTyp.add("\uD83C\uDF5D " + Restaurants.name());
        ausgabenTyp.add("\uD83D\uDE87 " + Transport.name());
        ausgabenTyp.add("\u26FD " + Tanken.name());
        ausgabenTyp.add("\uD83D\uDECF " + Unterkunft.name());
        ausgabenTyp.add("\uD83D\uDECD " + Shopping.name());
        ausgabenTyp.add("\uD83D\uDCFD " + Aktivitaeten.name());
        ausgabenTyp.add("\uD83D\uDCB3 " + Gebuehren.name());
        return ausgabenTyp;
    }
}
