package com.unam.tf.model.tienda;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference; 
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty; 
import com.unam.tf.model.cliente.Cliente;
import com.unam.tf.model.producto.Producto;
import com.unam.tf.model.ubicacion.Ubicacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tienda") 
public class Tienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String rubro;
    private byte[] fotoPerfil;
    private Long valoracionTotalPromedio; 
    private String contacto;   
    private Boolean servicioEnvio;
    private Boolean activo;
    
    @OneToMany(mappedBy = "tienda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference 
    private Set<Producto> productos;

    @JsonProperty("ubicacion")
    @OneToOne(mappedBy = "tienda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Ubicacion ubicacion;

    @ManyToOne() 
    @JoinColumn(name = "cliente_id", referencedColumnName = "id") 
    @JsonBackReference(value = "tiendas")
    @NotNull
    private Cliente cliente;
}
