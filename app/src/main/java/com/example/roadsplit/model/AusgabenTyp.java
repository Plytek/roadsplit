package com.example.roadsplit.model;

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
}
