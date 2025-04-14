package com.example.demo.service;

import java.time.LocalDate;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AdherentProfileDTO;
import com.example.demo.dto.AdherentUpdateDTO;
import com.example.demo.entities.Adherent;
import com.example.demo.entities.Personne;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.PersonneRepository;
import com.example.demo.repositories.UtulisateurRepository;

import jakarta.transaction.Transactional;

@Service
public class AdherentService {
	
	@Autowired
    private UtulisateurRepository utulisateurRepository;
	
	@Autowired
    private PersonneRepository personneRepository;

	public AdherentProfileDTO getMonProfil(String email) {
	    Utulisateur user = utulisateurRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

	    Adherent adherent = user.getAdherent();

	    AdherentProfileDTO dto = new AdherentProfileDTO();
	    dto.setNom(adherent.getPersonne().getNom());
	    dto.setPrenom(adherent.getPersonne().getPrenom());
	    dto.setEmail(user.getEmail());
	    dto.setTelephone(adherent.getPersonne().getTelephone());
	    dto.setAdresse(adherent.getPersonne().getAdresse());
	    dto.setActivite(adherent.getPersonne().getActivite());
	    dto.setCin(adherent.getPersonne().getCin());
	    dto.setDateNaissance(adherent.getPersonne().getDateNaissance().toString());
	    dto.setDateAdhesion(adherent.getDateInscription().toString());

	    if (adherent.getPhotoProfil() != null) {
	        dto.setPhotoProfilBase64(Base64.getEncoder().encodeToString(adherent.getPhotoProfil()));
	    }

	    return dto;
	}
	
	
	@Transactional
	public void updateProfil(String email, AdherentUpdateDTO dto) {
	    Utulisateur user = utulisateurRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

	    Personne personne = user.getAdherent().getPersonne();

	    // Mise à jour Personne
	    personne.setNom(dto.getNom());
	    personne.setPrenom(dto.getPrenom());
	    personne.setTelephone(dto.getTelephone());
	    personne.setDateNaissance(LocalDate.parse(dto.getDateNaissance()));

	    personneRepository.save(personne);

	    // Mise à jour Utulisateur
	    user.setEmail(dto.getEmail());

	    utulisateurRepository.save(user);
	}


}
