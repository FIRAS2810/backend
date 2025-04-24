package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.TransfertActionRequestDTO;
import com.example.demo.dto.TransfertActionResponseDTO;
import com.example.demo.entities.Adherent;
import com.example.demo.entities.Cotisation;
import com.example.demo.entities.Parametrage;
import com.example.demo.entities.StatutTransfert;
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


    public TransfertAction creerDemandeTransfert(TransfertActionRequestDTO dto) {
        Adherent vendeur = adherentRepository.findById(dto.getCinVendeur())
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé"));
        Adherent acheteur = adherentRepository.findById(dto.getCinAcheteur())
                .orElseThrow(() -> new RuntimeException("Acheteur non trouvé"));

        // ✅ Vérifier que le vendeur a complété son montant minimal
        var bilan = cotisationService.getEtatCotisationParAdherent(dto.getCinVendeur());
        if (!bilan.isAdhesionComplete()) {
            throw new RuntimeException("Le vendeur n’a pas encore complété son montant minimal d’adhésion.");
        }

        Parametrage params = parametrageRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Paramétrage non trouvé"));

        int nbActDispo = vendeur.getCotisations().stream()
                .mapToInt(c -> c.getNombreActions())
                .sum();

        // 🛑 Vérifier qu’il reste le minimum après le transfert
        if (nbActDispo - dto.getNombreActions() < params.getNbActionsMinimales()) {
            throw new RuntimeException("Le vendeur ne peut pas transférer autant d’actions sans descendre sous le minimum requis.");
        }

        // ✅ Créer une demande de transfert (en attente)
        TransfertAction t = new TransfertAction();
        t.setVendeur(vendeur);
        t.setAcheteur(acheteur);
        t.setNombreActions(dto.getNombreActions());
        t.setDateTransfert(LocalDateTime.now());
        t.setCommentaire(dto.getCommentaire());
        t.setStatut(StatutTransfert.EN_ATTENTE);

        return transfertRepo.save(t);
    }

    // ✅ Validation par l’admin
    public void validerTransfert(Long id) {
        TransfertAction transfert = transfertRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert non trouvé"));

        if (transfert.getStatut() != StatutTransfert.EN_ATTENTE) {
            throw new RuntimeException("Ce transfert a déjà été traité.");
        }

        // Mise à jour des cotisations
        Adherent vendeur = transfert.getVendeur();
        Adherent acheteur = transfert.getAcheteur();
        int nombre = transfert.getNombreActions();

        // 💥 Déduction manuelle (à affiner si cotisation par cotisation plus tard)
        int reste = nombre;
        for (var cotisation : vendeur.getCotisations()) {
            int actions = cotisation.getNombreActions();
            if (actions >= reste) {
                cotisation.setNombreActions(actions - reste);
                break;
            } else {
                reste -= actions;
                cotisation.setNombreActions(0);
            }
        }

        // ➕ Ajouter une cotisation fictive à l’acheteur
        Parametrage params = parametrageRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Paramétrage non trouvé"));

        double valeurAction = params.getValeurAction();
        double montant = nombre * valeurAction;

        // Pas besoin de datePaiement ici, on peut juste mettre une cotisation fictive si nécessaire
        // (à compléter selon structure actuelle)

        Cotisation cotisationAcheteur = new Cotisation();
        cotisationAcheteur.setAdherent(acheteur);
        cotisationAcheteur.setNombreActions(nombre);
        cotisationAcheteur.setMontantVerse(montant);
        cotisationAcheteur.setValeurActionSnapshot(valeurAction);
        cotisationAcheteur.setMontantMinimalSnapshot(params.getMontantMinimalAdhesion());
        cotisationAcheteur.setDatePaiement(LocalDate.now());

        cotisationRepository.save(cotisationAcheteur);

        // ✅ Mettre à jour le statut
        transfert.setStatut(StatutTransfert.VALIDE);
        System.out.println("🔁 Ajout d'une cotisation de " + nombre + " actions pour " + acheteur.getCin());

        transfertRepo.save(transfert);
    }

    // ❌ Refus de la demande
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

    // 📌 Historique d’un adhérent (vendeur ou acheteur)
    public List<TransfertAction> getHistoriquePourAdherent(String cin) {
        return transfertRepo.findByVendeurCinOrAcheteurCin(cin, cin);
    }
    
    public List<TransfertActionResponseDTO> getTousTransfertsAvecNoms() {
        return transfertRepo.findAll().stream().map(t ->
            new TransfertActionResponseDTO(
                t.getId(),
                t.getNombreActions(),
                t.getCommentaire(),
                t.getDateTransfert(),
                t.getStatut().name(),
                t.getVendeur().getPersonne().getNom() + " " + t.getVendeur().getPersonne().getPrenom(),
                t.getAcheteur().getPersonne().getNom() + " " + t.getAcheteur().getPersonne().getPrenom()
            )
        ).toList();
    }

    
    

}
