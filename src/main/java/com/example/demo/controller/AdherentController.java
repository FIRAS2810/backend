package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.AdherentProfileDTO;
import com.example.demo.dto.AdherentTableDTO;
import com.example.demo.dto.AdherentUpdateDTO;
import com.example.demo.entities.Adherent;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.service.AdherentService;

@RestController
@RequestMapping("/api/adherents")
@CrossOrigin("*")
public class AdherentController {

	@Autowired
    private AdherentService adherentService;
	
	@Autowired
    private AdherentRepository adherentRepository;

    
    @GetMapping("/profil/{email}")
    public AdherentProfileDTO getMonProfil(@PathVariable String email) {
        return adherentService.getMonProfil(email);
    }
    
    @PutMapping("/update-profil/{email}")
    public ResponseEntity<?> updateProfil(@PathVariable String email, @RequestBody AdherentUpdateDTO dto) {
        adherentService.updateProfil(email, dto);
        return ResponseEntity.ok().body("Profil mis à jour avec succès !");
    }
    
    @GetMapping("/tous")
    public List<AdherentTableDTO> getAllAdherents() {
        return adherentService.getAllAdherents();
    }

    @GetMapping("/autres/{cin}")
    public List<AdherentTableDTO> getAutresAdherents(@PathVariable String cin) {
        return adherentService.getAllAdherents().stream()
            .filter(dto -> !dto.getCin().equals(cin)) // filtrer soi-même
            .collect(Collectors.toList());
    }


    /*@GetMapping("/tous")
    public List<AdherentTableDTO> getTous() {
        return adherentService.getTousLesAdherents();
    }*/
    
    @PostMapping("/update-photo/{cin}")
    public ResponseEntity<String> updatePhoto(@PathVariable String cin, @RequestParam("photo") MultipartFile photo) {
        try {
            Adherent adherent = adherentRepository.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Adhérent introuvable"));

            adherent.setPhotoProfil(photo.getBytes());
            adherentRepository.save(adherent);

            return ResponseEntity.ok("Photo mise à jour !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l’upload.");
        }
    }


    @PatchMapping("/signaler-deces/{cin}")
    public void signalerDeces(@PathVariable String cin) {
        adherentService.signalerDeces(cin);
    }

    @PatchMapping("/exclure-temporairement/{cin}")
    public ResponseEntity<Void> exclureTemporairement(@PathVariable String cin) {
        adherentService.signalerExclusionTemporaire(cin);
        return ResponseEntity.ok().build();
    }

    
    

}
