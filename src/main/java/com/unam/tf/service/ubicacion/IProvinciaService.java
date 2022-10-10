package com.unam.tf.service.ubicacion;

import java.util.List;

import com.unam.tf.model.dto.ProvinciaDto;
import com.unam.tf.model.ubicacion.Provincia;

public interface IProvinciaService {
    public void crearProvincia(Provincia provincia);

    public void borrarProvincia(Long id);

    public Provincia buscarProvincia(Long id);

    public List<Provincia> buscarTodasLasProvincias(); 

    public ProvinciaDto getProvinciaJson(String provinciaJson);
}
