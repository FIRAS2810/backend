package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.DescriptionDTO;
import com.example.demo.entities.DescriptionSmbsa;
import com.example.demo.repositories.DescriptionSmbsaRepository;

@Service
public class DescriptionSmbsaService {

	
	 private final DescriptionSmbsaRepository repo;
	 
     public DescriptionSmbsaService(DescriptionSmbsaRepository repo) {
         this.repo = repo;
     }
 
     public DescriptionDTO getDescription() {
         List<DescriptionSmbsa> all = repo.findAll();
         if (all.isEmpty()) {
             return new DescriptionDTO(); // ou renvoyer null
         }
 
         DescriptionSmbsa d = all.get(0);
         DescriptionDTO dto = new DescriptionDTO();
         dto.setContenuHtml(d.getContenuHtml());
         return dto;
     }
 
     public void updateDescription(DescriptionDTO dto) {
         List<DescriptionSmbsa> all = repo.findAll();
         DescriptionSmbsa desc;
 
         if (all.isEmpty()) {
             // 🔥 Nouveau si vide
             desc = new DescriptionSmbsa();
         } else {
             // 🔁 Modifier l'existant
             desc = all.get(0);
         }
 
         desc.setContenuHtml(dto.getContenuHtml());
         repo.save(desc); // insert ou update
     }
}
