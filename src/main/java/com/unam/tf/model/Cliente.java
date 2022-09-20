package com.unam.tf.model;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unam.tf.security.entity.UsuarioJwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String mail;
    private Blob fotoPerfil;
    private Blob fotoBanner; 

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private UsuarioJwt usuariojwt;

    public Cliente(Long id, UsuarioJwt usuariojwt) {
        this.id = id;
        this.usuariojwt = usuariojwt;
    }
}
