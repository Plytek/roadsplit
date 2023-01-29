package com.example.roadsplit.model;

import java.math.BigDecimal;

public class Schulden {
    private Long id;
    private Long reisenderId;
    private String reisenderName;
    private Long reisenderIdOwe;
    private String reisenderNameOwe;
    private BigDecimal amount;

    public Schulden(Long id, Long reisenderId, String reisenderName, Long reisenderIdOwe, String reisenderNameOwe, BigDecimal amount) {
        this.id = id;
        this.reisenderId = reisenderId;
        this.reisenderName = reisenderName;
        this.reisenderIdOwe = reisenderIdOwe;
        this.reisenderNameOwe = reisenderNameOwe;
        this.amount = amount;
    }

    public Schulden() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReisenderId() {
        return reisenderId;
    }

    public void setReisenderId(Long reisenderId) {
        this.reisenderId = reisenderId;
    }

    public String getReisenderName() {
        return reisenderName;
    }

    public void setReisenderName(String reisenderName) {
        this.reisenderName = reisenderName;
    }

    public Long getReisenderIdOwe() {
        return reisenderIdOwe;
    }

    public void setReisenderIdOwe(Long reisenderIdOwe) {
        this.reisenderIdOwe = reisenderIdOwe;
    }

    public String getReisenderNameOwe() {
        return reisenderNameOwe;
    }

    public void setReisenderNameOwe(String reisenderNameOwe) {
        this.reisenderNameOwe = reisenderNameOwe;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
