package com.unam.tf.controller.producto;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.dto.ProductoDto;
import com.unam.tf.model.producto.Categoria;
import com.unam.tf.model.producto.FotosProducto;
import com.unam.tf.model.producto.Producto;
import com.unam.tf.model.producto.Reseña;
import com.unam.tf.model.tienda.Tienda;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.producto.CategoriaService;
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

    @Autowired
    CategoriaService categoriaService;

    @PostMapping("/producto/crearProducto")
    public ResponseEntity<?> crearProducto(@RequestParam("arrayMultipartFotos") MultipartFile[] arrayMultipartFotos, @RequestParam String productoJson, @RequestParam String listaIndicesCat){
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

            
            /* Mapeo la lista de indices de categorias */
            String str2 = listaIndicesCat.substring(1,listaIndicesCat.length()-1);
            String split[] = str2.split(",");  
            
            Set<Categoria> categorias = new HashSet<>();
            for (String indice : split) {
                categorias.add(categoriaService.buscarCategoria(Long.valueOf(indice)));
            }


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
            producto.setNombre(productoDto.getNombre()); 
            producto.setPrecio(Long.valueOf(productoDto.getPrecio()));
            producto.setReseñas(reseñas);
            producto.setCategorias(categorias);
            producto.setTienda(tienda);
            productoService.crearProducto(producto);

            /* Creo el SET de fotos y agrego a la tienda la referencia */
            System.out.println("Setting fotos");
            
            for (int i = 0; i < arrayMultipartFotos.length; i++) { 
                FotosProducto foto = new FotosProducto();
                foto.setProducto(producto);
                foto.setActivo(true);
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

    @PostMapping("/producto/eliminarProducto/{idProducto}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long idProducto, @RequestParam Long idCliente){
        try {
            System.out.println("Buscando producto");
            Producto producto = productoService.buscarProducto(idProducto);
            System.out.println("Comprobando producto");
            if (producto.getTienda().getCliente().getUsuariojwt().getId() == idCliente){
                System.out.println("Eliminando producto (logico)");
                productoService.borrarProducto(idProducto);
                return new ResponseEntity<Mensaje>(new Mensaje("Eliminado con exito"), HttpStatus.OK); 
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Error al eliminar"), HttpStatus.INTERNAL_SERVER_ERROR); 
            }
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }

    
    @PostMapping("/producto/editarProducto")
    public ResponseEntity<?> editarProducto(@NotBlank @Valid @NotEmpty @RequestParam("arrayMultipartFotos") MultipartFile[] arrayMultipartFotos, @NotBlank @Valid @NotEmpty @RequestParam String productoJson, @RequestParam String listaIndicesCat){
        Producto auxProducto = new Producto();
        List<FotosProducto> fotosProductos = new ArrayList<>();
        List<FotosProducto> fotosAux = new ArrayList<>();
        Long idProducto = -1L;
        try {
            if (arrayMultipartFotos.length > 0){
                System.out.println("ProductoJSON: " + productoJson);
                System.out.println("ArrayMultipartFotos length: " + arrayMultipartFotos.length);
                ObjectMapper mapper = new ObjectMapper();
                ProductoDto productoDto = mapper.readValue(productoJson, ProductoDto.class);
                System.out.println("Mapped"); 
                Producto producto = productoService.buscarProducto(productoDto.getIdProducto());
                auxProducto = producto;
                idProducto = producto.getId();

                /* Fotos */ 
                for (FotosProducto foto : producto.getFotos()) {
                    fotosAux.add(foto);
                }

                /* Mapeo la lista de indices de categorias */
                String str2 = listaIndicesCat.substring(1,listaIndicesCat.length()-1);
                String split[] = str2.split(",");  
                
                Set<Categoria> categorias = new HashSet<>();
                for (String indice : split) {
                    categorias.add(categoriaService.buscarCategoria(Long.valueOf(indice)));
                }
            
                /* Creo el producto para asociar con los datos */
                String str = productoDto.getCaducidad().toString();
                LocalDate fecha = LocalDate.parse(str);

                producto.setNombre(productoDto.getNombre()); 
                producto.setPrecio(Long.valueOf(productoDto.getPrecio()));
                producto.setCaducidad(fecha); 
                producto.setCantidad(productoDto.getCantidad());
                producto.setCantidadCritica(productoDto.getCantidadCritica());
                producto.setDescripcion(productoDto.getDescripcion());              
                producto.setCategorias(categorias);

                System.out.println("Editado con exito, guardando...");
                
                for (FotosProducto aux : producto.getFotos()) {
                    aux.setActivo(false);
                    fotosProductoService.crearFotosProducto(aux);
                }

                for (MultipartFile aux : arrayMultipartFotos) {
                    FotosProducto foto = new FotosProducto();
                    foto.setProducto(producto);
                    foto.setActivo(true);
                    foto.setFoto(aux.getBytes());
                    fotosProductoService.crearFotosProducto(foto);
                    fotosProductos.add(foto);
                }
                System.out.println("Guardando...");
                productoService.crearProducto(producto);
                return new ResponseEntity<Producto>(producto, HttpStatus.OK);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Error: no se enviaron fotos"), HttpStatus.BAD_REQUEST);
            }
            
        } catch (Exception e) {
            if (fotosProductos.size() > 0){
                for (FotosProducto foto : fotosProductos) {
                    fotosProductoService.borrarFotosProductoPerm(foto.getId());
                }
            }
            if (fotosAux.size() > 0){
                for (FotosProducto foto : fotosAux) {
                    foto.setActivo(true);
                    fotosProductoService.crearFotosProducto(foto);
                }
            }
            if (idProducto != -1L){
                Producto producto = productoService.buscarProducto(idProducto);
                producto.setNombre(auxProducto.getNombre());
                producto.setCaducidad(auxProducto.getCaducidad());
                producto.setPrecio(auxProducto.getPrecio()); 
                producto.setCantidad(auxProducto.getCantidad());
                producto.setCantidadCritica(auxProducto.getCantidadCritica());
                producto.setDescripcion(auxProducto.getDescripcion());
                productoService.crearProducto(producto);
            }
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/producto/obtenerProducto")
    public ResponseEntity<?> obtenerProducto(@RequestParam String listaIndicesCat){
        try {
            /* Mapeo la lista de indices de categorias */
            String str2 = listaIndicesCat.substring(1,listaIndicesCat.length()-1);
            String split[] = str2.split(",");  
            System.out.println("String: " + str2);
            
            Set<Categoria> categorias = new HashSet<>();
            for (String indice : split) {
                categorias.add(categoriaService.buscarCategoria(Long.valueOf(indice)));
                System.out.println("Categoria: " + categoriaService.buscarCategoria(Long.valueOf(indice)).getNombre());
            }

            System.out.println("Buscando productos:");
            Set<Producto> productos = new HashSet<>();
            List<Producto> tProductos = productoService.buscarTodosLosProductos();
            for (Categoria categoria : categorias) { 
                for (int i = 0; i < tProductos.size(); i++) {
                    if (tProductos.get(i).getActivo()){
                        if (tProductos.get(i).getCategorias().contains(categoria)){
                            Set<FotosProducto> fotos = new HashSet<>();
                            for (FotosProducto foto : tProductos.get(i).getFotos()) {
                                if (foto.getActivo()){
                                    fotos.add(foto);
                                }
                            }
                            tProductos.get(i).setFotos(fotos);
                            productos.add(tProductos.get(i));
                            System.out.println("Producto " + tProductos.get(i).getNombre() + " agregado.");
                        }
                    } 
                }
            } 
            System.out.println("Finalizando busqueda, retornando valores.");
            return new ResponseEntity<>(productos, HttpStatus.OK); 
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }

    @PostMapping("/producto/obtenerUnProducto")
    public ResponseEntity<?> obtenerUnProducto(@RequestParam String idProducto){
        try { 
            System.out.println("Buscando producto id:" + idProducto);
            Producto producto = productoService.buscarProducto(Long.valueOf(idProducto));
            Set<FotosProducto> fotos = new HashSet<>();
            for (FotosProducto foto : producto.getFotos()) {
                if (foto.getActivo()){
                    fotos.add(foto);
                }
            }
            producto.setFotos(fotos);
            System.out.println("Producto: " + producto.getNombre());
            return new ResponseEntity<>(producto, HttpStatus.OK); 
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }

}
