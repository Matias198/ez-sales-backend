package com.unam.tf.model.ubicacion;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity; 
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany; 
import javax.persistence.Table; 

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unam.tf.model.cliente.Cliente;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ciudad")
public class Ciudad {
    @Id 
    private Long codCiudad;
    private String nombre;
    private boolean eliminado = false;

    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference
    @JsonBackReference 
    private Set<Ubicacion> ubicaciones;

    @ManyToOne() 
    @JoinColumn(name = "provincia_id", referencedColumnName = "codProvincia")  
    @JsonManagedReference
    private Provincia provincia;

    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("clientes")
    private Set<Cliente> clientes;
}
