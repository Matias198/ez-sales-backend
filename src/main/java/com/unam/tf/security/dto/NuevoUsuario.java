package com.unam.tf.security.dto;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class NuevoUsuario {
    @NotBlank
    private Long dni;
    @NotBlank
    private String password;
    private Set<String> roles = new HashSet<>();
}
