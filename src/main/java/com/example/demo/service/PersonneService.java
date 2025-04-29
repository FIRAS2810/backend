package com.example.demo.service;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repositories.PersonneRepository;

@Service
public class PersonneService {
	
	@Autowired
    private PersonneRepository personneRepository;

	

	public long getTrancheAgeMoins30() {
	    return personneRepository.findAll().stream()
	        .filter(p -> Period.between(p.getDateNaissance(), LocalDate.now()).getYears() < 30)
	        .count();
	}

	public long getTrancheAge30a50() {
	    return personneRepository.findAll().stream()
	        .filter(p -> {
	            int age = Period.between(p.getDateNaissance(), LocalDate.now()).getYears();
	            return age >= 30 && age <= 50;
	        })
	        .count();
	}

	public long getTrancheAgePlus50() {
	    return personneRepository.findAll().stream()
	        .filter(p -> Period.between(p.getDateNaissance(), LocalDate.now()).getYears() > 50)
	        .count();
	}

}
