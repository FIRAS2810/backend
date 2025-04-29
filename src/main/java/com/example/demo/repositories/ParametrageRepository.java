package com.example.demo.repositories;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entities.Parametrage;

public interface ParametrageRepository extends JpaRepository <Parametrage,Long> {

	@Query(value = "SELECT * FROM parametrage ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Parametrage> findLastValue();

}
