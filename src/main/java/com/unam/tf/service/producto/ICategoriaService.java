package com.unam.tf.service.producto;

import java.util.List; 

import com.unam.tf.model.producto.Categoria;

public interface ICategoriaService {
    public void crearCategoria(Categoria categoria);

    public void borrarCategoriaPerm(Long id);

    public void borrarCategoria(Long id);

    public void restaurarCategoria(Long id);

    public Categoria buscarCategoria(Long id); 
    
    public List<Categoria> buscarTodasLasCategorias(); 
}
