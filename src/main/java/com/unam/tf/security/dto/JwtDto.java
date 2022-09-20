package com.unam.tf.security.dto;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
public class JwtDto {
    private String token;
    private String bearer = "Bearer";
    private String dni;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtDto(String token, String dni, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.dni = dni;
        this.authorities = authorities;
    }
    
    
}
