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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.unam.tf.model.ubicacion.Ubicacion;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.service.tienda.TiendaService;
import com.unam.tf.service.ubicacion.CiudadService;
import com.unam.tf.service.ubicacion.PaisService;
import com.unam.tf.service.ubicacion.ProvinciaService;
import com.unam.tf.service.ubicacion.UbicacionService;

@RestController
public class UbicacionController {

    @Autowired
    UbicacionService ubicacionService;

    @Autowired
    PaisService paisService;

    @Autowired
    ProvinciaService provinciaService;

    @Autowired
    CiudadService ciudadService;

    @Autowired
    TiendaService tiendaService;

    @PostMapping("/ubicacion/crear/{idTienda}/{idCiudad}/")
    public ResponseEntity<?> crearUbicacion(@PathVariable Long idTienda, @PathVariable Long idCiudad,
            @RequestPart String ubicacionJson, @RequestPart String rol)
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR") || rol.contains("ROL_CLIENTE")) {
                Ubicacion ubicacionTemp = ubicacionService.getUbicacionJson(ubicacionJson);
                Ubicacion ubicacion = new Ubicacion();
                ubicacionTemp.setId(ubicacion.getId());
                ubicacionTemp.setCiudad(ciudadService.buscarCiudad(idCiudad));
                ubicacionTemp.setTienda(tiendaService.buscarTienda(idTienda));
                ubicacionService.crearUbicacion(ubicacionTemp);
                return new ResponseEntity<Mensaje>(new Mensaje("Registrado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/ubicacion/editarUbicacion/{idTienda}/{idCiudad}/{idUbicacion}/")
    public ResponseEntity<?> editarUbicacion(@PathVariable Long idTienda, @PathVariable Long idCiudad,
            @PathVariable Long idUbicacion,
            @RequestPart String ubicacionJson, @RequestPart String rol)
            throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR") || rol.contains("ROL_CLIENTE")) {
                Ubicacion ubicacionTemp = ubicacionService.getUbicacionJson(ubicacionJson);
                ubicacionTemp.setId(idUbicacion);
                ubicacionTemp.setCiudad(ciudadService.buscarCiudad(idCiudad));
                ubicacionTemp.setTienda(tiendaService.buscarTienda(idTienda));
                ubicacionService.crearUbicacion(ubicacionTemp);
                return new ResponseEntity<Mensaje>(new Mensaje("Modificado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"),
                        HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/ubicacion/borrarUbicacion/{id}/")
    public ResponseEntity<?> borrarUbicacion(@PathVariable Long id, @RequestPart String rol) throws URISyntaxException {
        try {
            if (rol.contains("ROL_ADMINISTRADOR")) {
                ubicacionService.borrarUbicacion(id);
                return new ResponseEntity<Mensaje>(new Mensaje("Eliminado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/ubicacion/borrarUbicacionCliente/{id}/")
    public ResponseEntity<?> borrarUbicacionCliente(@PathVariable Long id, @RequestPart String rol) throws URISyntaxException {
        try {
            if (rol.contains("ROL_CLIENTE")) {
                ubicacionService.borrarUbicacionLogico(id);
                return new ResponseEntity<Mensaje>(new Mensaje("Eliminado con exito"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ubicacion/obtenerUbicacion/{id}/")
    public ResponseEntity<?> obtenerUbicacion(@PathVariable Long id) throws URISyntaxException {
        try {
            return new ResponseEntity<Ubicacion>(ubicacionService.buscarUbicacion(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ubicacion/obtenerUbicaciones")
    public ResponseEntity<?> obtenerUbicaciones() throws URISyntaxException {
        try {
            return new ResponseEntity<List<Ubicacion>>(ubicacionService.buscarTodasLasUbicaciones(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
