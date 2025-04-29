package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // ✅ pour générer automatiquement le constructeur utilisé dans le .map(...)
@NoArgsConstructor
public class TransfertActionResponseDTO {

	private Long id;
    private int nombreActions;
    private String commentaire;
    private LocalDateTime dateTransfert;
    private String statut;
    private String vendeurNom;
    private String acheteurNom;
    private String cinAcheteur;
    private String cinVendeur;
    private String statutVendeur;


    
}
