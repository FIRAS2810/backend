package com.example.demo.dto;

import lombok.Data;

@Data
public class TransfertActionRequestDTO {

	private String cinVendeur;
    private String cinAcheteur;
    private int nombreActions;
    private String commentaire;
}
