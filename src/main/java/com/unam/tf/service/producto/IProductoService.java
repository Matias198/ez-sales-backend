package com.unam.tf.service.producto;

import java.util.List;

import com.unam.tf.model.producto.Producto;

public interface IProductoService {
    public void crearProducto(Producto producto);

    public void borrarProducto(Long id);

    public Producto buscarProducto(Long id);

    public List<Producto> buscarTodosLosProductos(); 
    
}
