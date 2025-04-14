package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class DemandeAdhesionRequestDTO {
	

	private String cin;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String activite;
    private String dateNaissance; // format ISO : "2001-05-14"

    // Justificatif (image ou PDF)
    private MultipartFile fichier;
    private String typeFichier;  
    private String nomFichier;
}
