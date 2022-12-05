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
        Tienda tienda = buscarTienda(id);
        if (tienda != null){
            tienda.setActivo(false);
            tiendaRepository.save(tienda);
        }
        //tiendaRepository.deleteById(id); 
    }

    @Override
    public void restaurarTienda(Long id) {
        Tienda tienda = buscarTienda(id);
        if (tienda != null){
            tienda.setActivo(true);
            tiendaRepository.save(tienda);
        }
        //tiendaRepository.deleteById(id); 
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
