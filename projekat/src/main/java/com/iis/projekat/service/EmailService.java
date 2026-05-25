package com.iis.projekat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    //TODO: ako je ikada potrebno
    private void sendMail(String sendTo, String subject, String body) {}

    public void sendVolunteerWelcomeMail(String sendTo, String name, String surname){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(sendTo);
        mailMessage.setSubject("Welcome");
        mailMessage.setText("Welcome " + name + " " + surname +", \nYou have been registered as a volunteer");
        mailSender.send(mailMessage);
    }
}
