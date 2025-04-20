package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BilanCotisationDTO {

	private double montantTotalVerse;
    private int nombreTotalActions;
    private double montantRestant;             // Ce qu'il reste pour atteindre le montant minimal
    private boolean adhesionComplete;          // true si >= montant minimal
    private LocalDate dateDernierVersement;
}
