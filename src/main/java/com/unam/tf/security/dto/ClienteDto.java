package com.unam.tf.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {
    
    private Long id;
    private String nombre;
    private String apellido;

    @Override
    public String toString(){
        String id, nombre, apellido;
        id = "\"id:\""+getId()+"\";";
        nombre = "\"nombre:\""+getNombre()+"\";";
        apellido = "\"apellido:\""+getApellido()+"\";";
        return id+nombre+apellido;
    }
}
