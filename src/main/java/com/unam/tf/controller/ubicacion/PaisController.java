package com.unam.tf.controller.ubicacion;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.dto.PaisDto;
import com.unam.tf.model.ubicacion.Pais;
import com.unam.tf.model.ubicacion.Provincia;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.service.ubicacion.PaisService;

@RestController
public class PaisController {
    @Autowired
    PaisService paisService;

    @PostMapping("/pais/crearPais")
    public ResponseEntity<?> crearPais(@RequestPart String paisJson, @RequestPart String rol)
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                ObjectMapper mapper = new ObjectMapper();
                PaisDto paisTemp = mapper.readValue(paisJson, PaisDto.class);
                List<Pais> paises = paisService.buscarTodosLosPaises();
                boolean existe = false;
                for (Pais pais : paises) {
                    if (pais.getNombre().toUpperCase().equals(paisTemp.getNombre().toUpperCase())||
                        pais.getCodPais().equals(paisTemp.getCodPais())) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    return new ResponseEntity<Mensaje>(new Mensaje("Pais existente"), HttpStatus.BAD_REQUEST);
                } else { 
                    Pais pais = new Pais();
                    Set<Provincia> provincias = new HashSet<>();
                    pais.setCodPais(paisTemp.getCodPais());
                    pais.setNombre(paisTemp.getNombre());
                    pais.setProvincias(provincias);
                    paisService.crearPais(pais);
                    return new ResponseEntity<Mensaje>(new Mensaje("Registrado con exito"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/pais/editarPais/{id}/")
    public ResponseEntity<?> editarPais(@PathVariable Long id, @RequestPart String paisJson, @RequestPart String rol) 
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                Pais pais = paisService.getPaisJsonJson(paisJson);
                pais.setCodPais(id);
                pais.setProvincias(paisService.buscarPais(id).getProvincias());
                paisService.crearPais(pais);
                return new ResponseEntity<Mensaje>(new Mensaje("Modificado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/pais/borrarPais/{id}/")
    public ResponseEntity<?> borarPais(@PathVariable Long id, @RequestPart String rol) throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                Pais pais = paisService.buscarPais(id);
                pais.setEliminado(true);
                paisService.crearPais(pais);
                //paisService.borrarPais(id);
                return new ResponseEntity<Mensaje>(new Mensaje("Eliminado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/pais/obtenerPais/{id}/")
    public ResponseEntity<?> obtenerPais(@PathVariable Long id) throws URISyntaxException {
        try {
            return new ResponseEntity<Pais>(paisService.buscarPais(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/pais/obtenerPaises")
    public ResponseEntity<?> obtenerPaises() throws URISyntaxException {
        try {
            return new ResponseEntity<List<Pais>>(paisService.buscarTodosLosPaises(), HttpStatus.OK); 
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
