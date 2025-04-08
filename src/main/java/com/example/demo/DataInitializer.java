package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Role;
import com.example.demo.entities.Utulisateur;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.UtulisateurRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
    private UtulisateurRepository utulisateurRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (utulisateurRepository.findByEmail("admin@mail.com").isEmpty()) {
            Utulisateur utulisateur = new Utulisateur();
            utulisateur.setEmail("admin@mail.com");
            utulisateur.setMotDePasse(passwordEncoder.encode("admin123"));
            utulisateur.setRole(Role.ADMIN);

            utulisateur = utulisateurRepository.save(utulisateur);

            Admin admin = new Admin();
            admin.setUtilisateur(utulisateur);
            admin.setNom("Admin Principal");
         

            adminRepository.save(admin);

        }
}
}
