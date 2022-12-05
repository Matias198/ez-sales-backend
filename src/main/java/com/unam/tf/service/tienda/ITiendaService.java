package com.unam.tf.service.tienda;

import java.util.List;

import com.unam.tf.model.tienda.Tienda;

public interface ITiendaService {    
    public void crearTienda(Tienda tienda);

    public void borrarTienda(Long id);

    public void restaurarTienda(Long id);

    public Tienda buscarTienda(Long id);

    public List<Tienda> buscarTodasLasTiendas(); 
}
