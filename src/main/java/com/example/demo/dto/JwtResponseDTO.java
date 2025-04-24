package com.example.demo.dto;

import lombok.Data;

@Data
public class JwtResponseDTO {

	private String token;
    private String role;
    private String email;
    
    
    
    public JwtResponseDTO(String token, String role, String email) {
        this.token = token;
        this.role = role;
        this.email = email;
        
    }

}
