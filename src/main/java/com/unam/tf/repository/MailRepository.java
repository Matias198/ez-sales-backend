package com.unam.tf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.unam.tf.model.Mail;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
    
}
