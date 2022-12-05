package com.unam.tf.subproceso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.unam.tf.model.producto.Producto;
import com.unam.tf.service.producto.ProductoService;
 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
public class DayControllerTasks {
  
    @Autowired
    private ProductoService productoService;
    private List<Producto> productos;

    public List<Producto> actualizarListaProductos(){
        List<Producto> productos = new ArrayList<>();
        productos = productoService.buscarTodosLosProductos();
        return productos; 
    }
}
