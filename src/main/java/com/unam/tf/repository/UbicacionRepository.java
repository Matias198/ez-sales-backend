package com.unam.tf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.unam.tf.model.Ubicacion;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long>{
    
}
