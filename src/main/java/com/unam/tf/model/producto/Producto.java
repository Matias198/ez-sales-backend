package com.unam.tf.model.producto;
 
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unam.tf.model.tienda.Tienda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto") 
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Long precio;
    private Long descuento = 0L;
    private Long cantidad;
    private Long cantidadCritica;
    private String descripcion;
    private LocalDate caducidad; 
    //caracteristicas[0][0]: Marca;
    //caracteristicas[0][1]: Samsung;
    //resultado -> Marca: Samsung;
    private Boolean activo;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Reseña> reseñas;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("fotos")
    private Set<FotosProducto> fotos;

    @ManyToOne() 
    @JoinColumn(name = "tienda_id", referencedColumnName = "id") 
    @JsonBackReference("productos")
    @NotNull
    private Tienda tienda;
}
