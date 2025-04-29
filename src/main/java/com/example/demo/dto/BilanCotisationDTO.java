package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BilanCotisationDTO {

	private double montantTotalVerse;
    private int nombreTotalActions;
    private int nombreActionsCotisees;
    private int nombreActionsRecues;
    private int nombreActionsVendues;
    private double montantRestant;
    private boolean adhesionComplete;
    private LocalDate dateDernierVersement;
    private double montantTransfertsEstime;
    private double montantTotalEstime;
    private double soldeDisponible; // 💡 Valeur actuelle en DT (actions actuelles × valeur action)

}
