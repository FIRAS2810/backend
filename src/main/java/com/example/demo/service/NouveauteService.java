package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.NouveauteResponseDTO;
import com.example.demo.entities.Nouveaute;
import com.example.demo.entities.TypeFichier;
import com.example.demo.repositories.NouveauteRepository;

@Service
public class NouveauteService {

	@Autowired
    private NouveauteRepository nouveauteRepository;

	public void ajouterNouveaute(String titre,String description, MultipartFile fichier) throws IOException {

	    Nouveaute nouveaute = new Nouveaute();

	    nouveaute.setTitre(titre);
	    nouveaute.setDescription(description);
	    nouveaute.setDatePublication(LocalDate.now());  // Date automatique

	    // Enregistrement du contenu du fichier
	    nouveaute.setFichier(fichier.getBytes());

	    // Enregistrement du nom original du fichier
	    nouveaute.setNomFichier(fichier.getOriginalFilename());

	    // Détection automatique du type du fichier
	    String contentType = fichier.getContentType();

	    if (contentType != null && contentType.startsWith("image")) {
	        nouveaute.setTypeFichier(TypeFichier.IMAGE);
	    } else {
	        nouveaute.setTypeFichier(TypeFichier.PDF);
	    }

	    // Sauvegarde en base
	    nouveauteRepository.save(nouveaute);
	}

	
	public List<Nouveaute> getAllNouveautes() {
	    return nouveauteRepository.findAll();
	}
	
	
	public List<NouveauteResponseDTO> getAllNouveautees() {
	    return nouveauteRepository.findAll().stream().map(n -> {
	        NouveauteResponseDTO dto = new NouveauteResponseDTO();
	        dto.setId(n.getId());
	        dto.setTitre(n.getTitre());
	        dto.setDescription(n.getDescription());
	        dto.setDatePublication(n.getDatePublication().toString());
	        dto.setNomFichier(n.getNomFichier());
	        dto.setTypeFichier(n.getTypeFichier().name());

	        // encoder base64
	        dto.setFichierBase64(Base64.getEncoder().encodeToString(n.getFichier()));
	        return dto;
	    }).toList();
	}

	
	
	public void supprimerNouveaute(Long id) {
	    nouveauteRepository.deleteById(id);
	}
	
	
	public void modifierNouveaute(Long id, String titre, String description, MultipartFile fichier) throws IOException {
	    Nouveaute nouveaute = nouveauteRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Nouveauté non trouvée"));

	    nouveaute.setTitre(titre);
	    nouveaute.setDescription(description);

	    if (fichier != null && !fichier.isEmpty()) {
	        nouveaute.setFichier(fichier.getBytes());
	        nouveaute.setNomFichier(fichier.getOriginalFilename());

	        if (fichier.getContentType().startsWith("image")) {
	            nouveaute.setTypeFichier(TypeFichier.IMAGE);
	        } else {
	            nouveaute.setTypeFichier(TypeFichier.PDF);
	        }
	    }

	    nouveauteRepository.save(nouveaute);
	}

	
	



}
