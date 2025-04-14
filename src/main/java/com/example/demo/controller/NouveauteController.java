package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.NouveauteResponseDTO;
import com.example.demo.entities.Nouveaute;
import com.example.demo.service.NouveauteService;

@RestController
@RequestMapping("/api/nouveautes")
@CrossOrigin("*")
public class NouveauteController {

	@Autowired
    private NouveauteService nouveauteService;

	@PostMapping("/ajouter")
    public ResponseEntity<String> ajouterNouveaute(
    		@RequestParam String titre,
            @RequestParam String description,
            @RequestParam MultipartFile fichier) {

        try {
            nouveauteService.ajouterNouveaute(titre,description, fichier);
            return ResponseEntity.ok("✅ Nouveauté ajoutée avec succès !");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("❌ Erreur lors de l'ajout !");
        }
    }

    // ✅ Get All Nouveautés
	//@GetMapping("/toutes")
	//public List<Nouveaute> getAllNouveautes() {
	   // return nouveauteService.getAllNouveautes();
	//}
	
	@GetMapping("/toutes")
	public List<NouveauteResponseDTO> getAllNouveautes() {
	    return nouveauteService.getAllNouveautees();
	}

	
	
	@DeleteMapping("/supprimer/{id}")
	public ResponseEntity<String> supprimerNouveaute(@PathVariable Long id) {
	    nouveauteService.supprimerNouveaute(id);
	    return ResponseEntity.ok("✅ Nouveauté supprimée avec succès !");
	}
	
	
	@PutMapping("/modifier/{id}")
	public ResponseEntity<String> modifierNouveaute(
	        @PathVariable Long id,
	        @RequestParam String titre,
	        @RequestParam String description,
	        @RequestParam(required = false) MultipartFile fichier) throws IOException {

	    nouveauteService.modifierNouveaute(id, titre, description, fichier);
	    return ResponseEntity.ok("✅ Nouveauté modifiée avec succès !");
	}



}
