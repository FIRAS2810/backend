package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.TransfertAction;

public interface TransfertActionRepository extends JpaRepository<TransfertAction, Long> {

	List<TransfertAction> findByVendeurCinOrAcheteurCin(String vendeurCin, String acheteurCin);

}
