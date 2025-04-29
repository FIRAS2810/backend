package com.example.demo.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TransfertActionRequestDTO;
import com.example.demo.dto.TransfertActionResponseDTO;
import com.example.demo.entities.TransfertAction;
import com.example.demo.repositories.TransfertActionRepository;
import com.example.demo.service.TransfertActionService;

@RestController
@RequestMapping("/api/transferts")
@CrossOrigin("*")
public class TransfertActionController {

	@Autowired
    private TransfertActionService transfertService;
	
	@Autowired
	private TransfertActionRepository transfertActionRepository;
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
	    return ResponseEntity.badRequest().body("❌ Erreur : " + ex.getMessage());
	}


    // 📨 Création d’une demande de transfert
    @PostMapping("/demander")
    public TransfertAction demanderTransfert(@RequestBody TransfertActionRequestDTO dto) {
        return transfertService.creerDemandeTransfert(dto);
    }

    // ✅ Validation par l’admin
    @PatchMapping("/{id}/valider")
    public void valider(@PathVariable Long id) {
        transfertService.validerTransfert(id);
    }

    // ❌ Refus par l’admin
    @PatchMapping("/{id}/refuser")
    public void refuser(@PathVariable Long id) {
        transfertService.refuserTransfert(id);
    }

    // 📋 Liste de tous les transferts
    @GetMapping("/tous")
    public List<TransfertAction> getTous() {
        return transfertService.getTousTransferts();
    }
    
    @GetMapping("/tous-avec-noms")
    public List<TransfertActionResponseDTO> getTousAvecNoms() {
    
        return transfertService.getTousTransfertsAvecNoms();
    }


    // 📌 Historique pour un adhérent donné
    @GetMapping("/historique/{cin}")
    public List<TransfertAction> getHistoriquePourAdherent(@PathVariable String cin) {
        return transfertService.getHistoriquePourAdherent(cin);
    }
    
 
 // Contrôleur pour récupérer l'historique des transferts pour un adhérent spécifique
    @GetMapping("/transferts-adherent/{cin}")
    public ResponseEntity<List<TransfertActionResponseDTO>> getHistoriqueTransfertsAdherent(@PathVariable String cin) {
        // Recherche les transferts associés au CIN de l'adhérent (qu'il soit vendeur ou acheteur)
        List<TransfertAction> transferts = transfertActionRepository.findByVendeurCinOrAcheteurCin(cin, cin);
        
        // Si aucun transfert n'est trouvé, retourner un status 204 (No Content)
        if (transferts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
        }

        // Convertir les transferts en TransfertActionResponseDTO
        List<TransfertActionResponseDTO> responseDTOs = transferts.stream()
                .map(t -> new TransfertActionResponseDTO(
                        t.getId(),
                        t.getNombreActions(),
                        t.getCommentaire(),
                        t.getDateTransfert(),
                        t.getStatut().toString(),
                        t.getVendeur().getPersonne().getNom() + " " + t.getVendeur().getPersonne().getPrenom(),
                        t.getAcheteur().getPersonne().getNom() + " " + t.getAcheteur().getPersonne().getPrenom(),
                        t.getAcheteur().getCin(),
                        t.getVendeur().getCin(),
                        t.getStatutVendeur().name() 
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    

    @PatchMapping("/{id}/accepter-par-vendeur")
    public void accepterParVendeur(@PathVariable Long id) {
        transfertService.accepterParVendeur(id);
    }

    @PatchMapping("/{id}/refuser-par-vendeur")
    public void refuserParVendeur(@PathVariable Long id) {
        transfertService.refuserParVendeur(id);
    }

    @GetMapping("/admin/tous")
    public List<TransfertActionResponseDTO> getTousPourAdmin() {
        return transfertService.getTousPourAdmin();
    }



}
