package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.DemandeDemission;
import com.example.demo.service.DemandeDemissionService;

@RestController
@RequestMapping("/api/demissions")
@CrossOrigin(origins = "*")
public class DemandeDemissionController {

	 @Autowired
	    private DemandeDemissionService service;

	    // 1️⃣ L’adhérent envoie une demande
	    @PostMapping("/demander")
	    public DemandeDemission demander(@RequestBody DemandeDemission demande) {
	        return service.envoyerDemande(demande);
	    }

	    // 2️⃣ Admin : voir toutes les demandes
	    @GetMapping("/toutes")
	    public List<DemandeDemission> getAll() {
	        return service.getAll();
	    }

	    // 3️⃣ Admin : accepter ou refuser une demande
	    @PatchMapping("/traiter/{id}")
	    public void traiter(@PathVariable Long id, @RequestParam boolean accepter) {
	        service.traiterDemission(id, accepter);
	    }
}
