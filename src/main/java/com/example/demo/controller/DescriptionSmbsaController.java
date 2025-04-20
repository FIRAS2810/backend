package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DescriptionDTO;
import com.example.demo.service.DescriptionSmbsaService;

@RestController
@RequestMapping("/api/description")
@CrossOrigin(origins = "*")
public class DescriptionSmbsaController {

	private final DescriptionSmbsaService service;
	 
    public DescriptionSmbsaController(DescriptionSmbsaService service) {
        this.service = service;
    }

    @GetMapping
    public DescriptionDTO getDescription() {
        return service.getDescription();
    }

    @PutMapping
    public void updateDescription(@RequestBody DescriptionDTO dto) {
        System.out.println("📩 Reçu : " + dto.getContenuHtml()); // 👈 pour test
        service.updateDescription(dto);
    }
}
