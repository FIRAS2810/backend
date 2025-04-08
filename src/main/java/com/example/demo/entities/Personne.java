  package com.example.demo.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Personne {

	@Id
    private String cin;  

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateNaissance;
    private String activite;
    
    @OneToMany(mappedBy = "personne", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DemandeAdhesion> demandes;
}
