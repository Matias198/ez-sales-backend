package com.unam.tf.service.producto;

import java.util.Set;

import com.unam.tf.model.producto.FotosProducto;

public interface IFotosProductoService {
    public void crearFotosProducto(FotosProducto fotosProducto);

    public void borrarFotosProductoPerm(Long id);

    public void borrarFotosProducto(Long id);

    public void restaurarFotosProducto(Long id);

    public FotosProducto buscarFotosProducto(Long id); 
    
    public Set<FotosProducto> buscarTodasLasFotosProductos(Long idProducto); 
}
