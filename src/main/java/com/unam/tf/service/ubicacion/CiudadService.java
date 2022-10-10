package com.unam.tf.service.ubicacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.dto.CiudadDto;
import com.unam.tf.model.ubicacion.Ciudad;
import com.unam.tf.repository.ubicacion.CiudadRepository;

@Service
public class CiudadService implements ICiudadService {

    @Autowired
    CiudadRepository ciudadRepository;

    @Override
    public void crearCiudad(Ciudad ciudad) {
        ciudadRepository.save(ciudad);
    }

    @Override
    public void borrarCiudad(Long codCiudad) {
        ciudadRepository.deleteById(codCiudad);
    }

    @Override
    public Ciudad buscarCiudad(Long codCiudad) {
        return ciudadRepository.findById(codCiudad).orElse(null);
    }

    @Override
    public List<Ciudad> buscarTodasLasCiudades() {
        return ciudadRepository.findAll();
    }

    @Override
    public CiudadDto getCiudadJson(String ciudadJson){
        CiudadDto ciudad = new CiudadDto();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ciudad = objectMapper.readValue(ciudadJson, CiudadDto.class);
            return ciudad;
        }catch (Exception e){
            System.out.println("Error en el mapeo de ciudad");
            return null;
        }
    }
    
}
