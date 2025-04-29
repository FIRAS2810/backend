package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Reclamation {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String motif;

	    private String cin; // ✅ CIN au lieu d'une relation

	    private LocalDate dateReclamation;

	    @Enumerated(EnumType.STRING)
	    private StatutReclamation statut = StatutReclamation.EN_ATTENTE;
	
}
