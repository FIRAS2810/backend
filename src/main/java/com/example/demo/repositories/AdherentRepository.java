package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Adherent;



public interface AdherentRepository extends JpaRepository<Adherent, String> {

}
