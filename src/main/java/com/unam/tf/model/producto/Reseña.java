package com.unam.tf.model.producto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reseña")
public class Reseña {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    //Valores del 1 al 10:
    private Integer satisfaccionProducto; 
    private Integer satisfaccionPrecio;
    private Integer satisfaccionAtencion;
    //--------------------
    private Long valoracionLocal;
    private Boolean activo;
    
    @ManyToOne() 
    @JoinColumn(name = "producto_id", referencedColumnName = "id") 
    @JsonBackReference
    @NotNull
    private Producto producto;
}
