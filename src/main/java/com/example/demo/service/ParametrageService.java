package com.example.demo.service;



import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.HistoriqueParametrage;
import com.example.demo.entities.Parametrage;
import com.example.demo.repositories.HistoriqueParametrageRepository;
import com.example.demo.repositories.ParametrageRepository;

@Service
public class ParametrageService {

	@Autowired
    private ParametrageRepository parametrageRepository;
	
	@Autowired
	private HistoriqueParametrageRepository historiqueRepo;
	
	


    // ✅ Récupérer le paramétrage (il n’y en a qu’un)
    public Parametrage getParametrage() {
        return parametrageRepository.findById(1L).orElse(null);
    }

    // ✅ Mettre à jour les paramètres avec un seul enregistrement ID = 1
    public Parametrage updateParametrage(Long id, Parametrage updatedParam) {
        Parametrage existing = parametrageRepository.findById(id).orElse(null);

        if (existing != null) {
            // ✅ Enregistrer les anciennes valeurs dans l’historique AVANT modification
            HistoriqueParametrage historique = new HistoriqueParametrage();
            historique.setMontantMinimalAdhesion(existing.getMontantMinimalAdhesion());
            historique.setValeurAction(existing.getValeurAction());
            historique.setNbActionsMinimales(existing.getNbActionsMinimales());
            historique.setDateModification(LocalDateTime.now());
            historiqueRepo.save(historique);

            // ✅ Appliquer les nouvelles valeurs
            existing.setMontantMinimalAdhesion(updatedParam.getMontantMinimalAdhesion());
            existing.setValeurAction(updatedParam.getValeurAction());
            existing.setNbActionsMinimales(updatedParam.getNbActionsMinimales());

            return parametrageRepository.save(existing);

        } else {
            // Cas : premier enregistrement
            updatedParam.setId(1L);

            // ✅ Historique aussi pour le 1er ajout (valeurs = celles ajoutées)
            HistoriqueParametrage historique = new HistoriqueParametrage();
            historique.setMontantMinimalAdhesion(updatedParam.getMontantMinimalAdhesion());
            historique.setValeurAction(updatedParam.getValeurAction());
            historique.setNbActionsMinimales(updatedParam.getNbActionsMinimales());
            historique.setDateModification(LocalDateTime.now());
            historiqueRepo.save(historique);

            return parametrageRepository.save(updatedParam);
        }
    }



}
