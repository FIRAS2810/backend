package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.service.RegisterService;

@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = "*")
public class RegisterController {

	@Autowired
    private RegisterService registerService;

    @GetMapping("/verifier-cin/{cin}")
    public ResponseEntity<Boolean> verifierCIN(@PathVariable String cin) {
        boolean peutSinscrire = registerService.verifierCINPourInscription(cin);
        return ResponseEntity.ok(peutSinscrire);
    }
    
    @PostMapping("/creer-compte")
    public ResponseEntity<Map<String, String>> creerCompte(@RequestBody RegisterRequestDTO request) {
        try {
            registerService.enregistrerCompte(request.getCin(), request.getEmail(), request.getMotDePasse());
            return ResponseEntity.ok(Map.of("message", "✅ Compte créé avec succès !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "❌ Erreur : " + e.getMessage()));
        }
    }

}
