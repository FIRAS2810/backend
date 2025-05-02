package com.example.demo.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AdherentProfileDTO;
import com.example.demo.dto.AdherentTableDTO;
import com.example.demo.dto.AdherentUpdateDTO;
import com.example.demo.entities.Adherent;
import com.example.demo.entities.CompteAttente;
import com.example.demo.entities.Cotisation;
import com.example.demo.entities.Parametrage;
import com.example.demo.entities.Personne;
import com.example.demo.entities.Reclamation;
import com.example.demo.entities.StatutAdherent;
import com.example.demo.entities.StatutCompte;
import com.example.demo.entities.StatutReclamation;
import com.example.demo.entities.StatutReglement;
import com.example.demo.entities.TypeCompteAttente;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.repositories.CompteAttenteRepository;
import com.example.demo.repositories.CotisationRepository;
import com.example.demo.repositories.ParametrageRepository;
import com.example.demo.repositories.PersonneRepository;
import com.example.demo.repositories.ReclamationRepository;
import com.example.demo.repositories.UtulisateurRepository;

import jakarta.transaction.Transactional;

@Service
public class AdherentService {
	
	@Autowired
    private UtulisateurRepository utulisateurRepository;
	
	@Autowired
    private PersonneRepository personneRepository;
	
	@Autowired
	private AdherentRepository adherentRepository;
	
	@Autowired
	private CotisationRepository cotisationRepository;
	
	@Autowired
	private CotisationService cotisationService;
	
	@Autowired
    private ParametrageRepository parametrageRepository;
	
	@Autowired
    private CompteAttenteRepository compteAttenteRepository;
	
	@Autowired
	private ReclamationRepository reclamationRepository;

	public AdherentProfileDTO getMonProfil(String email) {
	    Utulisateur user = utulisateurRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

	    Adherent adherent = user.getAdherent();

	    AdherentProfileDTO dto = new AdherentProfileDTO();
	    dto.setNom(adherent.getPersonne().getNom());
	    dto.setPrenom(adherent.getPersonne().getPrenom());
	    dto.setEmail(user.getEmail());
	    dto.setTelephone(adherent.getPersonne().getTelephone());
	    dto.setAdresse(adherent.getPersonne().getAdresse());
	    dto.setActivite(adherent.getPersonne().getActivite());
	    dto.setCin(adherent.getPersonne().getCin());
	    dto.setDateNaissance(adherent.getPersonne().getDateNaissance().toString());
	    dto.setDateAdhesion(adherent.getDateInscription().toString());

	    if (adherent.getPhotoProfil() != null) {
	        dto.setPhotoProfilBase64(Base64.getEncoder().encodeToString(adherent.getPhotoProfil()));
	    }

	    return dto;
	}
	
	
	@Transactional
	public void updateProfil(String email, AdherentUpdateDTO dto) {
	    Utulisateur user = utulisateurRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

	    Personne personne = user.getAdherent().getPersonne();

	    // Mise à jour Personne
	    personne.setNom(dto.getNom());
	    personne.setPrenom(dto.getPrenom());
	    personne.setTelephone(dto.getTelephone());
	    personne.setDateNaissance(LocalDate.parse(dto.getDateNaissance()));

	    personneRepository.save(personne);

	    // Mise à jour Utulisateur
	    user.setEmail(dto.getEmail());

	    utulisateurRepository.save(user);
	}

	
	
