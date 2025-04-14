package com.example.demo.dto;

import lombok.Data;

@Data
public class AdherentUpdateDTO {

	private String nom;
	private String prenom;
	private String telephone;
	private String adresse; 
	private String email;
	private String dateNaissance;

}
