package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Parametrage;
import com.example.demo.repositories.ParametrageRepository;

@Service
public class ParametrageService {

	@Autowired
    private ParametrageRepository parametrageRepository;

    // ✅ Récupérer le paramétrage (il n’y en a qu’un)
    public Parametrage getParametrage() {
        return parametrageRepository.findById(1L).orElse(null);
    }

    // ✅ Mettre à jour les paramètres avec un seul enregistrement ID = 1
    public Parametrage updateParametrage(Long id, Parametrage updatedParam) {
        Parametrage existing = parametrageRepository.findById(id).orElse(null);

        if (existing != null) {
            existing.setMontantMinimalAdhesion(updatedParam.getMontantMinimalAdhesion());
            existing.setValeurAction(updatedParam.getValeurAction());
            return parametrageRepository.save(existing);
        } else {
            // Si rien n'existe, on crée un nouveau avec id = 1
            updatedParam.setId(1L);
            return parametrageRepository.save(updatedParam);
        }
    }
}
