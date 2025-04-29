package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CompteAttente {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cinAdherent;

    private String nomComplet;


    private double montantTotal;
    
    

    private LocalDate dateSortie;

    @Enumerated(EnumType.STRING)
    private TypeCompteAttente type; // DÉCÈS, EXCLUSION, DÉMISSION

    @Enumerated(EnumType.STRING)
    private StatutReglement statutReglement = StatutReglement.NON_RÉGLÉ;

}
