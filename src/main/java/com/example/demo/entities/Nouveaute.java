package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Nouveaute {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String titre;

	private String nomFichier;
	
    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate datePublication;

    @Lob
    private byte[] fichier;  
    
       // Exemple : "document.pdf" ou "photo.png"


    @Enumerated(EnumType.STRING)
    private TypeFichier typeFichier; 
    
}
