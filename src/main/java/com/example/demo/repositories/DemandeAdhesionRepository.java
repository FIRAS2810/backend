package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.DemandeAdhesion;
import com.example.demo.entities.EtatDemande;


public interface DemandeAdhesionRepository extends JpaRepository<DemandeAdhesion, Long> {

	boolean existsByPersonneCin(String cin);
	long countByEtat(EtatDemande etat);

}
