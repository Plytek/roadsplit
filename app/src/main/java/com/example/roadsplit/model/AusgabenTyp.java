package com.example.roadsplit.model;

public enum AusgabenTyp {
    Egal,
    Unterkunft,
    Verpflegung,
    Transport,
    Events;

    public static AusgabenTyp typForPosition(int pos)
    {
        switch (pos){
            case 1:
                return Unterkunft;
            case 2:
                return Verpflegung;
            case 3:
                return Transport;
            case 4:
                return Events;
            default: return Egal;
        }
    }
}
