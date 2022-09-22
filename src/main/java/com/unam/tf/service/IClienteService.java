package com.unam.tf.service;

import java.util.List;
import com.unam.tf.model.Cliente;

public interface IClienteService {

    public void crearCliente(Cliente cliente);

    public void borrarCliente(Long id);

    public Cliente buscarCliente(Long id);

    public List<Cliente> buscarTodosLosClientes();     

    public Cliente getClienteJson(String clienteJson);
}
