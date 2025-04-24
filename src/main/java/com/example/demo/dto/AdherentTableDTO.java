package com.example.demo.dto;

import lombok.Data;

@Data
public class AdherentTableDTO {

	
    private String nom;
    private String prenom;
    private String email;
    private String cin;
    private String tel;
    private String sexe;
    private String etat;
    private String dateAdhesion;
    private String dateFin;
    private double montant;
    private int nbActions;
    private String photoProfilBase64;
}
