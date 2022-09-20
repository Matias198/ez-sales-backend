package com.unam.tf.controller;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.unam.tf.model.Cliente;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.RolService;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.IClienteService;
import com.unam.tf.service.IUploadFileService;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
public class ClienteController {
    
    @Autowired
    private IClienteService clienteService;

    @Autowired
    private UService uService;

    @Autowired
    private RolService rService;

    @GetMapping("cliente/obtenerUnCliente")
    @ResponseBody
    public ResponseEntity<?> obtenerUnCliente(@RequestParam Long dni) throws URISyntaxException {
        try {            
            return new ResponseEntity<Cliente>(uService.getClienteAsociado(dni), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Persona inexistente con DNI: " + dni), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("cliente/obtenerTodosLosClientes")
    @ResponseBody
    public ResponseEntity<?> obtenerTodosLosClientes(@RequestParam RolNombre rol) throws URISyntaxException {
        try {      
            if (rService.getByRolNombre(rol).get().getRolNombre() == RolNombre.ROL_ADMINISTRADOR){
                return new ResponseEntity<List<Cliente>>(clienteService.buscarTodosLosClientes(), HttpStatus.OK);
            }else{
                return new ResponseEntity<Mensaje>(new Mensaje("Rol no valido para esta operacion"), HttpStatus.BAD_REQUEST);
            }   
        } catch (Exception e) {
            return new ResponseEntity<Mensaje>(new Mensaje("Error inesperado: "+ e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
