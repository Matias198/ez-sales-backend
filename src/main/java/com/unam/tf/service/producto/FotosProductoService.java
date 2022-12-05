package com.unam.tf.service.producto;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unam.tf.model.producto.FotosProducto;
import com.unam.tf.model.producto.Producto;
import com.unam.tf.repository.producto.FotosProductoRepository;

@Service
public class FotosProductoService implements IFotosProductoService{

    @Autowired
    FotosProductoRepository fotosProductoRepository;

    @Autowired
    ProductoService productoService;

    @Override
    public void crearFotosProducto(FotosProducto fotosProducto) {
        fotosProductoRepository.save(fotosProducto);
    }

    @Override
    public void borrarFotosProducto(Long id) {
        //fotosProductoRepository.deleteById(id);
        FotosProducto foto = fotosProductoRepository.findById(id).get();
        foto.setActivo(false);
        fotosProductoRepository.save(foto);
    }

    @Override
    public void restaurarFotosProducto(Long id){
        FotosProducto foto = fotosProductoRepository.findById(id).get();
        foto.setActivo(true);
        fotosProductoRepository.save(foto);
    }

    @Override
    public void borrarFotosProductoPerm(Long id) {
        fotosProductoRepository.deleteById(id);
    }

    @Override
    public FotosProducto buscarFotosProducto(Long id) {
        return fotosProductoRepository.findById(id).orElse(null) ;
    }

    @Override
    public Set<FotosProducto> buscarTodasLasFotosProductos(Long idProducto) { 
        for (Producto producto : productoService.buscarTodosLosProductos()) {
            if (producto.getId().equals(idProducto)){
                return producto.getFotos();
            }
        }
        return null;
    } 
    
}
