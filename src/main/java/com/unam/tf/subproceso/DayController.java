package com.unam.tf.subproceso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.unam.tf.model.producto.Producto;

public class DayController {

    public static void run() {
        try {
            while (true) {
                DayControllerTasks task = new DayControllerTasks();
                List<Producto> productos = new ArrayList<>();
                System.out.println("SUBPROCESO DAYCONTROLLER INICIADO.");
                LocalDateTime date;
                Long counter = 0L;
                counter++;
                System.out.println("Repeticion " + counter);
                date = LocalDateTime.now();
                System.out.println("Buscando productos: ");
                productos = task.actualizarListaProductos();
                System.out.println("Iterando por producto...");
                for (Producto prod : productos) {
                    System.out.println("Producto: " + prod.getNombre());
                    LocalDateTime fecha = prod.getCaducidad().atStartOfDay();
                    if (fecha.minusDays(14L).isAfter(date)) {
                        System.out.println("Notificar: El producto " + prod.getNombre()
                                + " se encuentra cerca de su fecha de caducidad: " + prod.getCaducidad());
                    }
                    if (fecha.isBefore(date)) {
                        System.out.println(
                                "Notificar: El producto " + prod.getNombre() + " ya caducó: " + prod.getCaducidad());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}
