package com.unam.tf.service.mail;

import java.util.List;

import com.unam.tf.model.mail.Mail;

public interface IMailService {
    public void crearMail(Mail mail);

    public void borrarMail(Long id);

    public Mail buscarMail(Long id);

    public List<Mail> buscarTodosLosMail(); 

    public Mail getMailJson(String mailJson); 
}
