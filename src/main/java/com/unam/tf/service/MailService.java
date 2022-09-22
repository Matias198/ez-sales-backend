package com.unam.tf.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.Mail;
import com.unam.tf.repository.MailRepository;

@Service
public class MailService implements IMailService{

    @Autowired
    MailRepository mailRepository;

    @Override
    public void crearMail(Mail mail) {
        mailRepository.save(mail);        
    }

    @Override
    public void borrarMail(Long id) {
        mailRepository.deleteById(id);        
    }

    @Override
    public Mail buscarMail(Long id) { 
        return mailRepository.findById(id).orElse(null);
    }

    @Override
    public List<Mail> buscarTodosLosMail() {        
        return mailRepository.findAll();
    }

    @Override
    public Mail getMailJson(String mailJson){
        Mail mail = new Mail();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            mail = objectMapper.readValue(mailJson, Mail.class);
            return mail;
        }catch (Exception e){
            System.out.println("Error en el mapeo de objeto mail");
            return null;
        }
        
    }
     
}
