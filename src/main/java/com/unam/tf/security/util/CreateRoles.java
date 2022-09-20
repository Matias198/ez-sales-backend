package com.unam.tf.security.util;

//package com.integrador.tp.security.util;
//
//import com.integrador.tp.security.entity.Rol;
//import com.integrador.tp.security.enums.RolNombre;
//import com.integrador.tp.security.service.RolService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CreateRoles implements CommandLineRunner {
//
//    @Autowired
//    RolService rolService;
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        if (rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get() == null) {
//            Rol rolAdmin = new Rol(RolNombre.ROLE_ADMIN);
//            rolService.save(rolAdmin);
//            System.out.println("ROL ADMIN CREADO");
//        }else{
//            System.out.println("ROL ADMIN EXISTENTE");
//        }
//        
//        if (rolService.getByRolNombre(RolNombre.ROLE_USER).get() == null) {
//            Rol rolUser = new Rol(RolNombre.ROLE_USER);
//            rolService.save(rolUser);
//            System.out.println("ROL USER CREADO");
//        }else{
//            System.out.println("ROL USER EXISTENTE");
//        }
//        
//    }
//
//}
