package com.unam.tf.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.unam.tf.model.Cliente;
import com.unam.tf.model.Mail;
import com.unam.tf.model.Ubicacion;
import com.unam.tf.security.dto.ImgDto;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.IClienteService;
import com.unam.tf.service.MailService;
import com.unam.tf.service.UbicacionService;

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
    
    @Autowired
    private IClienteService clienteService;

    @Autowired
    private UService uService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UbicacionService ubicacionService;

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

    @RequestMapping(value = "cliente/modificarUnCliente", method = RequestMethod.PUT, consumes = {"multipart/form-data"})
    @ResponseBody
    public ResponseEntity<?> modificarUnCliente(@RequestParam Long dniAutenticado, @RequestPart String clienteJson, @RequestPart String mailJson, @RequestPart String ubicacionJson, @RequestPart("fotoP") @Valid @NotNull @NotBlank MultipartFile fotoP, @RequestPart("fotoB") @Valid @NotNull @NotBlank MultipartFile fotoB) throws URISyntaxException {
        try {  
            Cliente cliente = clienteService.getClienteJson(clienteJson);
            Mail mail = mailService.getMailJson(mailJson);
            Ubicacion ubicacion = ubicacionService.getUbicacionJson(ubicacionJson);

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
            if (esAdmin || cliente.getUsuariojwt().getDniUsuario().equals(dniAutenticado)){
                Cliente clienteTemp = uService.getUsuarioByDni(dniAutenticado).get().getCliente();
                Mail mailTemp = clienteTemp.getMail();
                Ubicacion ubicacionTemp = clienteTemp.getUbicacion();

                System.out.println("Setting mail");
                mailTemp.setMail(mail.getMail());
                mailTemp.setCodigo(mail.getCodigo());

                System.out.println("Setting ubicacion");
                ubicacionTemp.setCodigoPostal(ubicacion.getCodigoPostal());
                ubicacionTemp.setCoordenadasGoogle(ubicacion.getCoordenadasGoogle());
                ubicacionTemp.setLocalidad(ubicacion.getLocalidad());
                ubicacionTemp.setPais(ubicacion.getPais());
                ubicacionTemp.setProvincia(ubicacion.getProvincia());

                clienteTemp.setNombre(cliente.getNombre());
                clienteTemp.setApellido(cliente.getApellido());
                clienteTemp.setMail(cliente.getMail());
                //
                if ((!fotoP.isEmpty()) && (!fotoB.isEmpty())) {

                    byte[] bytesPerfil = fotoP.getBytes();
                    byte[] bytesBanner = fotoB.getBytes();
                    clienteTemp.setFotoPerfil(bytesPerfil);
                    clienteTemp.setFotoBanner(bytesBanner);
                }
                //
                clienteTemp.setMail(mailTemp);
                clienteTemp.setUbicacion(ubicacionTemp);
                clienteTemp.setUsuariojwt(uService.getUsuarioByDni(dniAutenticado).get());
                try{
                    clienteService.crearCliente(clienteTemp);
                    return new ResponseEntity<Mensaje>(new Mensaje("Cliente modificado con exito"), HttpStatus.OK);
                }catch (Exception e){
                    return new ResponseEntity<Mensaje>(new Mensaje("Error al guardar el cliente: " + e.getMessage()), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }          
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error en el metodo modificar un cliente: " + e.getMessage()), HttpStatus.BAD_REQUEST);
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
                    /*
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
                    */
                    ImgDto imgDto = new ImgDto();
                    Set<byte[]> setBytes = new HashSet<>();
                    setBytes.add(bytePerfil);
                    setBytes.add(byteBanner);
                    imgDto.setBytes(setBytes);
                    return new ResponseEntity<ImgDto>(imgDto, HttpStatus.OK);    
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
