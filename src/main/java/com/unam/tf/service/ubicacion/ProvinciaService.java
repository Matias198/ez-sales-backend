package com.unam.tf.service.ubicacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.dto.ProvinciaDto;
import com.unam.tf.model.ubicacion.Provincia;
import com.unam.tf.repository.ubicacion.ProvinciaRepository;

@Service
public class ProvinciaService implements IProvinciaService{

    @Autowired
    ProvinciaRepository provinciaRepository;

    @Override
    public void crearProvincia(Provincia provincia) {
        provinciaRepository.save(provincia);
    }

    @Override
    public void borrarProvincia(Long codProvincia) {
        provinciaRepository.deleteById(codProvincia);
    }

    @Override
    public Provincia buscarProvincia(Long codProvincia) {
        return provinciaRepository.findById(codProvincia).orElse(null);
    }

    @Override
    public List<Provincia> buscarTodasLasProvincias() {
        return provinciaRepository.findAll();
    }

    @Override
    public ProvinciaDto getProvinciaJson(String provinciaJson){
        ProvinciaDto provincia = new ProvinciaDto();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            provincia = objectMapper.readValue(provinciaJson, ProvinciaDto.class);
            return provincia;
        }catch (Exception e){
            System.out.println("Error en el mapeo de objeto provincia");
            return null;
        }
    }
    
}
