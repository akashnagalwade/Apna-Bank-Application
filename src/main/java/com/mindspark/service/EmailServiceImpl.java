package com.mindspark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mindspark.dto.EmailDetails;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	
	private String senderEmail;

	@Override
	public void sendEmailAlert(EmailDetails emailDetails) {

		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(senderEmail);
			mailMessage.setTo(emailDetails.getRecipent());
			mailMessage.setText(emailDetails.getMessageBody());
			mailMessage.setSubject(emailDetails.getSubject());
			
			javaMailSender.send(mailMessage);
			System.out.println("Mail sent successfully");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
