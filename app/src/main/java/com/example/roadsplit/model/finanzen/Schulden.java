package com.example.roadsplit.model.finanzen;

import java.math.BigDecimal;

public class Schulden {
    private long reisenderId;
    private String reisenderName;
    private long mitreisenderId;
    private String mitreisenderName;

    private BigDecimal schulden;

    public Schulden() {
    }

    public Schulden(long reisenderId, String reisenderName, long mitreisenderId, String mitreisenderName, BigDecimal schulden) {
        this.reisenderId = reisenderId;
        this.reisenderName = reisenderName;
        this.mitreisenderId = mitreisenderId;
        this.mitreisenderName = mitreisenderName;
        this.schulden = schulden;
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

    public long getMitreisenderId() {
        return mitreisenderId;
    }

    public void setMitreisenderId(long mitreisenderId) {
        this.mitreisenderId = mitreisenderId;
    }

    public String getMitreisenderName() {
        return mitreisenderName;
    }

    public void setMitreisenderName(String mitreisenderName) {
        this.mitreisenderName = mitreisenderName;
    }

    public BigDecimal getSchulden() {
        return schulden;
    }

    public void setSchulden(BigDecimal schulden) {
        this.schulden = schulden;
    }
}
