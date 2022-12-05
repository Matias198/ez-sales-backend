package com.unam.tf.service.transaccion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unam.tf.model.tranasccion.Transaccion;
import com.unam.tf.repository.transaccion.TransaccionRepository;

@Service
public class TransaccionService implements ITransaccionService{
    @Autowired
    TransaccionRepository transaccionRepository;

    @Override
    public void crearTransaccion(Transaccion transaccion) {
        transaccionRepository.save(transaccion);
    }

    @Override
    public void borrarTransaccion(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id).get();
        transaccion.setActivo(false);
        transaccionRepository.save(transaccion);
    }

    @Override
    public void restaurarTransaccion(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id).get();
        transaccion.setActivo(true);
        transaccionRepository.save(transaccion);
    }

    @Override
    public Transaccion buscarTransaccion(Long id) {
        return transaccionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Transaccion> buscarTodasLasTransaccions() {
        return transaccionRepository.findAll();
    }
    
}
