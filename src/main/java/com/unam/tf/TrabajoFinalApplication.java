package com.unam.tf;

import java.util.logging.Level;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication 
public class TrabajoFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrabajoFinalApplication.class, args);

        /* Mercado Pago Config */
        MercadoPagoConfig.setConnectionRequestTimeout(2000);
        MercadoPagoConfig.setSocketTimeout(2000);
        MercadoPagoConfig.setLoggingLevel(Level.FINEST);

        System.out.println("EZ SALES API INICIADA");
    }

}
