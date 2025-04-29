package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.CompteAttente;

public interface CompteAttenteRepository extends JpaRepository<CompteAttente,Long> {

}
