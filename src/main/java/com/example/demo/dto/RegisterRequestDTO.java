package com.example.demo.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {

	private String cin;
	private String email;
    private String motDePasse;
}
