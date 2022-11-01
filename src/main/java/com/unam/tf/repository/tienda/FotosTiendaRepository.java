package com.unam.tf.repository.tienda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unam.tf.model.tienda.FotosTienda;

@Repository
public interface FotosTiendaRepository extends JpaRepository<FotosTienda, Long> {
    
}
