package com.unam.tf.service.ubicacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.ubicacion.Pais;
import com.unam.tf.repository.ubicacion.PaisRepository;

@Service
public class PaisService implements IPaisService{

    @Autowired
    PaisRepository paisRepository;

    @Override
    public void crearPais(Pais pais) {
        paisRepository.save(pais);        
    }

    @Override
    public void borrarPais(Long codPais) {
        paisRepository.deleteById(codPais);        
    }

    @Override
    public Pais buscarPais(Long codPais) {
        return paisRepository.findById(codPais).orElse(null);
    }

    @Override
    public List<Pais> buscarTodosLosPaises() { 
        return paisRepository.findAll();
    }

    @Override
    public Pais getPaisJsonJson(String paisJson){
        Pais pais = new Pais();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            pais = objectMapper.readValue(paisJson, Pais.class);
            return pais;
        }catch (Exception e){
            System.out.println("Error en el mapeo de objeto pais");
            return null;
        }
    }
    
}
