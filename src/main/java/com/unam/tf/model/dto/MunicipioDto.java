package com.unam.tf.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MunicipioDto {
    private Long codCiudad; 
    private String nombreCiudad;
    private Long codProvincia;
    private String nombreProvincia;
}
