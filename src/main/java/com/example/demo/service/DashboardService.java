package com.example.demo.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DashboardStatsDTO;


@Service
public class DashboardService {

	@Autowired
    private AdherentService adherentService;

    @Autowired
    private CotisationService cotisationService;

    

    @Autowired
    private DemandeAdhesionService demandeService;

    @Autowired
    private PersonneService personneService;

	    public DashboardStatsDTO getStats() {
	        DashboardStatsDTO dto = new DashboardStatsDTO();

	        dto.setChiffreAffaires(cotisationService.getChiffreAffaires());
	        dto.setNbAdherents(adherentService.getNombreAdherentsActifs());
	        dto.setNbAdhesions(demandeService.getNombreTotalDemandes());
	        dto.setValeurPart(5);
	        dto.setResteCotisation(cotisationService.getResteCotisationTotal());
	        dto.setTotalCA(dto.getChiffreAffaires());
	        double moyenneMensuelle = dto.getChiffreAffaires() / LocalDate.now().getMonthValue();
	        dto.setPrevisionCA(moyenneMensuelle * 12);
	        double caActuel = cotisationService.getChiffreAffairesDuMois(LocalDate.now());
	        double caPrecedent = cotisationService.getChiffreAffairesDuMois(LocalDate.now().minusMonths(1));
	        System.out.println("CA actuel : " + caActuel);
	        System.out.println("CA précédent : " + caPrecedent);
	        dto.setCaActuel(caActuel);
	        dto.setCaPrecedent(caPrecedent);


	        double croissancePourcent = 0;
	        if (caPrecedent > 0) {
	            croissancePourcent = ((caActuel - caPrecedent) / caPrecedent) * 100;
	        }
	        dto.setCroissancePourcent(croissancePourcent);

	        dto.setNbHommes(adherentService.getNombreHommesAdherents());
	        dto.setNbFemmes(adherentService.getNombreFemmesAdherents());
	        dto.setNombreActionsTransferees(adherentService.getTotalActionsRecues());
	        dto.setNombreActionsVendues(adherentService.getTotalActionsVendues());
	        dto.setNbDemandesValidees(demandeService.getNombreDemandesValidees());
	        dto.setNbDemandesRefusees(demandeService.getNombreDemandesRefusees());
	        dto.setNbDemandesEnAttente(demandeService.getNombreDemandesEnAttente());
	        dto.setNbMoins30(adherentService.getTrancheAgeMoins30());
	        dto.setNbEntre30et50(adherentService.getTrancheAge30a50());
	        dto.setNbPlus50(adherentService.getTrancheAgePlus50());


	        return dto;
	    }
	
}
