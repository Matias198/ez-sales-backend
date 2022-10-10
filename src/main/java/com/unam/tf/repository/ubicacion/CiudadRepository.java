package com.unam.tf.repository.ubicacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unam.tf.model.ubicacion.Ciudad;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
    
}
