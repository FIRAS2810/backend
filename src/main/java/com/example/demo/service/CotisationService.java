package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BilanCotisationDTO;
import com.example.demo.entities.Adherent;
import com.example.demo.entities.Cotisation;
import com.example.demo.entities.CotisationSummaryDTO;
import com.example.demo.entities.Parametrage;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.repositories.CotisationRepository;

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

        double total = cotisations.stream().mapToDouble(Cotisation::getMontantVerse).sum();
        int totalActions = cotisations.stream().mapToInt(Cotisation::getNombreActions).sum();
        LocalDate dernierVersement = cotisations.stream()
            .map(Cotisation::getDatePaiement)
            .max(LocalDate::compareTo)
            .orElse(null);

        double montantMinimal = cotisations.stream()
            .mapToDouble(Cotisation::getMontantMinimalSnapshot)
            .max()
            .orElse(30.0); // fallback

        double montantRestant = Math.max(0, montantMinimal - total);
        boolean estComplete = total >= montantMinimal;

        return new BilanCotisationDTO(total, totalActions, montantRestant, estComplete, dernierVersement);
    }

    
   

}
