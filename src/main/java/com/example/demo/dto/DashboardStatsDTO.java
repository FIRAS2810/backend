package com.example.demo.dto;

import lombok.Data;

@Data
public class DashboardStatsDTO {

	private double chiffreAffaires;
    private double resteCotisation;
    private long nbAdherents;
    private double valeurPart=5;
    private long nbAdhesions;
    private long nbHommes;
    private long nbFemmes;
    private long nbMoins30;
    private long nbEntre30et50;
    private long nbPlus50;
    private double totalCA;
    private long nbDemandesValidees;
    private long nbDemandesRefusees;
    private long nbDemandesEnAttente;

    private double croissancePourcent;
    private double previsionCA;
    private int nombreActionsTransferees;
    private int nombreActionsVendues;


}
