package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.JwtResponseDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entities.StatutCompte;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.UtulisateurRepository;
import com.example.demo.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
    private JwtUtil jwtUtil;
	
	@Autowired
	private AuthenticationManager authenticationManager;


    @Autowired
    private UtulisateurRepository utilisateurRepository;

    @PostMapping("/login")
    public JwtResponseDTO authenticate(@RequestBody LoginRequestDTO request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getMotDePasse()
                    )
            );
        } catch (DisabledException e) {
            throw new Exception("Utilisateur désactivé", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Email ou mot de passe incorrect", e);
        }

        Utulisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
        
        if (utilisateur.getStatutCompte() == StatutCompte.INACTIF) {
            throw new Exception("Votre compte a été désactivé. Veuillez contacter l'administration.");
        }

        String token = jwtUtil.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole().name(),
                utilisateur.getId()
        );

        return new JwtResponseDTO(token, utilisateur.getRole().name(), utilisateur.getEmail());
    }
}
