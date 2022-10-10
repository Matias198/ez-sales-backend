package com.unam.tf.security.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUsuario {
    @NotBlank
    private Long dniUser;
    @NotBlank
    private String pass;
}
