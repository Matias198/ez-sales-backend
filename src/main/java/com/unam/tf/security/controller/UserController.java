package com.unam.tf.security.controller;

import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.RestController;
import com.unam.tf.security.dto.AuthDto;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.entity.Rol;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.RolService;
import com.unam.tf.security.service.UService;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UService uService;

    @Autowired
    private RolService rService; 

    @GetMapping("/getRoles/")
    public UsuarioJwt getRoles(@RequestParam Long dniUsuario) {
        return uService.getUsuarioByDni(dniUsuario).get();
    }

    @PutMapping("/bajaUsuario")
    public ResponseEntity<?> bajaUsuario(@Valid @RequestBody AuthDto authDto, BindingResult bindingResult) {
        try {
            Boolean esAdmin = false;
            Rol rolAdmin = rService.getByRolNombre(RolNombre.ROL_ADMINISTRADOR).get();
            Set<RolNombre> roles = new HashSet<>();
            uService.getUsuarioByDni(authDto.getDniAutenticado()).get().getRoles().forEach((rol) ->{
                roles.add(rol.getRolNombre());
            });
            for (int i = 0; i < roles.size(); i++){
                if (roles.contains(RolNombre.ROL_ADMINISTRADOR)){
                    esAdmin = true;
                }
            }
            if (esAdmin){
                if (bindingResult.hasErrors()) {
                    return new ResponseEntity<Mensaje>(new Mensaje("Error en los campos."), HttpStatus.BAD_REQUEST);
                }
                if (uService.getUsuarioByDni(authDto.getDniTarget()).get().getActivo()){
                    if (!(uService.getUsuarioByDni(authDto.getDniTarget()).get().getRoles().contains(rolAdmin))){
                        UsuarioJwt user = uService.getUsuarioByDni(authDto.getDniTarget()).get();
                        user.setActivo(Boolean.FALSE);
                        uService.save(user);
                        return new ResponseEntity<Mensaje>(new Mensaje("Baja de usuario exitosa"), HttpStatus.OK);    
                    }else{
                        return new ResponseEntity<Mensaje>(new Mensaje("No se puede dar de baja una cuenta de administrador."), HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<Mensaje>(new Mensaje("Usuario inactivo."), HttpStatus.BAD_REQUEST);
                } 
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            } 
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/activarUsuario")
    public ResponseEntity<?> activarUsuario(@Valid @RequestBody AuthDto authDto, BindingResult bindingResult) {
        try {
            Boolean esAdmin = false;
            Set<RolNombre> roles = new HashSet<>();
            uService.getUsuarioByDni(authDto.getDniAutenticado()).get().getRoles().forEach((rol) ->{
                roles.add(rol.getRolNombre());
            });
            for (int i = 0; i < roles.size(); i++){
                if (roles.contains(RolNombre.ROL_ADMINISTRADOR)){
                    esAdmin = true;
                }
            }
            if (esAdmin){
                if (bindingResult.hasErrors()) {
                    return new ResponseEntity<Mensaje>(new Mensaje("Error en los campos."), HttpStatus.BAD_REQUEST);
                }
                if (!(uService.getUsuarioByDni(authDto.getDniTarget()).get().getActivo())){
                    UsuarioJwt user = uService.getUsuarioByDni(authDto.getDniTarget()).get();
                    user.setActivo(Boolean.TRUE);
                    uService.save(user);
                    return new ResponseEntity<Mensaje>(new Mensaje("Usuario exitosamente reactivado"), HttpStatus.OK);
                }else{
                    return new ResponseEntity<Mensaje>(new Mensaje("Este usuario ya está activado."), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }            
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
