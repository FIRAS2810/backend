package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.TransfertActionRequestDTO;
import com.example.demo.dto.TransfertActionResponseDTO;
import com.example.demo.entities.Adherent;
import com.example.demo.entities.Cotisation;
import com.example.demo.entities.Parametrage;
import com.example.demo.entities.StatutTransfert;
import com.example.demo.entities.StatutVendeur;
import com.example.demo.entities.TransfertAction;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.repositories.CotisationRepository;
import com.example.demo.repositories.ParametrageRepository;
import com.example.demo.repositories.TransfertActionRepository;

@Service
public class TransfertActionService {

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private ParametrageRepository parametrageRepository;

    @Autowired
    private TransfertActionRepository transfertRepo;

    @Autowired
    private CotisationService cotisationService;

    @Autowired
    private CotisationRepository cotisationRepository;

    // ✅ Créer une demande de transfert
    // ✅ Création par l'acheteur
    public TransfertAction creerDemandeTransfert(TransfertActionRequestDTO dto) {
        Adherent vendeur = adherentRepository.findById(dto.getCinVendeur())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendeur non trouvé"));

        Adherent acheteur = adherentRepository.findById(dto.getCinAcheteur())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Acheteur non trouvé"));

        boolean dejaEnAttente = transfertRepo.findByAcheteurCin(dto.getCinAcheteur()).stream()
                .anyMatch(t -> t.getStatut() == StatutTransfert.EN_ATTENTE);

            if (dejaEnAttente) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vous avez déjà une demande de transfert en attente.");
            }
        
        Parametrage params = parametrageRepository.findById(1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Paramétrage non trouvé"));

        TransfertAction t = new TransfertAction();
        t.setVendeur(vendeur);
        t.setAcheteur(acheteur);
        t.setNombreActions(dto.getNombreActions());
        t.setDateTransfert(LocalDateTime.now());
        t.setCommentaire(dto.getCommentaire());
        t.setStatut(StatutTransfert.EN_ATTENTE);
        t.setValeurUnitaireTransfert(params.getValeurAction());
        t.setStatutVendeur(StatutVendeur.EN_ATTENTE); // 🆕

        return transfertRepo.save(t);
    }

    // ✅ Le vendeur accepte
    public void accepterParVendeur(Long id) {
        TransfertAction t = transfertRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert non trouvé"));

        if (t.getStatut() != StatutTransfert.EN_ATTENTE)
            throw new RuntimeException("Le transfert a déjà été traité.");

        if (t.getStatutVendeur() != StatutVendeur.EN_ATTENTE)
            throw new RuntimeException("Vous avez déjà répondu à ce transfert.");

        Adherent vendeur = t.getVendeur();
        var bilan = cotisationService.getEtatCotisationParAdherent(vendeur.getCin());
        var params = parametrageRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Paramétrage non trouvé"));

        int actionsDisponibles = bilan.getNombreActionsCotisees()
                + bilan.getNombreActionsRecues()
                - bilan.getNombreActionsVendues();

        if (actionsDisponibles - t.getNombreActions() < params.getNbActionsMinimales()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Vous ne pouvez pas accepter ce transfert car vous ne respecterez plus le minimum d’actions requis.");
        }

        t.setStatutVendeur(StatutVendeur.ACCEPTE);
        transfertRepo.save(t);
    }


    // ❌ Le vendeur refuse
    public void refuserParVendeur(Long id) {
        TransfertAction t = transfertRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert non trouvé"));

        if (t.getStatut() != StatutTransfert.EN_ATTENTE)
            throw new RuntimeException("Le transfert a déjà été traité.");

        t.setStatutVendeur(StatutVendeur.REFUSE);
        t.setStatut(StatutTransfert.REFUSE); // On marque aussi le transfert comme refusé
        transfertRepo.save(t);
    }

