package com.example.demo.dto;

import lombok.Data;

@Data
public class DemandeAdhesionResponseDTO {

	private Long id;
    private String cinPersonne;
    private String nom;
    private String prenom;
    private String email;
    private String etat;
    private String dateDemande;
    private String justificatifBase64;
}
