package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Personne;



public interface PersonneRepository extends JpaRepository<Personne, String> {

}