    // ✅ Admin valide le transfert si vendeur a accepté
    public void validerTransfert(Long id) {
        TransfertAction t = transfertRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert non trouvé"));

        if (t.getStatut() != StatutTransfert.EN_ATTENTE)
            throw new RuntimeException("Ce transfert a déjà été traité.");

        if (t.getStatutVendeur() != StatutVendeur.ACCEPTE)
            throw new RuntimeException("Le vendeur n’a pas encore accepté ce transfert.");

        Adherent vendeur = t.getVendeur();
        Adherent acheteur = t.getAcheteur();
        int nombre = t.getNombreActions();

        var bilanVendeur = cotisationService.getEtatCotisationParAdherent(vendeur.getCin());
        var params = parametrageRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Paramétrage non trouvé"));

        int actionsRestantes = bilanVendeur.getNombreActionsCotisees()
                + bilanVendeur.getNombreActionsRecues()
                - bilanVendeur.getNombreActionsVendues();

        if (actionsRestantes - nombre < params.getNbActionsMinimales())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le vendeur ne peut pas valider ce transfert sans descendre sous le minimum requis.");

        int reste = nombre;
        for (var cotisation : vendeur.getCotisations()) {
            int actions = cotisation.getNombreActions();
            if (actions == 0) continue;

            if (actions >= reste) {
                cotisation.setNombreActions(actions - reste);
                cotisationRepository.save(cotisation);
                break;
            } else {
                reste -= actions;
                cotisation.setNombreActions(0);
                cotisationRepository.save(cotisation);
            }
        }

        vendeur.setNombreActionsVendues(vendeur.getNombreActionsVendues() + nombre);
        acheteur.setNombreActionsRecues(acheteur.getNombreActionsRecues() + nombre);

        adherentRepository.save(vendeur);
        adherentRepository.save(acheteur);

        t.setStatut(StatutTransfert.VALIDE);
        transfertRepo.save(t);
    }


    // ❌ Refus d’un transfert
    public void refuserTransfert(Long id) {
        TransfertAction transfert = transfertRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert non trouvé"));

        if (transfert.getStatut() != StatutTransfert.EN_ATTENTE) {
            throw new RuntimeException("Ce transfert a déjà été traité.");
        }

        transfert.setStatut(StatutTransfert.REFUSE);
        transfertRepo.save(transfert);
    }

    // 📋 Récupérer tous les transferts
    public List<TransfertAction> getTousTransferts() {
        return transfertRepo.findAll();
    }

    // 📌 Historique d’un adhérent
    public List<TransfertAction> getHistoriquePourAdherent(String cin) {
        return transfertRepo.findByVendeurCinOrAcheteurCin(cin, cin);
    }

    // 🔁 Liste formatée pour frontend
    public List<TransfertActionResponseDTO> getTousTransfertsAvecNoms() {
        return transfertRepo.findAll().stream().map(t ->
                new TransfertActionResponseDTO(
                        t.getId(),
                        t.getNombreActions(),
                        t.getCommentaire(),
                        t.getDateTransfert(),
                        t.getStatut().name(),
                        t.getVendeur().getPersonne().getNom() + " " + t.getVendeur().getPersonne().getPrenom(),
                        t.getAcheteur().getPersonne().getNom() + " " + t.getAcheteur().getPersonne().getPrenom(),
                        t.getAcheteur().getCin(),
                        t.getVendeur().getCin(),
                        t.getStatutVendeur().name() 
                )
        ).toList();
    }
    
 // 📌 Liste brute sans filtre (admin)
    public List<TransfertActionResponseDTO> getTousPourAdmin() {
        return transfertRepo.findAll().stream().filter(t -> t.getStatutVendeur() == StatutVendeur.ACCEPTE).map(t ->
                new TransfertActionResponseDTO(
                        t.getId(),
                        t.getNombreActions(),
                        t.getCommentaire(),
                        t.getDateTransfert(),
                        t.getStatut().name(),
                        t.getVendeur().getPersonne().getNom() + " " + t.getVendeur().getPersonne().getPrenom(),
                        t.getAcheteur().getPersonne().getNom() + " " + t.getAcheteur().getPersonne().getPrenom(),
                        t.getAcheteur().getCin(),
                        t.getVendeur().getCin(),
                        t.getStatutVendeur().name() 
                )
        ).toList();
    }

}


    
    

