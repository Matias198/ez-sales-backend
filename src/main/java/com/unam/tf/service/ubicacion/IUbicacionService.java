package com.unam.tf.service.ubicacion;

import java.util.List;

import com.unam.tf.model.ubicacion.Ubicacion;

public interface IUbicacionService {
    public void crearUbicacion(Ubicacion ubicacion);

    public void borrarUbicacion(Long id);

    public void borrarUbicacionLogico(Long id);

    public Ubicacion buscarUbicacion(Long id);

    public List<Ubicacion> buscarTodasLasUbicaciones(); 

    public Ubicacion getUbicacionJson(String ubicacionJson);
}
