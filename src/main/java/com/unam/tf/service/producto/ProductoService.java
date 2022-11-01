package com.unam.tf.service.producto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unam.tf.model.producto.Producto;
import com.unam.tf.repository.producto.ProductoRepository; 

@Service
public class ProductoService implements IProductoService{

    @Autowired
    ProductoRepository productoRepository;

    @Override
    public void crearProducto(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public void borrarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public Producto buscarProducto(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Producto> buscarTodosLosProductos() {
        return productoRepository.findAll();
    }

    
    
}
