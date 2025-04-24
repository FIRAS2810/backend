package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DemandeAdhesionRequestDTO;
import com.example.demo.dto.DemandeAdhesionResponseDTO;
import com.example.demo.entities.DemandeAdhesion;
import com.example.demo.entities.EtatDemande;
import com.example.demo.entities.Fichier;
import com.example.demo.entities.Personne;
import com.example.demo.entities.TypeFichier;
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
        personne.setSexe(dto.getSexe());
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
        fichier.setNomFichier(dto.getFichier().getOriginalFilename());

        // 🟢 Détecter si image ou PDF
        String contentType = dto.getFichier().getContentType();

        if (contentType != null && contentType.startsWith("image")) {
            fichier.setTypeFichier(TypeFichier.IMAGE);
        } else {
            fichier.setTypeFichier(TypeFichier.PDF);
        }

        fichierRepository.save(fichier);

        System.out.println("✅ Demande enregistrée avec succès !");
    }

    
    public List<DemandeAdhesionResponseDTO> getAllDemandes() {
        return demandeRepository.findAll()
                .stream()
                .map(demande -> {
                    DemandeAdhesionResponseDTO dto = new DemandeAdhesionResponseDTO();
                    dto.setId(demande.getId());
                    dto.setCinPersonne(demande.getPersonne().getCin());
                    dto.setNom(demande.getPersonne().getNom());
                    dto.setPrenom(demande.getPersonne().getPrenom());
                    dto.setSexe(demande.getPersonne().getSexe());
                    dto.setEmail(demande.getPersonne().getEmail());
                    dto.setTel(demande.getPersonne().getTelephone());
                    dto.setVille(demande.getPersonne().getAdresse());
                    dto.setActivite(demande.getPersonne().getActivite());
                    dto.setEtat(demande.getEtat().toString());
                    dto.setDateDemande(String.valueOf(demande.getDateDemande()));

                    if (demande.getDateAcceptation() != null) {
                        dto.setDateDecision(String.valueOf(demande.getDateAcceptation()));
                    }

                    // 🔁 Justificatif : base64 + type
                    if (demande.getJustificatifs() != null && !demande.getJustificatifs().isEmpty()) {
                        byte[] contenu = demande.getJustificatifs().get(0).getContenu();
                        String base64 = Base64.getEncoder().encodeToString(contenu);
                        dto.setJustificatifBase64(base64);
                        dto.setTypeFichier(demande.getJustificatifs().get(0).getTypeFichier().name());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
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
