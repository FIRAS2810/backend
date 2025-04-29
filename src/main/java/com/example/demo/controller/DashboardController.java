package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DashboardStatsDTO;
import com.example.demo.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardController {

	 @Autowired
	    private DashboardService dashboardService;

	 @GetMapping("/stats")
	 public ResponseEntity<DashboardStatsDTO> getDashboardStats(){
	        DashboardStatsDTO stats = dashboardService.getStats();
	        return ResponseEntity.ok(stats);
	    }
	 
	 @GetMapping("/tester-croissance")
	 public double testerCroissance(
	         @RequestParam double caActuel,
	         @RequestParam double caPrecedent) {

	     if (caPrecedent == 0) {
	         return 0.0; // éviter division par zéro
	     }
	     return ((caActuel - caPrecedent) / caPrecedent) * 100;
	 }

}
