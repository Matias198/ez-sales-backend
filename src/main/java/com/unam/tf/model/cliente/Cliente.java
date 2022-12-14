package com.unam.tf.model.cliente;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unam.tf.model.mail.Mail;
import com.unam.tf.model.producto.Categoria;
import com.unam.tf.model.tienda.Tienda;
import com.unam.tf.model.tranasccion.Transaccion;
import com.unam.tf.model.ubicacion.Ciudad;
import com.unam.tf.security.entity.UsuarioJwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; 

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private byte[] fotoPerfil;
    private byte[] fotoBanner;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("mail")
    private Mail mail;   

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("usuariojwt")
    private UsuarioJwt usuariojwt;
 
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tiendas")
    //@JsonBackReference
    private Set<Tienda> tiendas;
    
    @ManyToOne() 
    @JoinColumn(name = "ciudad_id", referencedColumnName = "codCiudad")  
    @JsonManagedReference("clientes") 
    private Ciudad ciudad;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "cliente_categoria", joinColumns = @JoinColumn(name = "cliente_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<Categoria> categorias;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tr") 
    private Set<Transaccion> transacciones;

} 
