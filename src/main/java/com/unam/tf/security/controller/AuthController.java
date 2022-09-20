package com.unam.tf.security.controller;

import com.unam.tf.model.Cliente;
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
import com.unam.tf.service.ClienteService;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

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

    @PostMapping("/nuevoUsuario")
    public ResponseEntity<?> nuevoUsuario(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {
        try {
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
            roles.add(rolService.getByRolNombre(RolNombre.ROL_CLIENTE).get());
            
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
            Cliente cliente = new Cliente();
            cliente.setUsuariojwt(usuario);
            try{
                System.out.println("Creando cliente");
                clienteService.crearCliente(cliente);
                System.out.println("Asociando cliente");
                usuario.setCliente(cliente);
                System.out.println("Registrando usuario");
                usuarioService.save(usuario);
                return new ResponseEntity<Mensaje>(new Mensaje("Usuario guardado."), HttpStatus.CREATED);
                
            }catch (Exception e){
                clienteService.borrarCliente(cliente.getId());
                usuarioService.delete(usuario);
                return new ResponseEntity<Mensaje>(new Mensaje("Error de persistencia: " + e.getMessage()), HttpStatus.BAD_REQUEST);
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
            if (usuarioService.getUsuarioByDni(loginUsuario.getDni()).get().getActivo()){
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getDni(), loginUsuario.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtProvider.generateToken(authentication);
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());
                return new ResponseEntity<JwtDto>(jwtDto, HttpStatus.OK);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Usuario inactivo."), HttpStatus.BAD_REQUEST);
            }            
        } catch (AuthenticationException e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/bajaUsuario")
    public ResponseEntity<?> bajaUsuario(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<Mensaje>(new Mensaje("Error en los campos."), HttpStatus.BAD_REQUEST);
            }
            if (usuarioService.getUsuarioByDni(loginUsuario.getDni()).get().getActivo()){
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getDni(), loginUsuario.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                if (userDetails.getUsername().equals(loginUsuario.getDni().toString())){
                    UsuarioJwt user = usuarioService.getUsuarioByDni(loginUsuario.getDni()).get();
                    user.setActivo(Boolean.FALSE);
                    usuarioService.save(user);
                    return new ResponseEntity<Mensaje>(new Mensaje("Baja de usuario exitosa"), HttpStatus.OK);
                }else{
                    return new ResponseEntity<Mensaje>(new Mensaje("Error en la baja de usuario, vuelva a intentar mas tarde"), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Usuario inactivo."), HttpStatus.BAD_REQUEST);
            }            
        } catch (AuthenticationException e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/activarUsuario")
    public ResponseEntity<?> activarUsuario(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<Mensaje>(new Mensaje("Error en los campos."), HttpStatus.BAD_REQUEST);
            }
            if (!(usuarioService.getUsuarioByDni(loginUsuario.getDni()).get().getActivo())){
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getDni(), loginUsuario.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                if (userDetails.getUsername().equals(loginUsuario.getDni().toString())){
                    UsuarioJwt user = usuarioService.getUsuarioByDni(loginUsuario.getDni()).get();
                    user.setActivo(Boolean.TRUE);
                    usuarioService.save(user);
                    return new ResponseEntity<Mensaje>(new Mensaje("Usuario exitosamente reactivado"), HttpStatus.OK);
                }else{
                    return new ResponseEntity<Mensaje>(new Mensaje("Error en la activacion de usuario, vuelva a intentar mas tarde"), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Este usuario ya está activado."), HttpStatus.BAD_REQUEST);
            }            
        } catch (AuthenticationException e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/rol/cargarRoles")
    public ResponseEntity<?> nuevoRol(@Valid @RequestBody Rol[] roles) {
        try {
            int errors = 0;
            for (Rol rol : roles) {
                if ((rol.getRolNombre() == RolNombre.ROL_ADMINISTRADOR) ||
                (rol.getRolNombre() == RolNombre.ROL_CLIENTE) ||
                (rol.getRolNombre() == RolNombre.ROL_INVITADO) ||
                (rol.getRolNombre() == RolNombre.ROL_VENDEDOR)) {
                    if (!(rolService.getByRolNombre(rol.getRolNombre()).isPresent())){
                        rolService.save(rol);
                    }else{
                        errors++;
                    }
                }else{
                    errors++;
                }                
            }
            return new ResponseEntity<Mensaje>(new Mensaje("Registro exitoso, numero de errores: " + errors), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
