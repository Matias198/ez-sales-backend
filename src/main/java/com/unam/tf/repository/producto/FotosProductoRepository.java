package com.unam.tf.repository.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unam.tf.model.producto.FotosProducto; 

@Repository
public interface FotosProductoRepository extends JpaRepository<FotosProducto, Long> {

}
