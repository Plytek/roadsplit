package com.example.roadsplit.model;

import java.math.BigDecimal;

public class AusgabenSumme {

         private long id;
        private long reisenderId;
        private String reisenderName;
        private BigDecimal betrag;

        public AusgabenSumme(long id, long reisenderId, String reisenderName, BigDecimal betrag) {
            this.id = id;
            this.reisenderId = reisenderId;
            this.reisenderName = reisenderName;
            this.betrag = betrag;
        }

        public AusgabenSumme() {
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
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

        public BigDecimal getBetrag() {
            return betrag;
        }

        public void setBetrag(BigDecimal betrag) {
            this.betrag = betrag;
        }
}
