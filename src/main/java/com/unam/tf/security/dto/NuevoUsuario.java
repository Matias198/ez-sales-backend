package com.unam.tf.security.dto;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class NuevoUsuario {
    private Long dni;
    private String password;
    private Set<String> roles = new HashSet<>();

    @Override
    public String toString(){
        String dni, password, roles;
        dni = "\"dni:\""+getDni()+"\";";
        password = "\"password:\""+getPassword()+"\";";
        roles = "\"roles:\""+getRoles().toString();
        return dni+password+roles;
    }
}
