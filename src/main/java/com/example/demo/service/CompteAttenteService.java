package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.CompteAttente;
import com.example.demo.entities.StatutReglement;
import com.example.demo.entities.StatutTransfert;
import com.example.demo.repositories.CompteAttenteRepository;
import com.example.demo.repositories.CotisationRepository;
import com.example.demo.repositories.TransfertActionRepository;

@Service
public class CompteAttenteService {

	@Autowired
    private CompteAttenteRepository compteAttenteRepository;

	
	
	
	@Autowired
	
	
	 public List<CompteAttente> getAllComptesAttente() {
	        return compteAttenteRepository.findAll();  // Récupère tous les comptes de la base de données
	    }
	
    // Méthode pour marquer le compte comme réglé
    public void marquerCommeRegle(Long idCompte) {
        CompteAttente compte = compteAttenteRepository.findById(idCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        // Mise à jour du statut
        compte.setStatutReglement(StatutReglement.RÉGLÉ); // Marque le compte comme réglé
        compteAttenteRepository.save(compte);  // Sauvegarde la modification
    }
    
    

}
