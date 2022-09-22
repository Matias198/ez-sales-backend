package com.unam.tf.security.util; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.unam.tf.security.entity.Rol;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.RolService;

@Component
public class CreateRoles implements CommandLineRunner {

    @Autowired
    RolService rolService;

    @Override
    public void run(String... args) throws Exception {

        if (!(rolService.getByRolNombre(RolNombre.ROL_ADMINISTRADOR).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_ADMINISTRADOR);
            rolService.save(rolAdmin);
            System.out.println("ROL_ADMINISTRADOR CREADO");
        }else{
            System.out.println("ROL_ADMINISTRADOR EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_CLIENTE).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_CLIENTE);
            rolService.save(rolAdmin);
            System.out.println("ROL_CLIENTE CREADO");
        }else{
            System.out.println("ROL_CLIENTE EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_INVITADO).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_INVITADO);
            rolService.save(rolAdmin);
            System.out.println("ROL_INVITADO CREADO");
        }else{
            System.out.println("ROL_INVITADO EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_VENDEDOR).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_VENDEDOR);
            rolService.save(rolAdmin);
            System.out.println("ROL_VENDEDOR CREADO");
        }else{
            System.out.println("ROL_VENDEDOR EXISTENTE");
        }
        
    }

}
