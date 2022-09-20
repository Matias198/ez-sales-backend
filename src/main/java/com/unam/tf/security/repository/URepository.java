package com.unam.tf.security.repository;

import com.unam.tf.security.entity.UsuarioJwt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface URepository extends JpaRepository<UsuarioJwt, Long>{
    Optional<UsuarioJwt> findByDniUsuario(Long dni);
    boolean existsByDniUsuario(Long dni);
}
