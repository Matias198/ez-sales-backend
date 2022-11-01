package com.unam.tf.controller.tienda;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.cliente.Cliente;
import com.unam.tf.model.dto.TiendaDto;
import com.unam.tf.model.producto.Producto;
import com.unam.tf.model.tienda.FotosTienda;
import com.unam.tf.model.tienda.Tienda;
import com.unam.tf.model.ubicacion.Ciudad;
import com.unam.tf.model.ubicacion.Ubicacion;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.entity.Rol;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.RolService;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.producto.ProductoService;
import com.unam.tf.service.tienda.FotosTiendaService;
import com.unam.tf.service.tienda.TiendaService;
import com.unam.tf.service.ubicacion.CiudadService;
import com.unam.tf.service.ubicacion.UbicacionService;

@RestController
public class TiendaController {
    
    @Autowired
    TiendaService tiendaService;

    @Autowired
    UService usuarioService;

    @Autowired
    CiudadService ciudadService;

    @Autowired
    FotosTiendaService fotosTiendaService;

    @Autowired
    UbicacionService ubicacionService;

    @Autowired
    ProductoService productoService;

    @Autowired
    RolService rolService;

    @PostMapping("/tienda/crearTienda")
    public ResponseEntity<?> crearTienda(@RequestParam("arrayMultipartFotos") MultipartFile[] arrayMultipartFotos, @RequestParam String tiendaJson){
        Set<FotosTienda> fotos = new HashSet<>();
        Ubicacion ubicacion = new Ubicacion();
        Tienda tienda = new Tienda();
        tiendaService.crearTienda(tienda);
        try {
            for (int i = 0; i < arrayMultipartFotos.length; i++) {
                System.out.println(arrayMultipartFotos[i].toString());
            }
            System.out.println(tiendaJson); 

            /* Mapeo TiendaDto con tiendaJson */
            ObjectMapper mapper = new ObjectMapper();
            TiendaDto tiendaDto = mapper.readValue(tiendaJson, TiendaDto.class);
            System.out.println("Mapped");

            /* Creo mi cliente en base al dni asociado al usuario */
            Cliente cliente = usuarioService.getClienteAsociado(tiendaDto.getDniCliente());
            System.out.println("Cliente: " + cliente.getNombre() + ' ' + cliente.getApellido());

            /* Busco la ciudad a la que se asocia a la ubicacion */
            Ciudad ciudad = ciudadService.buscarCiudad(tiendaDto.getCodCiudad());
            System.out.println("Ciudad: " + ciudad.getNombre());
            
           
            tienda.setNombre(tiendaDto.getNombre());
            tienda.setRubro(tiendaDto.getRubro());
            tienda.setContacto(tiendaDto.getContacto());
            
            /* Creo el SET de fotos y agrego a la tienda la referencia */
            System.out.println("Setting fotos");
            
            for (int i = 0; i < arrayMultipartFotos.length; i++) { 
                FotosTienda foto = new FotosTienda();
                foto.setTienda(tienda);
                foto.setFoto(arrayMultipartFotos[i].getBytes());
                fotosTiendaService.crearFotosTienda(foto);
                fotos.add(foto); 
            } 
            tienda.setFotos(fotos);
            System.out.println("Fotos: hecho");

            /* Creo una ubicacion y setteo los valores */ 
            ubicacion.setLatitud(tiendaDto.getLatitud());
            ubicacion.setLongitud(tiendaDto.getLongitud());
            ubicacion.setCiudad(ciudad);
            ubicacion.setTienda(tienda);
            ubicacionService.crearUbicacion(ubicacion);
            System.out.println("Setting ubicacion");
            tienda.setUbicacion(ubicacion);
            System.out.println("Ubicacion: hecho");

            /* Set vacio de productos */
            System.out.println("Setting productos");
            Set<Producto> productos = new HashSet<>(); 
            tienda.setProductos(productos);
            
            /* Setteo el cliente en la tienda */
            System.out.println("Setting cliente");
            tienda.setCliente(cliente);

            System.out.println("GUARDANDO TIENDA"); 
            tiendaService.crearTienda(tienda); 
            UsuarioJwt user = cliente.getUsuariojwt();
            Set<Rol> roles = user.getRoles();
            roles.add(rolService.getByRolNombre(RolNombre.ROL_VENDEDOR).get());
            user.setRoles(roles);
            usuarioService.save(user);
            return new ResponseEntity<Tienda>(tienda, HttpStatus.OK);
        } catch (Exception e) { 
            for (FotosTienda foto : fotos) {
                if (foto.getId() != null){
                    fotosTiendaService.borrarFotosTienda(foto.getId());
                } 
            } 
            if (ubicacion.getId() != null){
                ubicacionService.borrarUbicacion(ubicacion.getId());
            } 
            if (tienda.getId() != null){
                tiendaService.borrarTienda(tienda.getId());
            } 
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tienda/obtenerTienda/{idTienda}/")
    public ResponseEntity<?> crearTienda(@RequestParam Long idUsuario, @PathVariable Long idTienda){
        try {
            if (usuarioService.getUsuarioById(idUsuario) != null){
                if (tiendaService.buscarTienda(idTienda).getCliente().getUsuariojwt().getId() == idUsuario){
                    if (usuarioService.getUsuarioById(idUsuario).getRoles().contains(rolService.getByRolNombre(RolNombre.ROL_VENDEDOR).get())){
                        return new ResponseEntity<Tienda>(tiendaService.buscarTienda(idTienda), HttpStatus.OK);
                    }else{
                        return new ResponseEntity<Mensaje>(new Mensaje("El usuario con el id "+ idUsuario +" no cumple con el rol de vendedor"), HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<Mensaje>(new Mensaje("El usuario con el id "+ idUsuario +" no es propietario de la tienda solicitada con el id " + idTienda), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("El usuario con el id "+ idUsuario +" no existe."), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
