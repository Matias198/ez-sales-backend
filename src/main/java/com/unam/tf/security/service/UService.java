package com.unam.tf.security.service;

import com.unam.tf.model.Cliente;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.repository.URepository;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UService{
    
    @Autowired
    URepository usuarioRepository;
    
    public Optional<UsuarioJwt> getUsuarioByDni(Long dni){
        return usuarioRepository.findByDniUsuario(dni);
    }
    
    public boolean existsByDni(Long dni){
        return usuarioRepository.existsByDniUsuario(dni);
    }
    
    public void save(UsuarioJwt usuarioJwt){
        usuarioRepository.save(usuarioJwt);
    }

    public void delete(UsuarioJwt usuarioJwt){
        usuarioRepository.delete(usuarioJwt);
    }
    
    public Cliente getClienteAsociado(Long dni){
        return usuarioRepository.findByDniUsuario(dni).get().getCliente();
    }
}
