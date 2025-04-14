package com.example.demo.dto;

import lombok.Data;

@Data
public class NouveauteResponseDTO {

	private Long id;
    private String titre;
    private String description;
    private String datePublication;
    private String nomFichier;
    private String typeFichier;
    private String fichierBase64;
}
