package com.unam.tf.service.tienda;
 
import java.util.Set;

import com.unam.tf.model.tienda.FotosTienda;

public interface IFotosTiendaService {
    public void crearFotosTienda(FotosTienda fotosTienda);

    public void borrarFotosTienda(Long id);

    public FotosTienda buscarFotosTienda(Long id); 
    
    public Set<FotosTienda> buscarTodasLasFotosTiendas(Long idTienda); 
    
}
