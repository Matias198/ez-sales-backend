package com.unam.tf.controller.ubicacion;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.cliente.Cliente;
import com.unam.tf.model.dto.MunicipioDto;
import com.unam.tf.model.dto.ProvinciaDto;
import com.unam.tf.model.ubicacion.Ciudad;
import com.unam.tf.model.ubicacion.Provincia;
import com.unam.tf.model.ubicacion.Ubicacion;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.service.cliente.ClienteService;
import com.unam.tf.service.ubicacion.CiudadService;
import com.unam.tf.service.ubicacion.PaisService;
import com.unam.tf.service.ubicacion.ProvinciaService;
import com.unam.tf.service.ubicacion.UbicacionService;

@RestController
public class GeoController {
    @Autowired
    PaisService paisService;

    @Autowired
    ProvinciaService provinciaService;

    @Autowired
    CiudadService ciudadService;

    @Autowired
    ClienteService clienteService;

    @Autowired
    UbicacionService ubicacionService;

    @PostMapping("geo/actualizarDatosGeo")
    public ResponseEntity<?> actualizarDatosGeo(@RequestPart String municipiosJson, @RequestPart String provinciaJson, @RequestPart String rol) throws URISyntaxException {
        try {
            int registrosProvincias = 0;
            int actualizadosProvincias = 0;
            int sinCambiosProvincias = 0;
            int registrosMunicipios = 0;
            int actualizadosMunicipios = 0;
            int sinCambiosMunicipios = 0;
            ObjectMapper mapper = new ObjectMapper();
            ProvinciaDto[] provincias = mapper.readValue(provinciaJson, ProvinciaDto[].class);
            System.out.println("Registrando provincias...");
            for (ProvinciaDto provinciaDto : provincias) {
                System.out.println("Buscando provincia de la BD");
                Provincia provinciaExistente = provinciaService.buscarProvincia(provinciaDto.getCodProvincia());
                Provincia provincia = new Provincia();
                provincia.setPais(paisService.buscarPais(1L));
                provincia.setNombre(provinciaDto.getNombre());
                provincia.setCodProvincia(provinciaDto.getCodProvincia());

                System.out.println("Pregunta si la provincia es igual a null");
                if (provinciaExistente == null){
                    Set<Ciudad> ciudades = new HashSet<>();  
                    ciudadService.buscarTodasLasCiudades().forEach(ciudad -> {
                        if (ciudad.getProvincia().getCodProvincia().equals(provincia.getCodProvincia())){
                            ciudades.add(ciudad);
                        }
                    });
                    provincia.setCiudades(ciudades);
                    provinciaService.crearProvincia(provincia);
                    System.out.println("Provincia " + provincia.getCodProvincia() + " registrada con exito.");
                    registrosProvincias++;
                }else{
                    System.out.println("Pregunta si coinciden los nombres y el codigo");
                    if (provincia.getNombre().equals(provinciaExistente.getNombre())
                        && provincia.getCodProvincia().equals(provinciaExistente.getCodProvincia())){
                        System.out.println("Provincia " + provincia.getCodProvincia() + " sin cambios.");
                        sinCambiosProvincias++;
                    }else{ 
                        System.out.println("No coinciden entonces actualiza");
                        provincia.setCiudades(provinciaExistente.getCiudades()); 
                        provinciaService.crearProvincia(provincia);
                        System.out.println("Provincia " + provincia.getCodProvincia() + " actualizada con exito.");
                        actualizadosProvincias++;
                    }
                }                
            }
            System.out.println("Registrando municipios...");
            MunicipioDto[] municipios = mapper.readValue(municipiosJson, MunicipioDto[].class);
            for (MunicipioDto municipioDto : municipios) {
                Ciudad ciudadExistente = ciudadService.buscarCiudad(municipioDto.getCodCiudad());
                Ciudad ciudad = new Ciudad();
                ciudad.setCodCiudad(municipioDto.getCodCiudad());
                ciudad.setNombre(municipioDto.getNombreCiudad());
                ciudad.setProvincia(provinciaService.buscarProvincia(municipioDto.getCodProvincia()));

                if (ciudadExistente == null){
                    Set<Cliente> clientes = new HashSet<>();
                    clienteService.buscarTodosLosClientes().forEach(cliente ->{
                        if(cliente.getCiudad().getCodCiudad().equals(ciudad.getCodCiudad())){
                            clientes.add(cliente);
                        }
                    });
                    Set<Ubicacion> ubicaciones = new HashSet<>();
                    ubicacionService.buscarTodasLasUbicaciones().forEach(ubicacion ->{
                        if (ubicacion.getCiudad().getCodCiudad().equals(ciudad.getCodCiudad())){
                            ubicaciones.add(ubicacion);
                            ciudad.setProvincia(ubicacion.getCiudad().getProvincia());
                        }
                    });
                    ciudad.setClientes(clientes);
                    ciudad.setUbicaciones(ubicaciones);
                    ciudadService.crearCiudad(ciudad);
                    System.out.println("Municipio " + ciudad.getCodCiudad() + " registrado con exito.");
                    registrosMunicipios++;
                }else{
                    if (ciudad.getNombre().equals(ciudadExistente.getNombre())
                        && ciudad.getCodCiudad().equals(ciudadExistente.getCodCiudad())){
                        System.out.println("Municipio " + ciudad.getCodCiudad() + " sin cambios.");
                        sinCambiosMunicipios++;
                    }else{
                        ciudad.setClientes(ciudadExistente.getClientes());
                        ciudad.setUbicaciones(ciudadExistente.getUbicaciones());
                        ciudad.setProvincia(ciudadExistente.getProvincia());
                        System.out.println("Municipio " + ciudad.getCodCiudad() + " actualizado con exito.");
                        actualizadosMunicipios++;
                    }
                }  
            }
            return new ResponseEntity<Mensaje>(new Mensaje("Base de datos actualizada con exito. Provincias: sin cambios: " +sinCambiosProvincias+ ", actualizados: "+actualizadosProvincias+", registrados: "+registrosProvincias+". Municipios: sin cambios: " +sinCambiosMunicipios+ ", actualizados: "+actualizadosMunicipios+", registrados: "+registrosMunicipios), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage() + " " + e.getCause()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
