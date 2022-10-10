package com.unam.tf.controller.ubicacion;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.unam.tf.model.dto.CiudadDto;
import com.unam.tf.model.ubicacion.Ciudad;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.service.ubicacion.CiudadService; 
import com.unam.tf.service.ubicacion.PaisService;
import com.unam.tf.service.ubicacion.ProvinciaService;
 

@RestController
public class CiudadController {
    @Autowired
    ProvinciaService provinciaService;

    @Autowired
    PaisService paisService; 

    @Autowired
    CiudadService ciudadService;

    @PostMapping("/ciudad/crearCiudad/{idProvincia}/")
    public ResponseEntity<?> crearCiudad(@PathVariable Long idProvincia, @RequestPart String ciudadJson, @RequestPart String rol)
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                CiudadDto ciudadTemp = ciudadService.getCiudadJson(ciudadJson);
                List<Ciudad> ciudades = ciudadService.buscarTodasLasCiudades();
                boolean existe = false;
                for (Ciudad ciudad : ciudades) {
                    if (ciudad.getNombre().equals(ciudadTemp.getNombre()) &&
                        ciudad.getProvincia().getCodProvincia().equals(idProvincia) && 
                        ciudad.getCodCiudad().equals(ciudadTemp.getCodCiudad())) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    return new ResponseEntity<Mensaje>(new Mensaje("Ciudad "+ciudadTemp.getNombre()+" existente"), HttpStatus.OK);
                } else {
                    Ciudad ciudad = new Ciudad(); 
                    ciudad.setCodCiudad(ciudadTemp.getCodCiudad());
                    ciudad.setNombre(ciudadTemp.getNombre());
                    ciudad.setProvincia(provinciaService.buscarProvincia(idProvincia));
                    ciudadService.crearCiudad(ciudad);
                    return new ResponseEntity<Mensaje>(new Mensaje("Registrado con exito"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            } 
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/ciudad/editarCiudad/{idProvincia}/{idCiudad}/")
    public ResponseEntity<?> editarCiudad(@PathVariable Long idProvincia, @PathVariable Long idCiudad, @RequestPart String ciudadJson, @RequestPart String rol) 
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                CiudadDto ciudadTemp = ciudadService.getCiudadJson(ciudadJson); 
                Ciudad ciudad = new Ciudad();
                ciudad.setNombre(ciudadTemp.getNombre());
                ciudad.setCodCiudad(ciudadTemp.getCodCiudad());
                ciudad.setProvincia(provinciaService.buscarProvincia(idProvincia)); 
                ciudad.setUbicaciones(ciudadService.buscarCiudad(idCiudad).getUbicaciones());
                ciudadService.crearCiudad(ciudad);
                return new ResponseEntity<Mensaje>(new Mensaje("Modificado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/ciudad/borrarCiudad/{idCiudad}/")
    public ResponseEntity<?> borrarCiudad(@PathVariable Long idCiudad, @RequestPart String rol) throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                Ciudad ciudad = ciudadService.buscarCiudad(idCiudad);
                ciudad.setEliminado(true);
                ciudadService.crearCiudad(ciudad);
                //ciudadService.borrarCiudad(idCiudad);
                return new ResponseEntity<Mensaje>(new Mensaje("Eliminado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/ciudad/obtenerCiudad/{idCiudad}/")
    public ResponseEntity<?> obtenerProvincia(@PathVariable Long idCiudad) throws URISyntaxException {
        try {
            return new ResponseEntity<Ciudad>(ciudadService.buscarCiudad(idCiudad), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/ciudad/obtenerCiudades")
    public ResponseEntity<?> obtenerCiudades() throws URISyntaxException {
        try {
            return new ResponseEntity<List<Ciudad>>(ciudadService.buscarTodasLasCiudades(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
