package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DemandeDemission {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cin;

    private String motif;

    private LocalDate dateDemande= LocalDate.now();

    @Enumerated(EnumType.STRING)
    private StatutDemandeDemission statut = StatutDemandeDemission.EN_ATTENTE;
}

