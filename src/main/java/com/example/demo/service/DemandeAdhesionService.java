package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DemandeAdhesionRequestDTO;
import com.example.demo.entities.DemandeAdhesion;
import com.example.demo.entities.EtatDemande;
import com.example.demo.entities.Fichier;
import com.example.demo.entities.Personne;
import com.example.demo.repositories.DemandeAdhesionRepository;
import com.example.demo.repositories.FichierRepository;
import com.example.demo.repositories.PersonneRepository;

import jakarta.transaction.Transactional;

@Service
public class DemandeAdhesionService {

	@Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private DemandeAdhesionRepository demandeRepository;

    @Autowired
    private FichierRepository fichierRepository;
    
    @Autowired
    private MailService mailService;

    @Transactional
    public void enregistrerDemande(DemandeAdhesionRequestDTO dto) throws IOException {

        // 1️⃣ Création ou mise à jour de la personne
        Personne personne = new Personne();
        personne.setCin(dto.getCin());
        personne.setNom(dto.getNom());
        personne.setPrenom(dto.getPrenom());
        personne.setEmail(dto.getEmail());
        personne.setTelephone(dto.getTelephone());
        personne.setAdresse(dto.getAdresse());
        personne.setActivite(dto.getActivite());
        personne.setDateNaissance(LocalDate.parse(dto.getDateNaissance()));

        personneRepository.save(personne);

        // 2️⃣ Création de la demande
        DemandeAdhesion demande = new DemandeAdhesion();
        demande.setPersonne(personne);
        demande.setEtat(EtatDemande.EN_ATTENTE);
        demande.setDateDemande(LocalDate.now());

        demande = demandeRepository.save(demande);

        // 3️⃣ Enregistrement du fichier
        Fichier fichier = new Fichier();
        fichier.setDemande(demande);
        fichier.setContenu(dto.getFichier().getBytes());

        fichierRepository.save(fichier);

        System.out.println("✅ Demande enregistrée avec succès !");
    }
    
    public List<DemandeAdhesion> getAllDemandes() {
        return demandeRepository.findAll();
    }
    
    public boolean demandeExistePourCin(String cin) {
        return demandeRepository.existsByPersonneCin(cin);
    }
    
    

    public void changerStatutEtNotifier(Long id, EtatDemande etat) {
        // Rechercher la demande
        DemandeAdhesion demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Mettre à jour l’état
        demande.setEtat(etat);

        if (etat == EtatDemande.ACCEPTEE) {
            demande.setDateAcceptation(LocalDate.now());
        }

        // Sauvegarder la demande
        demandeRepository.save(demande);

        // Envoyer un mail à la personne concernée
        String sujet = (etat == EtatDemande.ACCEPTEE)
                ? "Votre demande a été acceptée"
                : "Votre demande a été refusée";

        String contenu = (etat == EtatDemande.ACCEPTEE)
                ? "Félicitations, votre demande a été acceptée. Vous pouvez maintenant créer votre compte."
                : "Nous sommes désolés, votre demande a été refusée.";

        mailService.envoyerMail(demande.getPersonne().getEmail(), sujet, contenu);
    }


}
