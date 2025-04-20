package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Parametrage;
import com.example.demo.service.ParametrageService;


@RestController
@RequestMapping("/api/parametrage")
@CrossOrigin("*")
public class ParametrageController {

	
	@Autowired
    private ParametrageService parametrageService;

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
}
