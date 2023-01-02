package com.example.roadsplit.reponses;

import com.example.roadsplit.model.Reisender;

import java.math.BigDecimal;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayResponse {
    private String message;
    private HashMap<Reisender, BigDecimal> zusammenfassung = new HashMap<>();
}
