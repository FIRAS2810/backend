package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.CompteAttente;
import com.example.demo.service.CompteAttenteService;

@RestController
@RequestMapping("/api/compte-attente")
@CrossOrigin(origins = "*")
public class CompteAttenteControlleur {

	
	    @Autowired
	    private CompteAttenteService compteAttenteService;

	    @GetMapping("/tous")
	    public ResponseEntity<List<CompteAttente>> getComptesAttente() {
	        List<CompteAttente> comptes = compteAttenteService.getAllComptesAttente();
	        return ResponseEntity.ok(comptes);
	    }
	    // PATCH : Marquer un compte comme réglé
	    @PatchMapping("/regler/{id}")
	    public ResponseEntity<Void> marquerCommeRegle(@PathVariable Long id) {
	        compteAttenteService.marquerCommeRegle(id);  // Appelle la méthode du service pour mettre à jour le statut
	        return ResponseEntity.ok().build(); // Retourne une réponse 200 OK
	    }

	    
}
