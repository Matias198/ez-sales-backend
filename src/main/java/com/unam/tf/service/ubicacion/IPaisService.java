package com.unam.tf.service.ubicacion;

import java.util.List;

import com.unam.tf.model.ubicacion.Pais;

public interface IPaisService {
    public void crearPais(Pais pais);

    public void borrarPais(Long id);

    public Pais buscarPais(Long id);

    public List<Pais> buscarTodosLosPaises();
    
    public Pais getPaisJsonJson(String paisJson);
}
