package com.example.demo.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Adherent;
import com.example.demo.entities.EtatDemande;
import com.example.demo.entities.Parametrage;
import com.example.demo.entities.Personne;
import com.example.demo.entities.Role;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.repositories.ParametrageRepository;
import com.example.demo.repositories.PersonneRepository;
import com.example.demo.repositories.UtulisateurRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class RegisterService {

	   @Autowired
	    private PersonneRepository personneRepository;

	    @Autowired
	    private AdherentRepository adherentRepository;

	    @Autowired
	    private UtulisateurRepository utulisateurRepository;
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    
	    @PersistenceContext
	    private EntityManager entityManager;
	    
	    @Autowired
	    private ParametrageRepository parametrageRepository;

	    public boolean verifierCINPourInscription(String cin) {
	        // 1. Vérifie si la personne existe
	        Optional<Personne> personneOpt = personneRepository.findById(cin);
	        if (personneOpt.isEmpty()) return false;

	        // 2. Vérifie s'il a une demande acceptée
	        Personne personne = personneOpt.get();
	        boolean aDemandeAcceptee = personne.getDemandes().stream()
	                .anyMatch(d -> d.getEtat() == EtatDemande.ACCEPTEE);
	        if (!aDemandeAcceptee) return false;

	        // 3. Vérifie s’il n’a pas déjà de compte utilisateur
	        boolean existeDeja = utulisateurRepository.existsByAdherent_Cin(cin);
	        return !existeDeja;
	    }
	    
	    
	    @Transactional
	    public void enregistrerCompte(String cin, String email, String motDePasse) {
	        // Vérifier si la personne existe
	        Personne personne = personneRepository.findById(cin)
	            .orElseThrow(() -> new RuntimeException("Personne non trouvée."));

	        // Vérifier s’il a une demande acceptée
	        boolean demandeAcceptee = personne.getDemandes().stream()
	            .anyMatch(d -> d.getEtat() == EtatDemande.ACCEPTEE);

	        if (!demandeAcceptee) {
	            throw new RuntimeException("La demande n'a pas été acceptée.");
	        }

	        // Vérifier si un utilisateur existe déjà avec ce CIN
	        boolean utilisateurExiste = utulisateurRepository.existsByAdherent_Cin(cin);
	        if (utilisateurExiste) {
	            throw new RuntimeException("Un compte avec ce CIN existe déjà.");
	        }

	        // Vérifier si l’email est déjà utilisé
	        if (utulisateurRepository.findByEmail(email).isPresent()) {
	            throw new RuntimeException("Cet email est déjà utilisé.");
	        }

	        // Créer l'utilisateur
	        Utulisateur utilisateur = new Utulisateur();
	        utilisateur.setEmail(email);
	        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
	        utilisateur.setRole(Role.ADHERENT);
	        utilisateur = utulisateurRepository.save(utilisateur);

	        // Créer l'adhérent
	        Adherent adherent = new Adherent();
	        
	        Personne attachedPersonne = entityManager.merge(personne);
	        adherent.setPersonne(attachedPersonne);
	        adherent.setDateInscription(LocalDate.now());
	        adherent.setUtulisateur(utilisateur);
	        double montantMinimal = parametrageRepository.findById(1L)
                    .map(Parametrage::getMontantMinimalAdhesion)
                    .orElse(30.0); // Valeur par défaut si jamais la ligne n'existe pas

adherent.setMontantMinimalAdhesion(montantMinimal);

	        adherentRepository.save(adherent);
	    }

}
