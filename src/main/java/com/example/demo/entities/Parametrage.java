package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Parametrage {

	@Id
    private Long id = 1L; 

    private double montantMinimalAdhesion = 30.0; 
    private double valeurAction = 5.0;

}
