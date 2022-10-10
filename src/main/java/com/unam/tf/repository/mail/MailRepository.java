package com.unam.tf.repository.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unam.tf.model.mail.Mail;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
    
}
