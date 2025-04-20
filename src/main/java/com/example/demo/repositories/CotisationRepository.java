package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Cotisation;

public interface CotisationRepository extends JpaRepository<Cotisation,Long> {

	List<Cotisation> findByAdherentCin(String cin);
}
