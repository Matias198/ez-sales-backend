package com.unam.tf.controller.mail;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unam.tf.model.mail.Mail;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.service.mail.MailService;

@RestController
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping("/mail/crearMail")
    public ResponseEntity<?> crearMail(@ModelAttribute Mail mail) throws URISyntaxException {
        try {
            Mail mailTemp = new Mail();
            mail.setId(mailTemp.getId());
            mailService.crearMail(mail);
            return new ResponseEntity<Mensaje>(new Mensaje("Mail creado con exito, id: " + mail.getId()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error al crear mail: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mail/obtenerMail/{id}/")
    public ResponseEntity<?> obtenerMail(@PathVariable Long id) throws URISyntaxException {
        try {
            return new ResponseEntity<Mail>(mailService.buscarMail(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error al obtener mail: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mail/obtenerTodos")
    public ResponseEntity<?> obtenerTodos(@PathVariable Long id) throws URISyntaxException {
        try {
            return new ResponseEntity<List<Mail>>(mailService.buscarTodosLosMail(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error al obtener los mails: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/mail/eliminarMail/{id}/")
    public ResponseEntity<?> eliminarMail(@PathVariable Long id) throws URISyntaxException {
        try {
            mailService.borrarMail(id);
            return new ResponseEntity<Mensaje>(new Mensaje("Eliminado con exito"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error al eliminar mail: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    
}
