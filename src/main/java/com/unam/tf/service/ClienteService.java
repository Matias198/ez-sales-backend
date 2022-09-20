
package com.unam.tf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unam.tf.model.Cliente;
import com.unam.tf.repository.ClienteRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements IClienteService{

    @Autowired
    ClienteRepository clienteRepository;
            
    @Override
    public void crearCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    @Override
    public void borrarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Override
    public List<Cliente> buscarTodosLosClientes(){
        return clienteRepository.findAll();
    }     
    
    public Cliente getClienteJson(String clienteJson){
        Cliente clienteFinal = new Cliente();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            clienteFinal = objectMapper.readValue(clienteJson, Cliente.class);
        }catch (Exception e){
            System.out.println("Error en el mapeo de objeto Cliente");
        }
        return clienteFinal;
    }
}
