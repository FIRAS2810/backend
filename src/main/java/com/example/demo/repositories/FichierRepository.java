package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Fichier;

public interface FichierRepository extends JpaRepository<Fichier, Long> {

}
