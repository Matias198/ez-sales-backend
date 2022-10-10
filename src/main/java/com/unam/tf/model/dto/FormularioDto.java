package com.unam.tf.model.dto; 
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormularioDto {
    private String nombreCliente;
    private String apellidoCliente;
    private String mail; 
    private String nombreCiudad;
    private Long codCiudad;
    private Long dni;
    private String password;
    private String rol;
}
