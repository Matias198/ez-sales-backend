package com.unam.tf.controller.cliente;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.cliente.Cliente;
import com.unam.tf.model.dto.FormularioDto;
import com.unam.tf.model.mail.Mail;
import com.unam.tf.model.tienda.Tienda;
import com.unam.tf.model.ubicacion.Ubicacion;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.cliente.IClienteService;
import com.unam.tf.service.mail.MailService;
import com.unam.tf.service.mail.SendMailService;
import com.unam.tf.service.ubicacion.CiudadService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
public class ClienteController {
    
    @Value("${spring.mail.username}")
    String mailSuperusuario;

    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    private IClienteService clienteService;

    @Autowired
    private UService uService;

    @Autowired
    private MailService mailService;

    @Autowired
    private CiudadService ciudadService;

    @Autowired
    private UService usuarioService;
    
    @Autowired
    private SendMailService sendMailService;

    @GetMapping("cliente/obtenerUnCliente")
    @ResponseBody
    public ResponseEntity<?> obtenerUnCliente(@RequestParam Long dniAutenticado, @RequestParam Long dniCliente) throws URISyntaxException {
        try {  
            Boolean esAdmin = false;
            Set<RolNombre> roles = new HashSet<>();
            uService.getUsuarioByDni(dniAutenticado).get().getRoles().forEach((rol) ->{
                roles.add(rol.getRolNombre());
            });
            for (int i = 0; i < roles.size(); i++){
                if (roles.contains(RolNombre.ROL_ADMINISTRADOR)){
                    esAdmin = true;
                }
            }
            if (dniAutenticado.equals(dniCliente)){
                esAdmin = true;
            }
            if (esAdmin){
                return new ResponseEntity<Cliente>(uService.getClienteAsociado(dniCliente), HttpStatus.OK);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }          
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Persona inexistente con DNI: " + dniCliente), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("cliente/obtenerTodosLosClientes")
    @ResponseBody
    public ResponseEntity<?> obtenerTodosLosClientes(@RequestParam Long dniAutenticado) throws URISyntaxException {
        try { 
            Boolean esAdmin = false;
            Set<RolNombre> roles = new HashSet<>();
            uService.getUsuarioByDni(dniAutenticado).get().getRoles().forEach((rol) ->{
                roles.add(rol.getRolNombre());
            });
            for (int i = 0; i < roles.size(); i++){
                if (roles.contains(RolNombre.ROL_ADMINISTRADOR)){
                    esAdmin = true;
                }
            }
            if (esAdmin){
                return new ResponseEntity<List<Cliente>>(clienteService.buscarTodosLosClientes(), HttpStatus.OK);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }   
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: "+ e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "cliente/editarUsuario", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    @ResponseBody   
    public ResponseEntity<?> editarUsuario(@RequestPart String formularioJson, @RequestPart MultipartFile fotoP, @RequestPart MultipartFile fotoB, @RequestParam Long dni, BindingResult bindingResult) {
        UsuarioJwt usuarioCopy = new UsuarioJwt();
        Cliente clienteCopy = new Cliente();
        Mail mailCopy = new Mail();
        try {
            ObjectMapper mapper = new ObjectMapper();
            FormularioDto formularioDto = mapper.readValue(formularioJson, FormularioDto.class);

            if (formularioDto.getNombreCliente().equals("")||
                formularioDto.getApellidoCliente().equals("")||
                formularioDto.getMail().equals("")||
                formularioDto.getNombreCiudad().equals("")){
                return new ResponseEntity<Mensaje>(new Mensaje("Campos vacíos."), HttpStatus.BAD_REQUEST); 
            }

            if (formularioDto.getDni() == null || formularioDto.getPassword() == ""){
                return new ResponseEntity<Mensaje>(new Mensaje("DNI o contraseña no validos."), HttpStatus.BAD_REQUEST); 
            }

            System.out.println(formularioJson); 
            System.out.println(fotoP);
            System.out.println(fotoB); 
            System.out.println(dni);
            System.out.println(formularioDto.getDni() == dni);
            
            System.out.println("Corroborando Usuario");
            if (!formularioDto.getDni().equals(dni)){
                return new ResponseEntity<Mensaje>(new Mensaje("DNI no valido con su usuario."), HttpStatus.BAD_REQUEST);
            } 
            System.out.println("Autenticando Usuario");
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(formularioDto.getDni(), formularioDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (authentication.isAuthenticated()){
                System.out.println("Autenticado con exito");
                UsuarioJwt user = usuarioService.getUsuarioByDni(formularioDto.getDni()).get();
                Cliente cliente = user.getCliente();
                Mail mail = cliente.getMail();
                /* Copias de seguridad */
                mailCopy = mail;
                usuarioCopy = user;
                clienteCopy = cliente;
                /* Verifica existencia del mail */
                List<Mail> mails = mailService.buscarTodosLosMail();
                mails.remove(mail);
                boolean mailChanged = false;
                System.out.println("Verificando cambio de mail");
                if (!mail.getMail().equals(formularioDto.getMail())){
                    for (int i = 0; i < mails.size(); i++) {
                        if (mail.getMail().equals(mails.get(i).getMail())){
                            System.out.println("Mail duplicado, error");
                            return new ResponseEntity<Mensaje>(new Mensaje("Mail ya existente."), HttpStatus.BAD_REQUEST);
                        }
                    }
                    mailChanged = true;
                } 
                /* Continua con la modificacion */

                /* Modifica datos del cliente */
                System.out.println("Editando informacion de cliente");
                cliente.setNombre(formularioDto.getNombreCliente());
                cliente.setApellido(formularioDto.getApellidoCliente());
                cliente.setFotoPerfil(fotoP.getBytes());
                cliente.setFotoBanner(fotoB.getBytes());
                cliente.setCiudad(ciudadService.buscarCiudad(formularioDto.getCodCiudad()));

                /* Invalidando usuario */ 
                if (mailChanged){
                    System.out.println("Inactivando usuario");
                    user.setActivo(false);
                    /* Invalidando y re creando UUID mail */
                    System.out.println("Setting mail");
                    mail.setValidado(false);
                    mail.setMail(formularioDto.getMail()); 
                    mail.setCodigo(UUID.randomUUID().toString()); 
                }
                 
                try {
                    usuarioService.save(user);
                    mailService.crearMail(mail);
                    clienteService.crearCliente(cliente);
                    if (mailChanged){
                        /* Enviando mail de verificacion */
                        String fromInternetAdress = mailSuperusuario;
                        String toInternetAdress = cliente.getMail().getMail();
                        System.out.println("Enviando mail desde "+fromInternetAdress+" hacia "+toInternetAdress);
                        String subject = "Verificar Mail";
                        String link = "http://localhost:8080/auth/validarMail/"+mail.getId()+"/?codigo="+mail.getCodigo();
                        //String link = "https://ez-sales-api.herokuapp.com/auth/validarMail/"+mail.getId()+"/?codigo="+mail.getCodigo();
                        String body = "<div style='width: 100%;'><div style='text-align: center;'><h1 style='font-family: Lucida Console;font-size: 20px;letter-spacing: 0px;word-spacing: 0px;color: #0a0a0a;font-weight: normal;text-decoration: none;font-style: normal;font-variant: normal;text-transform: none;'>EZ Sales - Mail Verification</h1></br><h2 style='font-family: Impact;font-size: 20px;letter-spacing: 0px;word-spacing: 0px;color: #0a0a0a;font-weight: normal;text-decoration: none;font-style: normal;font-variant: small-caps;text-transform: none;'>Para activar su cuenta presione el boton validar a continuacion:<h2></br><a href='"+link+"' style='background:linear-gradient(to bottom, #19ff56 5%, #4cb015 100%);background-color:#19ff56;border-radius:31px;border:1px solid #000000;display:inline-block;cursor:pointer;color:#0a0a0a;font-family:Arial;font-size:23px;font-weight:bold;font-style:italic;padding:12px 44px;text-decoration:none;text-shadow:0px 1px 0px #000000;'>Validar</a></div></div>";
                        System.out.println("Enviando mail de verificacion");
                        //Boolean valor = sendMailService.sendCustomMail(fromInternetAdress, toInternetAdress, subject, body);
                        Boolean valor = sendMailService.sendCustomMail(toInternetAdress, subject, body);
                        if (valor){
                            System.out.println("Mail enviado con exito");
                        }else{
                            return new ResponseEntity<Mensaje>(new Mensaje("Error al registrar datos o enviar mail."), HttpStatus.BAD_REQUEST);
                        }
                    } 
                    System.out.println("Registrado con exito");
                    return new ResponseEntity<Mensaje>(new Mensaje("Modificado con exito, verifique su mail."), HttpStatus.OK);
                } catch (Exception e) {
                    user = usuarioCopy;
                    mail = mailCopy;
                    cliente = clienteCopy;
                    usuarioService.save(user);
                    mailService.crearMail(mail);
                    clienteService.crearCliente(cliente);
                    return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
                }  
            } else{
                return new ResponseEntity<Mensaje>(new Mensaje("Error en el usuario y contraseña."), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("cliente/obtenerFotosCliente")
    @ResponseBody
    public ResponseEntity<?> obtenerFotosCliente(@RequestParam Long dniAutenticado, @RequestParam Long dniCliente) throws URISyntaxException {
        try {  
            Boolean esAdmin = false;
            Set<RolNombre> roles = new HashSet<>();
            Cliente cliente = uService.getClienteAsociado(dniAutenticado);
            uService.getUsuarioByDni(dniAutenticado).get().getRoles().forEach((rol) ->{
                roles.add(rol.getRolNombre());
            });
            for (int i = 0; i < roles.size(); i++){
                if (roles.contains(RolNombre.ROL_ADMINISTRADOR)){
                    esAdmin = true;
                }
            }
            if (esAdmin || cliente.getUsuariojwt().getDniUsuario().equals(dniCliente)){
                try {
                    byte[] byteBanner = uService.getClienteAsociado(dniCliente).getFotoBanner();
                    byte[] bytePerfil = uService.getClienteAsociado(dniCliente).getFotoPerfil();
                    
                    //Perfil
                    String nombrePerfil = UUID.randomUUID().toString() +"_"+dniCliente+".png";
                    Path pathPerfil = Paths.get("uploads").resolve(nombrePerfil).toAbsolutePath();
                    BufferedImage imgPerfil = null;
                    imgPerfil = ImageIO.read(new ByteArrayInputStream(bytePerfil));
                    File fileimgPerfil = new File(pathPerfil.toString());                    
                    ImageIO.write(imgPerfil, "png", fileimgPerfil);

                    //Banner
                    String nombreBanner = UUID.randomUUID().toString() +"_"+dniCliente+".png";
                    Path pathBanner = Paths.get("uploads").resolve(nombreBanner).toAbsolutePath();
                    BufferedImage imgBanner = null;
                    imgBanner = ImageIO.read(new ByteArrayInputStream(byteBanner));
                    File fileimgBanner = new File(pathBanner.toString());                    
                    ImageIO.write(imgBanner, "png", fileimgBanner);

                    System.out.println("Imagenes guardadas localmente en uploads");
                    
                    return new ResponseEntity<Mensaje>(new Mensaje("Guardado localmente"), HttpStatus.OK);    
                } catch (Exception e) {
                    return new ResponseEntity<Mensaje>(new Mensaje("Error al obtener imagenes"), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }          
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Persona inexistente con DNI: " + dniCliente), HttpStatus.BAD_REQUEST);
        }
    }
}
