package com.example.demo.dto;

import lombok.Data;

@Data
public class DemandeAdhesionResponseDTO {

	private Long id;
    private String cinPersonne;
    private String nom;
    private String prenom;
    private String sexe;
    private String email;
    private String etat;
    private String dateDemande;
    private String tel;
    private String ville;
    private String activite;
    private String dateDecision;
    private String justificatifBase64;
}
