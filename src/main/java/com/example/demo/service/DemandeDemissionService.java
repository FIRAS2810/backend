package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Adherent;
import com.example.demo.entities.CompteAttente;
import com.example.demo.entities.DemandeDemission;
import com.example.demo.entities.Parametrage;
import com.example.demo.entities.StatutAdherent;
import com.example.demo.entities.StatutCompte;
import com.example.demo.entities.StatutDemandeDemission;
import com.example.demo.entities.StatutReglement;
import com.example.demo.entities.TypeCompteAttente;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.AdherentRepository;
import com.example.demo.repositories.CompteAttenteRepository;
import com.example.demo.repositories.DemandeDemissionRepository;
import com.example.demo.repositories.ParametrageRepository;
import com.example.demo.repositories.UtulisateurRepository;

@Service
public class DemandeDemissionService {

	@Autowired
    private DemandeDemissionRepository demissionRepository;

    @Autowired
    private AdherentRepository adherentRepository;
    
    @Autowired
	private CotisationService cotisationService;

    @Autowired
    private UtulisateurRepository utulisateurRepository;

    @Autowired
    private CompteAttenteRepository compteAttenteRepository;

    @Autowired
    private ParametrageRepository parametrageRepository;

    public DemandeDemission envoyerDemande(DemandeDemission demande) {
        return demissionRepository.save(demande);
    }

    public List<DemandeDemission> getAll() {
        return demissionRepository.findAll();
    }

    public void traiterDemission(Long idDemande, boolean accepter) {
        DemandeDemission demande = demissionRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        if (accepter) {
            demande.setStatut(StatutDemandeDemission.ACCEPTEE);
            signalerDemission(demande.getCin());
        } else {
            demande.setStatut(StatutDemandeDemission.REFUSEE);
        }

        demissionRepository.save(demande);
    }

    public void signalerDemission(String cin) {
        Adherent adherent = adherentRepository.findById(cin)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

        int totalActions = adherent.getNombreActionsRecues()
                             + adherent.getNombreActionsAchetees()
                             - adherent.getNombreActionsVendues();

        Parametrage param = parametrageRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Paramétrage introuvable"));

        double valeur = param.getValeurAction();
        double montantTotal = cotisationService.getEtatCotisationParAdherent(cin).getMontantTotalEstime();

        CompteAttente compte = new CompteAttente();
        compte.setCinAdherent(cin);
        compte.setNomComplet(adherent.getPersonne().getNom() + " " + adherent.getPersonne().getPrenom());
        compte.setMontantTotal(montantTotal);
        compte.setDateSortie(java.time.LocalDate.now());
        compte.setType(TypeCompteAttente.DÉMISSION);
        compte.setStatutReglement(StatutReglement.NON_RÉGLÉ);
        compteAttenteRepository.save(compte);

        adherent.setStatut(StatutAdherent.DEMIS);
        adherentRepository.save(adherent);

        if (adherent.getUtulisateur() != null) {
            Utulisateur u = adherent.getUtulisateur();
            u.setStatutCompte(StatutCompte.INACTIF);
            utulisateurRepository.save(u);
        }
    }
}
