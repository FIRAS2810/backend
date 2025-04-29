package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	 private final JwtFilter jwtFilter;

	    public SecurityConfig(JwtFilter jwtFilter) {
	        this.jwtFilter = jwtFilter;
	    }

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        return http
	            .csrf(csrf -> csrf.disable())
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/api/auth/**").permitAll()
	                .requestMatchers("/api/register/**").permitAll()
	                .requestMatchers("/api/admin/**").hasRole("ADMIN")
	                .requestMatchers("/api/demandes/soumettre").permitAll()
	                .requestMatchers("/api/demandes/**").permitAll()
	                .requestMatchers("/api/adherents/**").permitAll()
	                .requestMatchers("/api/nouveautes/ajouter").permitAll()
	                .requestMatchers("/api/nouveautes/toutes").permitAll()
	                .requestMatchers("/api/nouveautes/supprimer/**").permitAll()
	                .requestMatchers("/api/nouveautes/modifier/**").permitAll()
	                .requestMatchers("/api/cotisations/ajouter/**").permitAll()
	                .requestMatchers("/api/cotisations/adherent/**").permitAll()
	                .requestMatchers("/api/cotisations/resume/**").permitAll()
	                .requestMatchers("/api/parametrage/**").permitAll()
	                .requestMatchers("/api/reclamations/**").permitAll()
	                .requestMatchers("/api/cotisations/**").permitAll()
	                .requestMatchers("/api/transferts/**").permitAll()
	                .requestMatchers("/api/demissions/**").permitAll()
	                .requestMatchers("/api/compte-attente/**").permitAll()
	                .requestMatchers("/api/dashboard/**").permitAll()
	                .requestMatchers("/api/adherent/**").hasRole("ADHERENT")
	                .requestMatchers("/api/description/**").permitAll()
	                .anyRequest().authenticated()
	            )
	            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	   
	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	        return config.getAuthenticationManager();
	    }

}
