package com.unam.tf.security.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import com.unam.tf.model.Cliente;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "usuariojwt")
public class UsuarioJwt implements Serializable {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull private Long dniUsuario;
    @NotNull private String password;
    @NotNull private Boolean activo;
    @NotNull @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    @JsonBackReference
    private Cliente cliente;

    public UsuarioJwt() {
        this.activo = true;
    }

    public UsuarioJwt(Long dniUsuario, String password, Cliente cliente) {
        this.dniUsuario = dniUsuario;
        this.password = password;
        this.activo = true;
        this.cliente = cliente;
    }  

}
