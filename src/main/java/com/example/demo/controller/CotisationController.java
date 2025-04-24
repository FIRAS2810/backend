package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BilanCotisationDTO;
import com.example.demo.entities.Cotisation;
import com.example.demo.entities.CotisationSummaryDTO;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.service.CotisationService;

@RestController
@RequestMapping("/api/cotisations")
@CrossOrigin("*")
public class CotisationController {
	
	@Autowired
    private CotisationService cotisationService;
	
	@Autowired
	private AdherentRepository adherentRepository;

	@PostMapping("/ajouter/{cin}")
    public Cotisation ajouterCotisation(
            @PathVariable String cin, 
            @RequestParam double montantVerse) {
        return cotisationService.ajouterCotisation(cin, montantVerse);
    }

    // ✅ Récupérer les cotisations par adhérent
    @GetMapping("/adherent/{cin}")
    public List<Cotisation> getCotisationsAdherent(@PathVariable String cin) {
        return cotisationService.getCotisationsByAdherent(cin);
    }
    
    @GetMapping("/adherent/{cin}/montant-total")
    public double getMontantTotal(@PathVariable String cin) {
        return cotisationService.getMontantTotalByAdherent(cin);
    }
    
    @GetMapping("/adherent/{cin}/actions-total")
    public int getActionsTotal(@PathVariable String cin) {
        return cotisationService.getTotalActionsByAdherent(cin);
    }

    @GetMapping("/adherent/{cin}/montant-restant")
    public double getMontantRestant(@PathVariable String cin) {
        return cotisationService.getMontantRestantByAdherent(cin);
    }
    
    @GetMapping("/resume/{cin}")
    public CotisationSummaryDTO getResume(@PathVariable String cin) {
        return cotisationService.getResumeCotisation(cin);
    }
    
    @GetMapping("/etat-financier/{cin}")
    public BilanCotisationDTO getEtatFinancierAdherent(@PathVariable String cin) {
        return cotisationService.getEtatCotisationParAdherent(cin);
    }
    
    @GetMapping("/par-email/{email}")
    public List<Cotisation> getCotisationsByEmail(@PathVariable String email) {
        return adherentRepository.findAdherentByEmail(email)
            .map(adherent -> cotisationService.getCotisationsByAdherent(adherent.getCin()))
            .orElseThrow(() -> new RuntimeException("Adhérent non trouvé avec cet email"));
    }



}
