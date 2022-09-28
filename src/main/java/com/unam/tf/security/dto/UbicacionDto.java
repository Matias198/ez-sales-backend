package com.unam.tf.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDto {
    
    private Long id;
    private String pais;
    private String provincia;
    private String localidad;
    private String codigoPostal;
    private String coordenadasGoogle;

    @Override
    public String toString(){
        String id, pais, provincia, codigoPostal, coordenadasGoogle;
        id = "\"id:\""+getId()+"\";";
        pais = "\"pais:\""+getPais()+"\";";
        provincia = "\"provincia:\""+getProvincia()+"\";";
        codigoPostal = "\"codigoPostal:\""+getCodigoPostal()+"\";";
        coordenadasGoogle = "\"coordenadasGoogle:\""+getCoordenadasGoogle()+"\";";
        return id+pais+provincia+codigoPostal+coordenadasGoogle;
    }
}
