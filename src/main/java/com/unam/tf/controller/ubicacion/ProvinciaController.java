package com.unam.tf.controller.ubicacion;

import java.net.URISyntaxException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.unam.tf.model.dto.ProvinciaDto;
import com.unam.tf.model.ubicacion.Ciudad;
import com.unam.tf.model.ubicacion.Provincia;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.service.ubicacion.PaisService;
import com.unam.tf.service.ubicacion.ProvinciaService;

@RestController
public class ProvinciaController {
    
    @Autowired
    ProvinciaService provinciaService;

    @Autowired
    PaisService paisService;

    @PostMapping("/provincia/crearProvincia/{idPais}/")
    public ResponseEntity<?> crearProvincia(@PathVariable Long idPais, @RequestPart String provinciaJson, @RequestPart String rol)
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                ProvinciaDto provinciaTemp = provinciaService.getProvinciaJson(provinciaJson);
                List<Boolean> valor = new ArrayList<>();
                valor.add(false);
                provinciaService.buscarTodasLasProvincias().forEach(prov -> {
                    if ((prov.getNombre().equals(provinciaTemp.getNombre())||
                        prov.getCodProvincia().equals(provinciaTemp.getCodProvincia()))){
                         valor.remove(0);
                         valor.add(true);
                    }
                });
                if (valor.get(0)){
                    return new ResponseEntity<Mensaje>(new Mensaje("Provincia "+provinciaTemp.getNombre()+" existente o codigo " +provinciaTemp.getCodProvincia()+ " ya registrado."), HttpStatus.OK);
                }else{
                    Provincia provincia = new Provincia();
                    Set<Ciudad> ciudades = new HashSet<>();
                    provincia.setNombre(provinciaTemp.getNombre());
                    provincia.setCodProvincia(provinciaTemp.getCodProvincia());
                    provincia.setPais(paisService.buscarPais(idPais));
                    provincia.setCiudades(ciudades);
                    provinciaService.crearProvincia(provincia);
                    return new ResponseEntity<Mensaje>(new Mensaje("Registrado con exito"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            } 
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/provincia/editarProvincia/{idPais}/{id}/")
    public ResponseEntity<?> editarProvincia(@PathVariable Long idPais, @PathVariable Long id, @RequestPart String provinciaJson, @RequestPart String rol) 
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                ProvinciaDto provinciaTemp = provinciaService.getProvinciaJson(provinciaJson);
                List<Boolean> valor = new ArrayList<>();
                valor.add(false);
                provinciaService.buscarTodasLasProvincias().forEach(prov -> {
                    if (prov.getNombre().equals(provinciaTemp.getNombre())&&
                        prov.getCodProvincia().equals(provinciaTemp.getCodProvincia())){
                         valor.remove(0);
                         valor.add(true);
                    }
                });
                if (valor.get(0)){
                    return new ResponseEntity<Mensaje>(new Mensaje("Provincia existente"), HttpStatus.BAD_REQUEST);
                }else{
                    Provincia provincia = new Provincia();
                provincia.setCodProvincia(id);
                provincia.setCodProvincia(provinciaTemp.getCodProvincia());
                provincia.setNombre(provinciaTemp.getNombre());
                provincia.setCiudades(provinciaService.buscarProvincia(id).getCiudades());
                provincia.setPais(paisService.buscarPais(idPais));
                provinciaService.crearProvincia(provincia);
                return new ResponseEntity<Mensaje>(new Mensaje("Modificado con exito"), HttpStatus.OK);
                }
                
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/provincia/borrarProvincia/{id}/")
    public ResponseEntity<?> borrarProvincia(@PathVariable Long id, @RequestParam String rol) throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                Provincia provincia = provinciaService.buscarProvincia(id);
                provincia.setEliminado(true);
                provinciaService.crearProvincia(provincia);
                //provinciaService.borrarProvincia(id);
                return new ResponseEntity<Mensaje>(new Mensaje("Eliminado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/provincia/obtenerProvincia/{id}/")
    public ResponseEntity<?> obtenerProvincia(@PathVariable Long id) throws URISyntaxException {
        try {
            return new ResponseEntity<Provincia>(provinciaService.buscarProvincia(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/provincia/obtenerProvincias")
    public ResponseEntity<?> obtenerProvincias() throws URISyntaxException {
        try {
            return new ResponseEntity<List<Provincia>>(provinciaService.buscarTodasLasProvincias(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
