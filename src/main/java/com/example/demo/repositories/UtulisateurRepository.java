package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Utulisateur;

public interface UtulisateurRepository extends JpaRepository<Utulisateur, Long> {

	Optional<Utulisateur> findByEmail(String email);
	boolean existsByAdherent_Cin(String cin);


}
