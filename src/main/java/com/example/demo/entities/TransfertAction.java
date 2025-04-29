package com.example.demo.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class TransfertAction {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendeur_cin")
    @JsonIgnore
    private Adherent vendeur;

    @ManyToOne
    @JoinColumn(name = "acheteur_cin")
    @JsonIgnore
    private Adherent acheteur;

    private int nombreActions;
    
    private double valeurUnitaireTransfert; // 🆕 snapshot de la valeur au moment du transfert


    private LocalDateTime dateTransfert;
    
    @Enumerated(EnumType.STRING)
    private StatutTransfert statut; // EN_ATTENTE, VALIDE, REFUSE

    @Enumerated(EnumType.STRING)
    private StatutVendeur statutVendeur;

    private String commentaire;
}