	public List<AdherentTableDTO> getAllAdherents() {
	    List<Adherent> adherents = adherentRepository.findAll();

	    return adherents.stream().map(adherent -> {
	        AdherentTableDTO dto = new AdherentTableDTO();
	        dto.setCin(adherent.getCin()); // ✅ fonctionne car "cin" est ta clé
	        dto.setNom(adherent.getPersonne().getNom());
	        dto.setPrenom(adherent.getPersonne().getPrenom());
	        dto.setEmail(adherent.getUtulisateur().getEmail());
	        dto.setCin(adherent.getPersonne().getCin());
	        dto.setTel(adherent.getPersonne().getTelephone());
	        dto.setSexe(adherent.getPersonne().getSexe());
	        dto.setEtat(adherent.getUtulisateur().getStatutCompte().toString());
	        dto.setDateAdhesion(adherent.getDateInscription().toString());
	        dto.setDateFin(null); // si tu veux gérer plus tard la sortie

	        // Montant total et nb actions depuis la table cotisation
	        List<Cotisation> cotisations = cotisationRepository.findByAdherentCin(adherent.getPersonne().getCin());
	        double total = cotisations.stream().mapToDouble(Cotisation::getMontantVerse).sum();
	        int actions = cotisations.stream().mapToInt(Cotisation::getNombreActions).sum();

	        dto.setMontant(total);
	        dto.setNbActions(actions);
	        
	        dto.setNombreActionsCotisees(actions);
	        dto.setNombreActionsRecues(adherent.getNombreActionsRecues());
	        dto.setNombreActionsVendues(adherent.getNombreActionsVendues());

	        if (adherent.getPhotoProfil() != null) {
	            dto.setPhotoProfilBase64(Base64.getEncoder().encodeToString(adherent.getPhotoProfil()));
	        }

	        return dto;
	    }).collect(Collectors.toList());
	}
	
	public void signalerDeces(String cin) {
	    Adherent adherent = adherentRepository.findById(cin)
	        .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

	    // 1. Calcul des données
	    int totalActions = adherent.getNombreActionsRecues() + adherent.getNombreActionsAchetees() - adherent.getNombreActionsVendues();
	    Parametrage params = parametrageRepository.findById(1L)
	        .orElseThrow(() -> new RuntimeException("Paramétrage introuvable"));
	    double valeur = params.getValeurAction();
	    double montantTotal = cotisationService.getEtatCotisationParAdherent(cin).getMontantTotalEstime();


	    // 2. Créer l'objet CompteAttente
	    CompteAttente compte = new CompteAttente();
	    compte.setCinAdherent(cin);
	    compte.setNomComplet(adherent.getPersonne().getNom() + " " + adherent.getPersonne().getPrenom());
	    compte.setMontantTotal(montantTotal);
	    compte.setDateSortie(LocalDate.now());
	    compte.setType(TypeCompteAttente.DÉCÈS);
	    compte.setStatutReglement(StatutReglement.NON_RÉGLÉ);
	    compteAttenteRepository.save(compte);

	    // 3. Mise à jour des statuts
	    adherent.setStatut(StatutAdherent.DÉCÉDÉ);
	    adherentRepository.save(adherent);  // S'assurer que l'adhérent est bien mis à jour avant de continuer

	    // 4. Mise à jour du statut du compte utilisateur
	    if (adherent.getUtulisateur() != null) {
	        Utulisateur u = adherent.getUtulisateur();
	        u.setStatutCompte(StatutCompte.INACTIF);
	        utulisateurRepository.save(u); // Sauvegarder l'utilisateur
	    } else {
	        throw new RuntimeException("Aucun compte utilisateur lié à cet adhérent !");
	    }
	}

	
	public void signalerExclusionTemporaire(String cin) {
	    Adherent a = adherentRepository.findById(cin)
	        .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

	    a.setStatut(StatutAdherent.EXCLU);
	    a.setDateExclusionTemporaire(LocalDate.now()); // ✅ ici
	    adherentRepository.save(a);

	    // ⚠️ Désactiver son compte utilisateur aussi si besoin
	    if (a.getUtulisateur() != null) {
	        Utulisateur u = a.getUtulisateur();
	        u.setStatutCompte(StatutCompte.INACTIF);
	        utulisateurRepository.save(u);
	    }
	}

	@Scheduled(cron = "0 0 3 * * ?") // Tous les jours à 03:00
	public void verifierExclusions() {
	    List<Adherent> adherentsExclus = adherentRepository.findByStatut(StatutAdherent.EXCLU);

	    for (Adherent adherent : adherentsExclus) {
	        LocalDate dateExclusion = adherent.getDateExclusionTemporaire();

	        if (dateExclusion != null && dateExclusion.plusDays(14).isBefore(LocalDate.now())) {
	            String cin = adherent.getCin();

	            Optional<Reclamation> rec = reclamationRepository.findByCin(cin);

	            if (rec.isEmpty() || rec.get().getStatut() == StatutReclamation.EN_ATTENTE) {
	                System.out.println("🔁 Exclusion définitive automatique de l’adhérent CIN = " + cin);
	                signalerExclusionDefinitive(cin); // méthode suivante
	            }
	        }
	    }
	}

