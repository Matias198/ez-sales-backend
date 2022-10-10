package com.unam.tf.model.ubicacion;
 
import java.io.Serializable;
 
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
 
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
@Table(name = "ubicacion")
public class Ubicacion implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String coordenadas;
    private Boolean activo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tienda_id", referencedColumnName = "id")
    private Tienda tienda;

    @ManyToOne() 
    @JoinColumn(name = "ciudad_id", referencedColumnName = "codCiudad") 
    @JsonManagedReference 
    @NotNull
    private Ciudad ciudad;

    /*
    @ManyToOne() 
    @JoinColumn(name = "ciudad_id", referencedColumnName = "codCiudad") 
    @JsonBackReference 
    @NotNull
    private Ciudad ciudad;
     */
}
