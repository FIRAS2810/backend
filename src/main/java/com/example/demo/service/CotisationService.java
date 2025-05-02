package com.example.demo.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BilanCotisationDTO;
import com.example.demo.entities.Adherent;
import com.example.demo.entities.Cotisation;
import com.example.demo.entities.CotisationSummaryDTO;
import com.example.demo.entities.Parametrage;
import com.example.demo.entities.StatutCompte;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.repositories.CotisationRepository;
import com.example.demo.repositories.ParametrageRepository;

@Service
public class CotisationService {

	@Autowired
    private CotisationRepository cotisationRepository;

    @Autowired
    private ParametrageService parametrageService;

    @Autowired
    private AdherentRepository adherentRepository;
    
    
    
  
    
    // ✅ Ajouter une cotisation (calcul automatique du nombre d'actions)
    public Cotisation ajouterCotisation(String cinAdherent, double montantVerse) {
        Adherent adherent = adherentRepository.findById(cinAdherent)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

        Parametrage params = parametrageService.getParametrage();
        
        if (adherent.getMontantMinimalAdhesion() == 0) {
            adherent.setMontantMinimalAdhesion(params.getMontantMinimalAdhesion());
            adherentRepository.save(adherent); // important !
        }

        Cotisation cotisation = new Cotisation();
        cotisation.setAdherent(adherent);
        cotisation.setMontantVerse(montantVerse);

        // Snapshots
        cotisation.setValeurActionSnapshot(params.getValeurAction());
        cotisation.setMontantMinimalSnapshot(params.getMontantMinimalAdhesion());

        // Calcul automatique du nombre d'actions
        int nombreActions = (int) (montantVerse / params.getValeurAction());
        cotisation.setNombreActions(nombreActions);

        return cotisationRepository.save(cotisation);
    }

    // ✅ Afficher les cotisations d'un adhérent
    public List<Cotisation> getCotisationsByAdherent(String cin) {
        return cotisationRepository.findByAdherentCin(cin);
    }
    
    public double getMontantTotalByAdherent(String cin) {
        return cotisationRepository
            .findByAdherentCin(cin)
            .stream()
            .mapToDouble(Cotisation::getMontantVerse)
            .sum();
    }
    
    public int getTotalActionsByAdherent(String cin) {
        return cotisationRepository
            .findByAdherentCin(cin)
            .stream()
            .mapToInt(Cotisation::getNombreActions)
            .sum();
    }
    
    public double getMontantRestantByAdherent(String cin) {
        double total = getMontantTotalByAdherent(cin);
        double valeurAction = parametrageService.getParametrage().getValeurAction();

        return total % valeurAction;
    }
    
    public CotisationSummaryDTO getResumeCotisation(String cin) {
        double total = getMontantTotalByAdherent(cin);
        double valeur = parametrageService.getParametrage().getValeurAction();

        int nbActions = (int)(total / valeur);
        double reste = total % valeur;

        CotisationSummaryDTO dto = new CotisationSummaryDTO();
        dto.setTotalVerse(total);
        dto.setTotalActions(nbActions);
        dto.setMontantRestant(reste);

        return dto;
    }

 

    public BilanCotisationDTO getEtatCotisationParAdherent(String cin) {
        List<Cotisation> cotisations = cotisationRepository.findByAdherentCin(cin);

        // 💰 Montant total versé
        double total = cotisations.stream()
                .mapToDouble(Cotisation::getMontantVerse)
                .sum();

        // ✅ Actions issues des cotisations
        int actionsCotisees = cotisations.stream()
                .mapToInt(Cotisation::getNombreActions)
                .sum();

        // 🔄 Récupérer l'adhérent
        Adherent adherent = adherentRepository.findById(cin)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

        int actionsRecues = adherent.getNombreActionsRecues();
        int actionsVendues = adherent.getNombreActionsVendues();

        // 📊 Total actions détenues actuellement
        int totalActions = actionsCotisees + actionsRecues - actionsVendues;

        // 🗓️ Dernier paiement
        LocalDate dernierVersement = cotisations.stream()
                .map(Cotisation::getDatePaiement)
                .max(LocalDate::compareTo)
                .orElse(null);

        // 📏 Montant minimal requis
        double montantMinimal = adherent.getMontantMinimalAdhesion();


        boolean estComplete = total >= montantMinimal;
        double montantRestant = calculerMontantRestant(adherent);


        // 💵 Valeurs estimées
        double valeurAction = parametrageService.getParametrage().getValeurAction();
        double montantTransfertsEstime = actionsRecues * valeurAction;
        double montantTotalEstime = (total - actionsVendues * valeurAction) + montantTransfertsEstime;

        // ✅ NOUVEAU : solde actuel réel (basé sur actions disponibles)
        double soldeDisponible = totalActions * valeurAction;

        return new BilanCotisationDTO(
                total,
                totalActions,
                actionsCotisees,
                actionsRecues,
                actionsVendues,
                montantRestant,
                estComplete,
                dernierVersement,
                montantTransfertsEstime,
                montantTotalEstime,
                soldeDisponible // ✅ nouveau paramètre
        );
    }


    public double getChiffreAffaires() {
        return cotisationRepository.findAll()
            .stream()
            .mapToDouble(Cotisation::getMontantVerse)
            .sum();
    }

    public double getResteCotisationTotal() {
        List<Adherent> adherentsActifs = adherentRepository.findAll().stream()
                .filter(a -> a.getUtulisateur().getStatutCompte() == StatutCompte.ACTIF)
                .toList();

        return adherentsActifs.stream()
                .mapToDouble(adherent -> {
                    double montantMinimalPourCetAdherent = adherent.getMontantMinimalAdhesion(); // 🟰 au moment où il est devenu adhérent
                    double montantVerse = adherent.getCotisations().stream()
                            .mapToDouble(Cotisation::getMontantVerse)
                            .sum();
                    double resteIndividuel = montantMinimalPourCetAdherent - montantVerse;
                    return Math.max(resteIndividuel, 0);
                })
                .sum();
    }


    public double calculerMontantRestant(Adherent adherent) {
	    double montantMinimal = adherent.getMontantMinimalAdhesion(); // 📌 montant obligatoire stocké dans l'adherent

	    double montantVerse = adherent.getCotisations().stream()
	            .mapToDouble(cotisation -> cotisation.getMontantVerse())
	            .sum(); // 📦 somme des cotisations réellement versées

	    double montantRestant = montantMinimal - montantVerse;

	    return Math.max(montantRestant, 0); // jamais négatif
	}
    
    public double getChiffreAffairesDuMois(LocalDate date) {
        YearMonth mois = YearMonth.from(date);
        Double montant = cotisationRepository.getMontantParMois(mois.atDay(1), mois.atEndOfMonth());
        return montant != null ? montant : 0.0;
    }




}
