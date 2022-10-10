package com.unam.tf.service.ubicacion;

import java.util.List;

import com.unam.tf.model.dto.CiudadDto;
import com.unam.tf.model.ubicacion.Ciudad;

public interface ICiudadService {
    public void crearCiudad(Ciudad ciudad);

    public void borrarCiudad(Long id);

    public Ciudad buscarCiudad(Long id);

    public List<Ciudad> buscarTodasLasCiudades(); 

    public CiudadDto getCiudadJson(String ciudadJson);
}
