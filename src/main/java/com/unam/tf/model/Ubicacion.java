package com.unam.tf.model;
 
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table; 

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
@Table(name = "ubicacion")
public class Ubicacion implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pais;
    private String provincia;
    private String localidad;
    private String codigoPostal;
    private String coordenadasGoogle;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    @JsonBackReference
    private Cliente cliente;

}