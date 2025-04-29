package com.example.demo.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Cotisation;

public interface CotisationRepository extends JpaRepository<Cotisation,Long> {

	List<Cotisation> findByAdherentCin(String cin);
	
	@Query("SELECT SUM(c.montantVerse) FROM Cotisation c WHERE c.datePaiement BETWEEN :start AND :end")
	Double getMontantParMois(@Param("start") LocalDate start, @Param("end") LocalDate end);




}
