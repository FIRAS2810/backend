package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Reclamation;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

	Optional<Reclamation> findByCin(String cin);

}
