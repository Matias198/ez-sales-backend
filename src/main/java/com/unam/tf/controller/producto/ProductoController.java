package com.unam.tf.controller.producto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.dto.ProductoDto;
import com.unam.tf.model.producto.FotosProducto;
import com.unam.tf.model.producto.Producto;
import com.unam.tf.model.producto.Reseña;
import com.unam.tf.model.tienda.Tienda;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.producto.FotosProductoService;
import com.unam.tf.service.producto.ProductoService;
import com.unam.tf.service.producto.ReseñaService;
import com.unam.tf.service.tienda.TiendaService;

@RestController
public class ProductoController {
    @Autowired
    TiendaService tiendaService;

    @Autowired
    UService usuarioService;

    @Autowired
    ProductoService productoService;

    @Autowired
    ReseñaService reseñaService;

    @Autowired
    FotosProductoService fotosProductoService;

    @PostMapping("/producto/crearProducto")
    public ResponseEntity<?> crearProducto(@RequestParam("arrayMultipartFotos") MultipartFile[] arrayMultipartFotos, @RequestParam String productoJson){
        Set<FotosProducto> fotos = new HashSet<>(); 
        Set<Reseña> reseñas = new HashSet<>(); 
        Producto producto = new Producto();
        try {
            for (int i = 0; i < arrayMultipartFotos.length; i++) {
                System.out.println(arrayMultipartFotos[i].toString());
            }
            System.out.println(productoJson); 

            /* Mapeo ProductoDto con productoJson */
            ObjectMapper mapper = new ObjectMapper();
            ProductoDto productoDto = mapper.readValue(productoJson, ProductoDto.class);
            System.out.println("Mapped"); 

            /* Creo mi tienda en base al id asociado al productoDto */
            Tienda tienda = tiendaService.buscarTienda(productoDto.getIdTienda());
            System.out.println("Tienda: " + tienda.getNombre() + ". id: " + tienda.getId());
            
            /* Creo el producto para asociar con los datos */
            String str = productoDto.getCaducidad().toString();
            LocalDate fecha = LocalDate.parse(str);
            producto.setActivo(true);
            producto.setCantidad(productoDto.getCantidad());
            producto.setCantidadCritica(productoDto.getCantidadCritica());
            producto.setCaducidad(fecha);
            producto.setDescripcion(productoDto.getDescripcion());
            producto.setDescuento(productoDto.getDescuento());
            producto.setNombre(productoDto.getNombre());
            producto.setPrecio(productoDto.getPrecio());
            producto.setReseñas(reseñas);
            producto.setTienda(tienda);
            productoService.crearProducto(producto);

            /* Creo el SET de fotos y agrego a la tienda la referencia */
            System.out.println("Setting fotos");
            
            for (int i = 0; i < arrayMultipartFotos.length; i++) { 
                FotosProducto foto = new FotosProducto();
                foto.setProducto(producto);
                foto.setFoto(arrayMultipartFotos[i].getBytes());
                fotosProductoService.crearFotosProducto(foto);
                fotos.add(foto); 
            } 
            producto.setFotos(fotos);
            System.out.println("Fotos: hecho");

            System.out.println("GUARDANDO PRODUCTO"); 
            productoService.crearProducto(producto);
            return new ResponseEntity<Producto>(producto, HttpStatus.OK);
        } catch (Exception e) { 
            for (FotosProducto foto : fotos) {
                if (foto.getId() != null){
                    fotosProductoService.borrarFotosProducto(foto.getId());
                } 
            } 
            if (producto.getId() != null){
                productoService.borrarProducto(producto.getId());
            }  
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
