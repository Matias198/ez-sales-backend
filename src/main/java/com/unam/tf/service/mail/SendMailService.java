package com.unam.tf.service.mail;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {
        // Esto es lo que va delante de @gmail.com en tu cuenta de correo. Es el
        // remitente también.

        @Value("${spring.mail.password}")
        String password;

        public boolean sendCustomMail(String toInternetAdress, String subject, String body) {
                String fromInternetAdress = "ezsalesbot@gmail.com"; // Para la dirección nomcuenta@gmail.com
                Properties props = System.getProperties();
                props.put("mail.smtp.host", "smtp.gmail.com"); // El servidor SMTP de Google
                props.put("mail.smtp.user", fromInternetAdress);
                props.put("mail.smtp.clave", password); // La clave de la cuenta
                props.put("mail.smtp.auth", "true"); // Usar autenticación mediante usuario y clave
                props.put("mail.smtp.starttls.enable", "true"); // Para conectar de manera segura al servidor SMTP
                props.put("mail.smtp.port", "587"); // El puerto SMTP seguro de Google

                Session session = Session.getDefaultInstance(props);
                MimeMessage message = new MimeMessage(session);
                MimeMessageHelper helper = new MimeMessageHelper(message);

                try {
                        helper.setSubject(subject);
                        helper.setFrom(fromInternetAdress);
                        helper.setTo(toInternetAdress);
                        boolean html = true;
                        helper.setText(body, html);
                        Transport transport = session.getTransport("smtp");
                        transport.connect("smtp.gmail.com", fromInternetAdress, password);
                        transport.sendMessage(message, message.getAllRecipients());
                        transport.close();
                        System.out.println("Mail enviado");
                        return true;
                } catch (MessagingException e) {
                        e.printStackTrace();
                        System.out.println("Error enviando mensaje: " + e.getMessage() + " " + e.getCause());
                        return false;
                }
        }

        /*
         * @Autowired
         * private JavaMailSender javaMailSender;
         * 
         * public Boolean sendCustomMail(String from, String to, String subject, String
         * body) throws MessagingException {
         * try {
         * MimeMessage message = javaMailSender.createMimeMessage();
         * MimeMessageHelper helper = new MimeMessageHelper(message);
         * helper.setSubject(subject);
         * helper.setFrom(from);
         * helper.setTo(to);
         * boolean html = true;
         * helper.setText(body, html);
         * javaMailSender.send(message);
         * System.out.println("Mail enviado");
         * return true;
         * } catch (Exception e) {
         * System.out.println("Error enviando mensaje: "+ e.getMessage() + " " +
         * e.getCause());
         * return false;
         * }
         * 
         * }
         */
}
