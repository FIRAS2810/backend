package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

import com.example.demo.entities.HistoriqueParametrage;
import com.example.demo.entities.Parametrage;
import com.example.demo.repositories.HistoriqueParametrageRepository;
import com.example.demo.service.ParametrageService;


@RestController
@RequestMapping("/api/parametrage")
@CrossOrigin("*")
public class ParametrageController {

	
	@Autowired
    private ParametrageService parametrageService;
	
	@Autowired
	private HistoriqueParametrageRepository historiqueRepo;

    // ✅ Récupérer le paramétrage actuel
    @GetMapping
    public ResponseEntity<Parametrage> getParametrage() {
        Parametrage p = parametrageService.getParametrage();
        return ResponseEntity.ok(p);
    }

    // ✅ Modifier le paramétrage
    @PutMapping("/{id}")
    public ResponseEntity<Parametrage> updateParametrage(@PathVariable Long id, @RequestBody Parametrage updated) {
        Parametrage p = parametrageService.updateParametrage(id, updated);
        return ResponseEntity.ok(p);
    }
    
    @GetMapping("/historique")
    public List<HistoriqueParametrage> getHistoriqueParametrage() {
        return historiqueRepo.findAll(Sort.by(Sort.Direction.DESC, "dateModification"));
    }
    
    

    /*@PostMapping("/test-historique")
    public ResponseEntity<String> testHistorique() {
        HistoriqueParametrage historique = new HistoriqueParametrage();
        historique.setMontantMinimalAdhesion(40.0);
        historique.setValeurAction(7.0);
        historique.setNbActionsMinimales(10);
        historique.setDateModification(LocalDateTime.now());

        historiqueRepo.save(historique);
        return ResponseEntity.ok("✅ Historique enregistré manuellement !");
    }*/

    
}
