package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Adherent;
import com.example.demo.entities.Reclamation;
import com.example.demo.entities.StatutAdherent;
import com.example.demo.entities.StatutCompte;
import com.example.demo.entities.StatutReclamation;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.repositories.ReclamationRepository;
import com.example.demo.repositories.UtulisateurRepository;

@Service
public class ReclamationService {

	@Autowired
    private ReclamationRepository reclamationRepository;
	
	@Autowired
    private AdherentService adherentService;
	
	 @Autowired
	   private UtulisateurRepository utulisateurRepository;
	
	@Autowired
    private AdherentRepository adherentRepository;

    public Reclamation soumettreReclamation(Reclamation reclamation) {
        return reclamationRepository.save(reclamation);
    }
    
    public void traiterReclamation(Long idReclamation, boolean accepter) {
        // 1. Récupérer la réclamation
        Reclamation reclamation = reclamationRepository.findById(idReclamation)
                .orElseThrow(() -> new RuntimeException("Réclamation introuvable"));

        // 2. Récupérer l'adhérent concerné
        Adherent adherent = adherentRepository.findById(reclamation.getCin())
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

        if (accepter) {
            // ✅ CAS : ACCEPTÉ
            reclamation.setStatut(StatutReclamation.ACCEPTEE);

            adherent.setStatut(StatutAdherent.VIVANT);
            if (adherent.getUtulisateur() != null) {
                Utulisateur u = adherent.getUtulisateur();
                u.setStatutCompte(StatutCompte.ACTIF);
                utulisateurRepository.save(u);
            }

            adherentRepository.save(adherent);

        } else {
            // ❌ CAS : REFUSÉ → Exclusion définitive
            reclamation.setStatut(StatutReclamation.REFUSEE);

            // Appelle la méthode qui transfère les données dans CompteAttente
            adherentService.signalerExclusionDefinitive(adherent.getCin());
        }

        // 3. Sauvegarder la mise à jour de la réclamation
        reclamationRepository.save(reclamation);
    }

}
