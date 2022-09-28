package com.unam.tf.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {

    private Long id;
    private String mail;
    private String codigo;
    private Boolean validado = false;

    @Override
    public String toString(){
        String id, mail, codigo, valido;
        id = "\"id:\""+getId()+"\";";
        mail = "\"mail:\""+getMail()+"\";";
        codigo = "\"codigo:\""+getCodigo()+"\";";
        valido = "\"valido:\""+getValidado()+"\";";
        return id+mail+codigo+valido;
    }
}
