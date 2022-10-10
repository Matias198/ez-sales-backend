package com.unam.tf.repository.ubicacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unam.tf.model.ubicacion.Ubicacion;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long>{
    
}
