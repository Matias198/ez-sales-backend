package com.unam.tf.controller.producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.entity.Rol;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.producto.CategoriaService;

@RestController
public class CategoriaController {
    @Autowired
    CategoriaService categoriaService;

    @Autowired
    UService usuarioService;

    @GetMapping("/categorias/obtenerTodas")
    private ResponseEntity<?> obtenerTodas(@RequestParam Long dniUsuario){
        try {
            UsuarioJwt user = usuarioService.getUsuarioByDni(dniUsuario).get();
            Boolean isAdmin = false;
            for (Rol rol : user.getRoles()) {
                if (rol.getRolNombre() == RolNombre.ROL_ADMINISTRADOR || rol.getRolNombre() == RolNombre.ROL_CLIENTE){
                    isAdmin = true;
                }
            }
            if (isAdmin){
                return new ResponseEntity<>(categoriaService.buscarTodasLasCategorias(), HttpStatus.OK);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol invalido para esta operacion"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
