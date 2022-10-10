package com.unam.tf.repository.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unam.tf.model.producto.Reseña;

@Repository
public interface ReseñaRepository extends JpaRepository<Reseña, Long>{
    
}
