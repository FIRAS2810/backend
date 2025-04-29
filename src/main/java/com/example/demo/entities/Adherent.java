package com.example.demo.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Adherent {

	@Id
    private String cin;

    @OneToOne
    @MapsId
    @JoinColumn(name = "cin")
    private Personne personne;

    @Lob
    private byte[] photoProfil;

    private LocalDate dateInscription;

    @Enumerated(EnumType.STRING)
    private StatutAdherent statut = StatutAdherent.VIVANT;
    
    private int nombreActionsRecues; 

    private int nombreActionsAchetees;
    
    private int nombreActionsVendues;

    private double montantMinimalAdhesion;

    private LocalDate dateExclusionTemporaire; // 🆕


    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    private Utulisateur utulisateur;
    
    @OneToMany(mappedBy = "adherent")
    private List<Cotisation> cotisations;

}
