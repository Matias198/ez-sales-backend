package com.unam.tf.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {

        @Autowired
        private JavaMailSender javaMailSender;

        public void sendMail(String from, String to, String subject, String body){
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setFrom(from);
                simpleMailMessage.setTo(to);
                simpleMailMessage.setSubject(subject);
                simpleMailMessage.setText(body);
                javaMailSender.send(simpleMailMessage);
        }

        public void sendCustomMail(String from, String to, String subject, String body) throws MessagingException{
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setSubject(subject);
                helper.setFrom(from);
                helper.setTo(to);
                boolean html = true;
                helper.setText(body, html);
                javaMailSender.send(message); 
        }
}
