package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Adherent;
import com.example.demo.entities.StatutAdherent;



public interface AdherentRepository extends JpaRepository<Adherent, String> {

	@Query("SELECT a FROM Adherent a WHERE a.utulisateur.email = :email")
	Optional<Adherent> findAdherentByEmail(@Param("email") String email);

	Optional<Adherent> findByCin(String cin);

	List<Adherent> findByStatut(StatutAdherent statut);
	
	


}
