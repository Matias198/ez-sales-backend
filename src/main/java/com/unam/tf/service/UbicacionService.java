package com.unam.tf.service;

import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.Ubicacion;
import com.unam.tf.repository.UbicacionRepository;

@Service
public class UbicacionService implements IUbicacionService{

    @Autowired
    UbicacionRepository ubicacionRepository;

    @Override
    public void crearUbicacion(Ubicacion ubicacion) {
        ubicacionRepository.save(ubicacion);
    }

    @Override
    public void borrarUbicacion(Long id) {
        ubicacionRepository.deleteById(id);
    }

    @Override
    public Ubicacion buscarUbicacion(Long id) {
        return ubicacionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Ubicacion> buscarTodasLasUbicaciones() {
        return ubicacionRepository.findAll();
    }
    
    @Override
    public Ubicacion getUbicacionJson(String ubicacionJson){
        Ubicacion ubicacion = new Ubicacion();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ubicacion = objectMapper.readValue(ubicacionJson, Ubicacion.class);
            return ubicacion;
        }catch (Exception e){
            System.out.println("Error en el mapeo de objeto ubicacion");
            return null;
        }
    }
}
