package com.unam.tf.security.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.unam.tf.model.ubicacion.Pais;
import com.unam.tf.security.entity.Rol;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.RolService;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.ubicacion.PaisService;

@Component
public class CreateRoles implements CommandLineRunner {

    @Autowired
    RolService rolService;

    @Autowired
    PaisService paisService;

    @Autowired
    UService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (!(rolService.getByRolNombre(RolNombre.ROL_ADMINISTRADOR).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_ADMINISTRADOR);
            rolService.save(rolAdmin);
            System.out.println("ROL_ADMINISTRADOR CREADO");
        } else {
            System.out.println("ROL_ADMINISTRADOR EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_CLIENTE).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_CLIENTE);
            rolService.save(rolAdmin);
            System.out.println("ROL_CLIENTE CREADO");
        } else {
            System.out.println("ROL_CLIENTE EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_INVITADO).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_INVITADO);
            rolService.save(rolAdmin);
            System.out.println("ROL_INVITADO CREADO");
        } else {
            System.out.println("ROL_INVITADO EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_VENDEDOR).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_VENDEDOR);
            rolService.save(rolAdmin);
            System.out.println("ROL_VENDEDOR CREADO");
        } else {
            System.out.println("ROL_VENDEDOR EXISTENTE");
        }

        if (paisService.buscarTodosLosPaises().isEmpty()) {
            Pais pais = new Pais();
            pais.setCodPais(1L);
            pais.setNombre("Argentina");
            paisService.crearPais(pais);
        }

        if (usuarioService.existsByDni(41419890L)) {
            System.out.println("Superusuario existente");
        } else {
            UsuarioJwt usuario = new UsuarioJwt();
            usuario.setActivo(true);
            usuario.setDniUsuario(41419890L);
            usuario.setCliente(null);
            usuario.setId(41419890L); 
            Set<Rol> roles = new HashSet<>();
            roles.add(rolService.getByRolNombre(RolNombre.ROL_ADMINISTRADOR).get());
            usuario.setRoles(roles);
            usuario.setPassword(passwordEncoder.encode("12345678"));
            usuarioService.save(usuario);
            System.out.println("SUPERUSUARIO CREADO");
        }

    }

}
