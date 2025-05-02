package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.ResetToken;

public interface ResetTokenRepository extends JpaRepository<ResetToken,Long> {

	Optional<ResetToken> findByEmailAndCode(String email, String code);
	
	
	void deleteByEmail(String email);

}
