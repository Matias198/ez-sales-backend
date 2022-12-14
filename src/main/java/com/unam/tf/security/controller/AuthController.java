package com.unam.tf.security.controller;
 
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.unam.tf.model.producto.Categoria;
import com.unam.tf.security.dto.JwtDto;
import com.unam.tf.security.dto.LoginUsuario;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.dto.NuevoUsuario;
import com.unam.tf.security.entity.Rol;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.jwt.JwtProvider;
import com.unam.tf.security.service.RolService;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.cliente.ClienteService;
import com.unam.tf.service.mail.MailService;
import com.unam.tf.service.mail.SendMailService;
import com.unam.tf.service.ubicacion.CiudadService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${spring.mail.username}")
    String mailSuperusuario;

    @Value("${app.link.prod}")
    String linkProd;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;
    
    @Autowired
    ClienteService clienteService;

    @Autowired
    MailService mailService;

    @Autowired
    CiudadService ciudadService;

    @Autowired
    SendMailService sendMailService;
 
    @RequestMapping(value = "nuevoUsuario", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    @ResponseBody   
    public ResponseEntity<?> nuevoUsuario(@RequestPart String formularioJson, @RequestPart MultipartFile fotoP, @RequestPart MultipartFile fotoB, BindingResult bindingResult) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            FormularioDto formularioDto = mapper.readValue(formularioJson, FormularioDto.class);

            if (formularioDto.getNombreCliente().equals("")||
                formularioDto.getApellidoCliente().equals("")||
                formularioDto.getMail().equals("")||
                formularioDto.getNombreCiudad().equals("")||
                formularioDto.getPassword().equals("")||
                formularioDto.getRol().equals("")){
                return new ResponseEntity<Mensaje>(new Mensaje("Campos vac??os."), HttpStatus.BAD_REQUEST); 
            }

            System.out.println(formularioJson); 
            System.out.println(fotoP);
            System.out.println(fotoB);  

            System.out.println("Mapeando clienteJson --> cliente"); 
            Cliente cliente = new Cliente();
            cliente.setNombre(formularioDto.getNombreCliente());
            cliente.setApellido(formularioDto.getApellidoCliente()); 
            cliente.setCiudad(ciudadService.buscarCiudad(formularioDto.getCodCiudad()));

            System.out.println("Mapeando mailJson --> mail"); 
            Mail mail = new Mail();
            mail.setMail(formularioDto.getMail());  

            System.out.println("Mapeando nuevoUsuarioJson --> nuevoUsuario");
            NuevoUsuario nuevoUsuario = new NuevoUsuario();
            nuevoUsuario.setDni(formularioDto.getDni()); 
            nuevoUsuario.setPassword(formularioDto.getPassword());
            Set<String> roles2 = new HashSet<>();
            roles2.add(formularioDto.getRol());
            nuevoUsuario.setRoles(roles2);          

            System.out.println("Verificando errores en los campos");
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<Mensaje>(new Mensaje("Error en los campos."), HttpStatus.BAD_REQUEST);
            }
            System.out.println("Verificando usuario dni");
            if (usuarioService.existsByDni(nuevoUsuario.getDni())) {
                return new ResponseEntity<Mensaje>(new Mensaje("Dni ya existente."), HttpStatus.BAD_REQUEST);
            }
            System.out.println("Creando usuario Jwt");
            UsuarioJwt usuario = new UsuarioJwt(nuevoUsuario.getDni(), passwordEncoder.encode(nuevoUsuario.getPassword()), null);
            
            System.out.println("Registrando roles");
            Set<Rol> roles = new HashSet<>(); 
            
            if (nuevoUsuario.getRoles().contains("administrador")) {
                roles.add(rolService.getByRolNombre(RolNombre.ROL_ADMINISTRADOR).get());
            }
            
            if (nuevoUsuario.getRoles().contains("invitado")) {
                roles.add(rolService.getByRolNombre(RolNombre.ROL_INVITADO).get());
            }
            
            if (nuevoUsuario.getRoles().contains("vendedor")) {
                roles.add(rolService.getByRolNombre(RolNombre.ROL_VENDEDOR).get());
            }
            
            if (nuevoUsuario.getRoles().contains("cliente")) {
                roles.add(rolService.getByRolNombre(RolNombre.ROL_CLIENTE).get());
            }
            usuario.setRoles(roles);                           
            

            if ((!fotoP.isEmpty()) && (!fotoB.isEmpty())) { 

                byte[] bytesPerfil = fotoP.getBytes();
                byte[] bytesBanner = fotoB.getBytes();
                cliente.setFotoPerfil(bytesPerfil);
                cliente.setFotoBanner(bytesBanner);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("No se reconocen las imagenes ingresadas o est?? vacio."), HttpStatus.BAD_REQUEST); 
            }

            String codigo = UUID.randomUUID().toString(); 
            mail.setCodigo(codigo); 
            mail.setCliente(cliente);
            cliente.setUsuariojwt(usuario); 
            cliente.setMail(mail); 
            Set<Categoria> categorias = new HashSet<>(); 
            cliente.setCategorias(categorias);
            try{
                System.out.println("Creando cliente");
                clienteService.crearCliente(cliente);
                System.out.println("Creando mail");
                mailService.crearMail(mail); 
                System.out.println("Asociando cliente");
                usuario.setCliente(cliente);
                usuario.setActivo(false);  

                // MAIL VERIFICATION
                String fromInternetAdress = mailSuperusuario;
                String toInternetAdress = cliente.getMail().getMail();
                System.out.println("Enviando mail desde "+fromInternetAdress+" hacia "+toInternetAdress);
                String subject = "Verificar Mail";
                String link = linkProd + "/verificar-mail/"+mail.getId()+"/"+mail.getCodigo();
                
                File archivo = null;
                FileReader fr = null;
                BufferedReader br = null;
                String texto = "";
                String linea = "";
                try { 
                    archivo = new File ("./src/main/resources/mail/mail.txt");
                    fr = new FileReader (archivo);
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8)); 

                    // Lectura del fichero  
                    linea=br.readLine();
                    texto += linea + link;
                    linea=br.readLine();
                    texto += linea + mail.getCodigo();
                    linea=br.readLine();
                    texto += linea;
                }
                catch(Exception e){
                    e.printStackTrace();
                }finally{ 
                    try{                    
                        if( null != fr ){   
                        fr.close();     
                        }                  
                    }catch (Exception e2){ 
                        e2.printStackTrace();
                    }
                } 
                String body = texto; 
                System.out.println("Enviando mail de verificacion");
                boolean valor = false;
                valor = sendMailService.sendCustomMail(toInternetAdress, subject, body);
                System.out.println("Registrando usuario");
                usuarioService.save(usuario);
                if (valor){
                    return new ResponseEntity<Mensaje>(new Mensaje("Usuario guardado."), HttpStatus.CREATED);
                }else{
                    mailService.borrarMail(mail.getId());
                    clienteService.borrarCliente(cliente.getId());
                    usuarioService.delete(usuario);
                    return new ResponseEntity<Mensaje>(new Mensaje("Error al enviar mail."), HttpStatus.BAD_REQUEST);
                } 
            }catch (Exception e){ 
                mailService.borrarMail(mail.getId());
                clienteService.borrarCliente(cliente.getId());
                usuarioService.delete(usuario);
                System.out.println("Error al crear usuario: "+ e.getMessage() + ". Causa: " + e. getCause());
                return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<Mensaje>(new Mensaje("Error en los campos."), HttpStatus.BAD_REQUEST);
            }
            try {
                if (usuarioService.getUsuarioByDni(loginUsuario.getDniUser()).get().getActivo()){
                    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getDniUser(), loginUsuario.getPass()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String jwt = jwtProvider.generateToken(authentication);
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());
                    return new ResponseEntity<JwtDto>(jwtDto, HttpStatus.OK);
                }else{
                    return new ResponseEntity<Mensaje>(new Mensaje("Usuario inactivo."), HttpStatus.BAD_REQUEST);
                } 
            } catch (Exception e) {
                return new ResponseEntity<Mensaje>(new Mensaje("Error al obtener usuario: " + e.getMessage()), HttpStatus.BAD_REQUEST);
            }            
        } catch (AuthenticationException e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/validarMail/{id}/")
    public ResponseEntity<?> validarMail(@PathVariable Long id, @RequestParam String codigo) {
        try {
            if(mailService.buscarMail(id).getCodigo().equals(codigo)){
                Mail mail = mailService.buscarMail(id);
                mail.setValidado(true);
                mail.setCodigo("");
                UsuarioJwt userJwt = clienteService.buscarCliente(mail.getCliente().getId()).getUsuariojwt();
                userJwt.setActivo(true);
                mailService.crearMail(mail);
                usuarioService.save(userJwt);
                return new ResponseEntity<Mensaje>(new Mensaje("Mail validado con exito"), HttpStatus.OK);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Codigo no valido"), HttpStatus.BAD_REQUEST);
            }
        } catch (AuthenticationException e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mail/obtenerMailByAddress/")
    public ResponseEntity<?> obtenerMailByAddress(@RequestParam String correo) throws URISyntaxException {
        try {
            List<Mail> correos = mailService.buscarTodosLosMail();
            for (int i = 0; i < correos.size(); i++) {
                if (correos.get(i).getMail().equals(correo)){
                    return new ResponseEntity<Mail>(correos.get(i), HttpStatus.OK);
                }
            }
            return new ResponseEntity<Mensaje>(new Mensaje("No se encuentra el mail"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error al obtener mail: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mail/obtenerMailById/{id}/")
    public ResponseEntity<?> obtenerMailById(@PathVariable Long id) throws URISyntaxException {
        try {
            return new ResponseEntity<Mail>(mailService.buscarMail(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error al obtener mail: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    } 

    @GetMapping("/imagen")
    public String verImagen(){
        try { 
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
            System.out.println("ByteArrayOutputStream OK");
            // serialize the image
            ImageIO.write(ImageIO.read(new File("./src/main/resources/images/favicon.png")), "png", baos); 
            System.out.println("ImageIO OK");
            // convert the written image to a byte[]
            byte[] bytes = baos.toByteArray();
            System.out.println("bytes.length " + bytes.length);
            // THIS IS IT! Change the bytes to Base 64 Binary
            String data = Base64.getEncoder().encodeToString(bytes);
            // add the 'data URI prefix' before returning the image as string 
            return "data:image/png;base64," + data;  
        } catch (Exception e) {
            return "Error: " + e.getMessage() +". Causa: " + e.getCause(); 
        } 
    }

    @GetMapping("/probarMail")
    public String enviarMail(){
        // MAIL VERIFICATION
        String fromInternetAdress = mailSuperusuario;
        String toInternetAdress = mailSuperusuario;
        String codigo = UUID.randomUUID().toString();
        System.out.println("Enviando mail desde "+fromInternetAdress+" hacia "+toInternetAdress);
        String subject = "Verificar Mail";
        String link = linkProd + "/verificar-mail/"+1+"/"+codigo;
        
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        String texto = "";
        String linea = "";
        try { 
            archivo = new File ("./src/main/resources/mail/mail.txt");
            fr = new FileReader (archivo);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8)); 

            // Lectura del fichero 
            linea=br.readLine();
            texto += linea + link;
            linea=br.readLine();
            texto += linea + codigo;
            linea=br.readLine();
            texto += linea;
        }
        catch(Exception e){
            e.printStackTrace();
        }finally{ 
            try{                    
                if( null != fr ){   
                fr.close();     
                }                  
            }catch (Exception e2){ 
                e2.printStackTrace();
            }
        } 
        String body = texto; 
        System.out.println("Enviando mail de verificacion");
        sendMailService.sendCustomMail(toInternetAdress, subject, body);
        return "OK";
    }
}
