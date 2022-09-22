package com.unam.tf.service;

import java.util.List;

import com.unam.tf.model.Ubicacion;

public interface IUbicacionService {
    public void crearUbicacion(Ubicacion ubicacion);

    public void borrarUbicacion(Long id);

    public Ubicacion buscarUbicacion(Long id);

    public List<Ubicacion> buscarTodasLasUbicaciones(); 

    public Ubicacion getUbicacionJson(String ubicacionJson);
}
