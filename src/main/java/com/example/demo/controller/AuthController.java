package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.JwtResponseDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entities.ResetToken;
import com.example.demo.entities.StatutCompte;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.ResetTokenRepository;
import com.example.demo.repositories.UtulisateurRepository;
import com.example.demo.service.MailService;
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
    private MailService mailService;

    @Autowired
    private UtulisateurRepository utilisateurRepository;
    
    @Autowired
    private ResetTokenRepository resetTokenRepository;

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
    
    
    @PostMapping("/demander-reset")
    public ResponseEntity<?> demanderReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Utulisateur user = utilisateurRepository.findByEmail(email)
        	    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));


        String code = String.valueOf(new Random().nextInt(899999) + 100000); // 6 chiffres
        ResetToken token = new ResetToken();
        token.setCode(code);
        token.setEmail(email);
        token.setExpiration(LocalDateTime.now().plusMinutes(5));
        resetTokenRepository.deleteByEmail(email);
        resetTokenRepository.save(token);

        mailService.envoyerMail(email, "🔐 Code de réinitialisation", "Voici votre code : " + code);

        return ResponseEntity.ok(Collections.singletonMap("message", "Code envoyé"));

    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        String nouveauPass = payload.get("newPassword");

        ResetToken token = resetTokenRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new RuntimeException("Code invalide"));

        if (token.getExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("Code expiré");
        }

        Utulisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        utilisateur.setMotDePasse(new BCryptPasswordEncoder().encode(nouveauPass));
        utilisateurRepository.save(utilisateur);
        resetTokenRepository.delete(token);

        return ResponseEntity.ok(Collections.singletonMap("message", "Mot de passe réinitialisé avec succès !"));

    }

    @PostMapping("/verifier-code")
    public ResponseEntity<?> verifierCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");

        Optional<ResetToken> tokenOpt = resetTokenRepository.findByEmailAndCode(email, code);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code invalide");
        }

        ResetToken token = tokenOpt.get();
        if (token.getExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("Code expiré");
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Code valide"));
    }


}
