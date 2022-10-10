package com.unam.tf.service.producto;

import java.util.List;

import com.unam.tf.model.producto.Reseña;

public interface IReseñaService {
    public void crearReseña(Reseña reseña);

    public void borrarReseña(Long id);

    public Reseña buscarReseña(Long id);

    public List<Reseña> buscarTodasLasReseñas();  
    
}
