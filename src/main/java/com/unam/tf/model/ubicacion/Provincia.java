package com.unam.tf.model.ubicacion;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity; 
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provincia")
public class Provincia {
    @Id 
    private Long codProvincia;
    private String nombre;
    private boolean eliminado = false;

    @OneToMany(mappedBy = "provincia", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference 
    @JsonBackReference
    private Set<Ciudad> ciudades = new HashSet<>();
    
    @ManyToOne() 
    @JoinColumn(name = "pais_id", referencedColumnName = "codPais") 
    //@JsonBackReference
    @JsonManagedReference
    private Pais pais;
}
