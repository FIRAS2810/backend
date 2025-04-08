package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	    @Autowired
	    private JavaMailSender mailSender;

	    public void envoyerMail(String to, String sujet, String contenu) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(to);
	        message.setSubject(sujet);
	        message.setText(contenu);
	        mailSender.send(message);

	        System.out.println("📨 Mail envoyé à " + to);
	    }
}
