package com.unam.tf.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDto { 
    private String nombre;
    private Long precio;
    private Long descuento;
    private Long cantidad;
    private Long cantidadCritica;
    private String descripcion;
    private String caducidad; 
    private Long idTienda;  
}
