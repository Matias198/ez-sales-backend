package com.unam.tf.security.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter @Setter
public class UsuarioPrincipal implements UserDetails{
    private Long dni;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UsuarioPrincipal() {
    }

    public UsuarioPrincipal(Long dni, String password, Collection<? extends GrantedAuthority> authorities) {
        this.dni = dni;
        this.password = password;
        this.authorities = authorities;
    }
    
    public static UsuarioPrincipal build(UsuarioJwt usuarioJwt){
        List<GrantedAuthority> authorities = usuarioJwt.getRoles().stream().map(rol -> new SimpleGrantedAuthority(rol.getRolNombre().name())).collect(Collectors.toList());
        return new UsuarioPrincipal(usuarioJwt.getDniUsuario(), usuarioJwt.getPassword(), authorities);
    }
   
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return dni.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
