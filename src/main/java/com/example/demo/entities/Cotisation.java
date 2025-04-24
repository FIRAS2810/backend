package com.example.demo.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Cotisation {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate datePaiement = LocalDate.now();

    private double montantVerse;            // Montant versé par l'adhérent (saisi par l'admin)
    private int nombreActions;              // Nombre d'actions (calculé automatiquement)

    private double valeurActionSnapshot;    // Valeur d'une action au moment du paiement
    private double montantMinimalSnapshot;  // Montant minimal adhésion au moment du paiement

    @ManyToOne
    @JoinColumn(name = "cin_adherent")
    @JsonIgnore
    private Adherent adherent;    
}
