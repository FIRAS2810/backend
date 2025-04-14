package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AdherentProfileDTO;
import com.example.demo.dto.AdherentUpdateDTO;
import com.example.demo.service.AdherentService;

@RestController
@RequestMapping("/api/adherents")
@CrossOrigin("*")
public class AdherentController {

	@Autowired
    private AdherentService adherentService;

    
    @GetMapping("/profil/{email}")
    public AdherentProfileDTO getMonProfil(@PathVariable String email) {
        return adherentService.getMonProfil(email);
    }
    
    @PutMapping("/update-profil/{email}")
    public ResponseEntity<?> updateProfil(@PathVariable String email, @RequestBody AdherentUpdateDTO dto) {
        adherentService.updateProfil(email, dto);
        return ResponseEntity.ok().body("Profil mis à jour avec succès !");
    }

}
