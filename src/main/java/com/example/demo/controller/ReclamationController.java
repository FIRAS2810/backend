package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Reclamation;
import com.example.demo.repositories.ReclamationRepository;
import com.example.demo.service.ReclamationService;

@RestController
@RequestMapping("/api/reclamations")
@CrossOrigin(origins = "*")
public class ReclamationController {

	@Autowired
    private ReclamationService reclamationService;
	
	@Autowired
    private ReclamationRepository reclamationRepository;

    @PostMapping("/soumettre")
    public Reclamation soumettreReclamation(@RequestBody Reclamation reclamation) {
        return reclamationService.soumettreReclamation(reclamation);
    }
    
    @GetMapping("/toutes")
    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findAll();
    }
    
    @PatchMapping("/traiter/{id}")
    public ResponseEntity<Void> traiterReclamation(@PathVariable Long id, @RequestParam boolean accepter) {
        reclamationService.traiterReclamation(id, accepter);
        return ResponseEntity.ok().build();
    }



}