	public void signalerExclusionDefinitive(String cin) {
	    Adherent adherent = adherentRepository.findById(cin)
	        .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

	    // 1. Calcul des actions
	    int totalActions = adherent.getNombreActionsRecues()
	                       + adherent.getNombreActionsAchetees()
	                       - adherent.getNombreActionsVendues();

	    Parametrage params = parametrageRepository.findById(1L)
	        .orElseThrow(() -> new RuntimeException("Paramétrage introuvable"));
	    double valeur = params.getValeurAction();
	    double montantTotal = cotisationService.getEtatCotisationParAdherent(cin).getMontantTotalEstime();

	    // 2. Création de l'entrée CompteAttente
	    CompteAttente compte = new CompteAttente();
	    compte.setCinAdherent(cin);
	    compte.setNomComplet(adherent.getPersonne().getNom() + " " + adherent.getPersonne().getPrenom());
	    compte.setMontantTotal(montantTotal);
	    compte.setDateSortie(LocalDate.now());
	    compte.setType(TypeCompteAttente.EXCLUSION); // 🔁 ici
	    compte.setStatutReglement(StatutReglement.NON_RÉGLÉ);
	    compteAttenteRepository.save(compte);

	    // 3. Mise à jour de l’adhérent
	    adherent.setStatut(StatutAdherent.EXCLU); // statut final déjà EXCLU
	    adherentRepository.save(adherent);

	    // 4. Désactivation du compte utilisateur
	    if (adherent.getUtulisateur() != null) {
	        Utulisateur u = adherent.getUtulisateur();
	        u.setStatutCompte(StatutCompte.INACTIF);
	        utulisateurRepository.save(u);
	    } else {
	        throw new RuntimeException("Aucun compte utilisateur lié à cet adhérent !");
	    }
	}


	public long getNombreAdherentsActifs() {
	    return adherentRepository.findAll().stream()
	        .filter(a -> a.getUtulisateur().getStatutCompte() == StatutCompte.ACTIF)
	        .count();
	}

	public long getNombreHommesAdherents() {
	    return adherentRepository.findAll().stream()
	        .filter(a -> a.getUtulisateur().getStatutCompte() == StatutCompte.ACTIF)
	        .filter(a -> "HOMME".equalsIgnoreCase(a.getPersonne().getSexe()))
	        .count();
	}

	public long getNombreFemmesAdherents() {
	    return adherentRepository.findAll().stream()
	        .filter(a -> a.getUtulisateur().getStatutCompte() == StatutCompte.ACTIF)
	        .filter(a -> "FEMME".equalsIgnoreCase(a.getPersonne().getSexe()))
	        .count();
	}

	public int getTotalActionsRecues() {
	    return adherentRepository.findAll().stream()
	            .mapToInt(Adherent::getNombreActionsRecues)
	            .sum();
	}

	public int getTotalActionsVendues() {
	    return adherentRepository.findAll().stream()
	            .mapToInt(Adherent::getNombreActionsVendues)
	            .sum();
	}

	

	public long getTrancheAgeMoins30() {
	    return adherentRepository.findAll().stream()
	        .filter(a -> a.getPersonne() != null)
	        .filter(a -> {
	            int age = Period.between(a.getPersonne().getDateNaissance(), LocalDate.now()).getYears();
	            return age < 30;
	        })
	        .count();
	}

	public long getTrancheAge30a50() {
	    return adherentRepository.findAll().stream()
	        .filter(a -> a.getPersonne() != null)
	        .filter(a -> {
	            int age = Period.between(a.getPersonne().getDateNaissance(), LocalDate.now()).getYears();
	            return age >= 30 && age <= 50;
	        })
	        .count();
	}

	public long getTrancheAgePlus50() {
	    return adherentRepository.findAll().stream()
	        .filter(a -> a.getPersonne() != null)
	        .filter(a -> {
	            int age = Period.between(a.getPersonne().getDateNaissance(), LocalDate.now()).getYears();
	            return age > 50;
	        })
	        .count();
	}

	
}
