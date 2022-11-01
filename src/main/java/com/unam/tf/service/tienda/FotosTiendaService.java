package com.unam.tf.service.tienda;
 
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unam.tf.model.tienda.FotosTienda;
import com.unam.tf.model.tienda.Tienda;
import com.unam.tf.repository.tienda.FotosTiendaRepository; 
import com.unam.tf.service.cliente.ClienteService;

@Service
public class FotosTiendaService implements IFotosTiendaService {

    @Autowired
    FotosTiendaRepository fotosTiendaRepository;

    @Autowired
    ClienteService clienteService;

    @Autowired
    TiendaService tiendaService;

    @Override
    public void crearFotosTienda(FotosTienda fotosTienda) {
        fotosTiendaRepository.save(fotosTienda);
    }

    @Override
    public void borrarFotosTienda(Long id) {
        fotosTiendaRepository.deleteById(id);
    }

    @Override
    public FotosTienda buscarFotosTienda(Long id) {
        return fotosTiendaRepository.findById(id).orElse(null);
    }

    @Override
    public Set<FotosTienda> buscarTodasLasFotosTiendas(Long idTienda) { 
        for (Tienda tienda : tiendaService.buscarTodasLasTiendas()) {
            if (tienda.getId().equals(idTienda)){
                return tienda.getFotos();
            }
        }
        return null;
    }
    
}
