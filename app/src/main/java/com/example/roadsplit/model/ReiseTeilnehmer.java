package com.example.roadsplit.model;

public class ReiseTeilnehmer {
    private Long id;
    private long reisenderId;
    private String reisenderName;

    public ReiseTeilnehmer() {
    }

    public ReiseTeilnehmer(Long id, long reisenderId, String reisenderName) {
        this.id = id;
        this.reisenderId = reisenderId;
        this.reisenderName = reisenderName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getReisenderId() {
        return reisenderId;
    }

    public void setReisenderId(long reisenderId) {
        this.reisenderId = reisenderId;
    }

    public String getReisenderName() {
        return reisenderName;
    }

    public void setReisenderName(String reisenderName) {
        this.reisenderName = reisenderName;
    }
}
