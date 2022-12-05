package com.unam.tf.service.transaccion;

import java.util.List;

import com.unam.tf.model.tranasccion.Transaccion;

public interface ITransaccionService {
    public void crearTransaccion(Transaccion transaccion);

    public void borrarTransaccion(Long id);

    public void restaurarTransaccion(Long id);

    public Transaccion buscarTransaccion(Long id);

    public List<Transaccion> buscarTodasLasTransaccions(); 
}
