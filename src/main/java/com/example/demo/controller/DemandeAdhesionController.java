package com.example.demo.controller;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;

import com.example.demo.dto.DemandeAdhesionRequestDTO;
import com.example.demo.dto.DemandeAdhesionResponseDTO;
import com.example.demo.entities.DemandeAdhesion;
import com.example.demo.entities.EtatDemande;
import com.example.demo.repositories.DemandeAdhesionRepository;
import com.example.demo.service.DemandeAdhesionService;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeAdhesionController {
	
	@Autowired
    private DemandeAdhesionService demandeService;
	
	@Autowired
    private DemandeAdhesionRepository demandeRepository;
	
	private final DemandeAdhesionService demandeAdhesionService;

    DemandeAdhesionController(DemandeAdhesionService demandeAdhesionService) {
        this.demandeAdhesionService = demandeAdhesionService;
    }

    @PostMapping("/soumettre")
    public ResponseEntity<?> soumettreDemande(@ModelAttribute DemandeAdhesionRequestDTO dto) {
        try {
            demandeService.enregistrerDemande(dto);
            return ResponseEntity.ok(Map.of("message", "✅ Demande soumise avec succès !"));
        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("❌ Fichier non valide !");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Erreur serveur : " + e.getMessage());
        }
    }
    
 // 🔁 1. Récupérer toutes les demandes (GET)
    /*@GetMapping("/toutes")
    public ResponseEntity<?> getAllDemandes() {
        return ResponseEntity.ok(demandeService.getAllDemandes());
    }*/
    
    /*@GetMapping("/toutes")
    public List<DemandeAdhesionResponseDTO> getAllDemandesAsDTOs() {
        List<DemandeAdhesion> demandes = demandeRepository.findAll();

        return demandes.stream().map(d -> {
            DemandeAdhesionResponseDTO dto = new DemandeAdhesionResponseDTO();
            dto.setId(d.getId());
            dto.setCinPersonne(d.getPersonne().getCin());
            dto.setNom(d.getPersonne().getNom());
            dto.setPrenom(d.getPersonne().getPrenom());
            dto.setEmail(d.getPersonne().getEmail());
            dto.setEtat(d.getEtat().name());
            dto.setDateDemande(d.getDateDemande().toString());
            dto.setTel(d.getPersonne().getTelephone());
            dto.setVille(d.getPersonne().getAdresse()); // ou getVille() si séparé
            dto.setActivite(d.getPersonne().getActivite());
            dto.setDateDecision(d.getDateAcceptation() != null ? d.getDateAcceptation().toString() : null);

            // Récupérer le fichier (le 1er si plusieurs)
            if (d.getJustificatifs() != null && !d.getJustificatifs().isEmpty()) {
                byte[] contenu = d.getJustificatifs().get(0).getContenu();
                String base64 = Base64.getEncoder().encodeToString(contenu);
                dto.setJustificatifBase64(base64);
            }

            return dto;
        }).toList();
    }
    
    
    
   */

    @GetMapping("/toutes")
    public List<DemandeAdhesionResponseDTO> getToutesDemandes() {
        return demandeAdhesionService.getAllDemandes();
    }



    // 🔍 2. Vérifier si une personne a déjà une demande
    @GetMapping("/existe/{cin}")
    public ResponseEntity<Boolean> verifierDemandeExistante(@PathVariable String cin) {
        boolean existe = demandeService.demandeExistePourCin(cin);
        return ResponseEntity.ok(existe);
    }
    
    @PatchMapping("/{id}/statut")
    public void changerStatutDemande(
            @PathVariable Long id,
            @RequestParam EtatDemande etat
    ) {
        // Appeler le service pour modifier l’état et envoyer un mail
        demandeAdhesionService.changerStatutEtNotifier(id, etat);
    }
    
    @GetMapping("/en-attente/count")
    public ResponseEntity<Long> getNombreDemandesEnAttente() {
        long count = demandeRepository.countByEtat(EtatDemande.EN_ATTENTE);
        return ResponseEntity.ok(count);
    }
    
    

}
