package com.unam.tf.model.tranasccion;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.unam.tf.model.cliente.Cliente;
import com.unam.tf.model.producto.Producto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaccion")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
 
    @ManyToOne() 
    @JoinColumn(name = "cliente_id", referencedColumnName = "id") 
    @JsonBackReference(value = "tr") 
    private Cliente cliente;
         
    @ManyToOne() 
    @JoinColumn(name = "producto_id", referencedColumnName = "id") 
    @JsonBackReference(value = "tr_prod") 
    private Producto producto;  

    private Long cantidad;
    private Long precioUnitario;
    private LocalDate fecha;
    private Long totalTransaccion; 
    private Boolean activo; 

}
