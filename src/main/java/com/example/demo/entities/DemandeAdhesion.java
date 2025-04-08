package com.example.demo.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class DemandeAdhesion {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDemande = LocalDate.now();

    private LocalDate dateAcceptation; // uniquement si acceptée

    @Enumerated(EnumType.STRING)
    private EtatDemande etat = EtatDemande.EN_ATTENTE;

    
    @ManyToOne
    @JoinColumn(name = "cin_personne")
    private Personne personne;
    
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL)
    private List<Fichier> justificatifs;


}
