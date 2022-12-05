package com.unam.tf.service.producto;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unam.tf.model.producto.Categoria;
import com.unam.tf.repository.producto.CategoriaRepository;

@Service
public class CategoriaService implements ICategoriaService {

    @Autowired
    CategoriaRepository categoriaRepository;

    @Override
    public void crearCategoria(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    @Override
    public void borrarCategoriaPerm(Long id) {
        categoriaRepository.deleteById(id);
    }

    @Override
    public void borrarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id).get();
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    @Override
    public void restaurarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id).get();
        categoria.setActivo(true);
        categoriaRepository.save(categoria);
    }

    @Override
    public Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Categoria> buscarTodasLasCategorias() {
        return categoriaRepository.findAll();
    }
    
}
