package com.unam.tf.service.producto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unam.tf.model.producto.Reseña;
import com.unam.tf.repository.producto.ReseñaRepository;

@Service
public class ReseñaService implements IReseñaService{

    @Autowired
    ReseñaRepository reseñaRepository;

    @Override
    public void crearReseña(Reseña reseña) {
        reseñaRepository.save(reseña);        
    }

    @Override
    public void borrarReseña(Long id) {
        reseñaRepository.deleteById(id);
    }

    @Override
    public Reseña buscarReseña(Long id) {
        return reseñaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Reseña> buscarTodasLasReseñas() {
        return reseñaRepository.findAll();
    }
    
}
