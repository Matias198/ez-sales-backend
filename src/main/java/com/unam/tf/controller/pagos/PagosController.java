package com.unam.tf.controller.pagos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.preference.PreferenceItem;
import com.unam.tf.model.cliente.Cliente;
import com.unam.tf.model.producto.Producto;
import com.unam.tf.model.tranasccion.Transaccion;
import com.unam.tf.security.dto.Mensaje;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.cliente.ClienteService;
import com.unam.tf.service.producto.ProductoService;
import com.unam.tf.service.transaccion.TransaccionService;
 

@RestController
public class PagosController {

    @Value("${app.link.prod}")
    String linkProd;
    
    @Value("${back.link.prod}")
    String linkBack;

    @Autowired
    UService uService;

    @Autowired
    ProductoService productoService;

    @Autowired
    ClienteService clienteService;

    @Autowired
    TransaccionService transaccionService;

    @PostMapping("/pagos/generarTransaccion")
    public ResponseEntity<?> generarTransaccion(@RequestParam Long dniCliente, @RequestParam Long idProducto, @RequestParam Long cantidad) {
        try {
            if (cantidad <= 0){
                System.out.println("La cantidad a comprar no puede ser 0");
                return new ResponseEntity<Mensaje>(new Mensaje("La cantidad a comprar no puede ser 0"), HttpStatus.BAD_REQUEST);
            }
            UsuarioJwt user = uService.getUsuarioByDni(dniCliente).get(); 
            Producto producto = productoService.buscarProducto(idProducto);
            Long idVendedor = producto.getTienda().getCliente().getId();
            if (user.getCliente().getId() != idVendedor){  

                PreferenceClient client = new PreferenceClient(); 

                List<PreferenceItemRequest> items = new ArrayList<>();
                PreferenceItemRequest item =
                PreferenceItemRequest.builder()
                    .id(producto.getId().toString())
                    .title(producto.getNombre())
                    .currencyId("ARS")
                    .description(producto.getDescripcion())  
                    .quantity(Integer.valueOf(cantidad.toString()))
                    .unitPrice(new BigDecimal(String.valueOf(producto.getPrecio())))
                    .build();
                items.add(item); 

                String url = linkBack + "/auth/pagos/pagoAceptado?idCliente=" + user.getCliente().getId();
                PreferenceRequest request = PreferenceRequest.builder()
                .binaryMode(true) 
                .backUrls(PreferenceBackUrlsRequest.builder().failure(linkProd + "/detalles").success(url).pending(url).build())
                .items(items).build(); 
                try { 
                    return new ResponseEntity<>(client.create(request).getResponse(), HttpStatus.OK); 
                } catch (Exception e) {
                    System.out.println("Excepcion: " + e.getMessage());
                    return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.OK);
                } 
            }else{
                System.out.println("No puede comprarse a si mismo");
                return new ResponseEntity<Mensaje>(new Mensaje("No puede comprarse a si mismo"), HttpStatus.BAD_REQUEST);
            } 
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.getMessage());
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        } 
    }

    @GetMapping("/auth/pagos/pagoAceptado")
    public ResponseEntity<?> pagoAceptado(
        @RequestParam Long idCliente,
        @RequestParam Long collection_id, //Identificador de la cuenta de Mercado Pago recaudadora
        @RequestParam String collection_status, //Estado de la cuenta de Mercado Pago recaudadora
        @RequestParam Long payment_id, //Identificador del pago
        @RequestParam String payment_type, //Tipo de pago
        @RequestParam String status, //Estado del pago
        @RequestParam String merchant_order_id, //Id del pagador
        @RequestParam String preference_id, //Id de la preferencia
        HttpServletResponse response
        ) {
        try {  
            System.out.println(status);
            if (status.equals("approved")){
                Cliente cliente = clienteService.buscarCliente(idCliente); 
                PreferenceClient client = new PreferenceClient(); 
                List<PreferenceItem> items = new ArrayList<>();
                Preference preference = client.get(preference_id);
                items = preference.getItems();
                for (PreferenceItem preferenceItem : items) {
                    Transaccion transaccion = new Transaccion();
                    transaccion.setActivo(true); 
                    transaccion.setCliente(cliente); 
                    Producto producto = productoService.buscarProducto(Long.valueOf(preferenceItem.getId()));
                    transaccion.setProducto(producto);
                    String str = preference.getDateCreated().toString().split("T")[0];
                    transaccion.setFecha(LocalDate.parse(str));
                    transaccion.setCantidad(Long.valueOf(preferenceItem.getQuantity()));
                    transaccion.setPrecioUnitario(Long.valueOf(preferenceItem.getUnitPrice().toString()));
                    transaccion.setTotalTransaccion(transaccion.getCantidad() * transaccion.getPrecioUnitario());
                    transaccionService.crearTransaccion(transaccion);
                    System.out.println("Transaccion creada, ID: " + transaccion.getId()); 
                    System.out.println("Stock actual: " + producto.getCantidad() + ". Stock nuevo: " + (producto.getCantidad() - preferenceItem.getQuantity())); 
                    Integer cantidad = Integer.valueOf(producto.getCantidad().toString()) - preferenceItem.getQuantity();
                    producto.setCantidad(Long.valueOf(cantidad)); 
                    productoService.crearProducto(producto);
                    System.out.println("Stock actualizado");  
                    System.out.println("Notificando al vendedor"); 
                }
                System.out.println("Transacciones creadas.");  
                response.sendRedirect(linkProd + "/detalles");
                return new ResponseEntity<>( "OK", HttpStatus.OK); 
            }else{
                System.out.println("No se completo el pago.");  
                response.sendRedirect(linkProd + "/detalles/error/");
                return new ResponseEntity<>( "ERROR", HttpStatus.BAD_REQUEST); 
            }            
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.getMessage());
            return new ResponseEntity<Mensaje>(new Mensaje("Error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        } 
    }

}
