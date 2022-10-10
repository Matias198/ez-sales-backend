package com.unam.tf.service.tienda;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unam.tf.model.tienda.Tienda;
import com.unam.tf.repository.tienda.TiendaRepository;

@Service
public class TiendaService implements ITiendaService {

    @Autowired
    TiendaRepository tiendaRepository;

    @Override
    public void crearTienda(Tienda tienda) { 
        tiendaRepository.save(tienda);
    }

    @Override
    public void borrarTienda(Long id) {
        tiendaRepository.deleteById(id); 
    }

    @Override
    public Tienda buscarTienda(Long id) { 
        return tiendaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Tienda> buscarTodasLasTiendas() { 
        return tiendaRepository.findAll();
    }
    
}
