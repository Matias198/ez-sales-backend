package com.unam.tf.model.dto;
 

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class TiendaDto {  
    private String nombre;
    private String rubro;
    private String contacto;
    private Long codCiudad; 
    private Long dniCliente;
    private String latitud;
    private String longitud;
    
}
