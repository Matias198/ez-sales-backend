package com.unam.tf.security.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mercadopago.MercadoPagoConfig;
import com.unam.tf.model.producto.Categoria;
import com.unam.tf.security.entity.Rol;
import com.unam.tf.security.entity.UsuarioJwt;
import com.unam.tf.security.enums.RolNombre;
import com.unam.tf.security.service.RolService;
import com.unam.tf.security.service.UService;
import com.unam.tf.service.producto.CategoriaService;
import com.unam.tf.service.ubicacion.CiudadService;
import com.unam.tf.service.ubicacion.PaisService;
import com.unam.tf.service.ubicacion.ProvinciaService;

@Component
public class CreateDefault implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String user;

    @Value("${spring.datasource.password}")
    String pass;

    @Value("${mercadopago.accesstoken}")
    String mpAccessToken;

    @Autowired
    RolService rolService;

    @Autowired
    PaisService paisService;

    @Autowired
    UService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CategoriaService categoriaService;

    @Autowired
    CiudadService ciudadService;

    @Autowired
    ProvinciaService provinciaService;

    @Override
    public void run(String... args) throws Exception {

        /* Mercado Pago Config */
        MercadoPagoConfig.setConnectionRequestTimeout(2000);
        MercadoPagoConfig.setSocketTimeout(2000);
        MercadoPagoConfig.setLoggingLevel(Level.FINEST);
        MercadoPagoConfig.setAccessToken(mpAccessToken);
        System.out.println("MERCADO PAGO CREADO");

        if (!(rolService.getByRolNombre(RolNombre.ROL_ADMINISTRADOR).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_ADMINISTRADOR);
            rolService.save(rolAdmin);
            System.out.println("ROL_ADMINISTRADOR CREADO");
        } else {
            System.out.println("ROL_ADMINISTRADOR EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_CLIENTE).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_CLIENTE);
            rolService.save(rolAdmin);
            System.out.println("ROL_CLIENTE CREADO");
        } else {
            System.out.println("ROL_CLIENTE EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_INVITADO).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_INVITADO);
            rolService.save(rolAdmin);
            System.out.println("ROL_INVITADO CREADO");
        } else {
            System.out.println("ROL_INVITADO EXISTENTE");
        }

        if (!(rolService.getByRolNombre(RolNombre.ROL_VENDEDOR).isPresent())) {
            Rol rolAdmin = new Rol(RolNombre.ROL_VENDEDOR);
            rolService.save(rolAdmin);
            System.out.println("ROL_VENDEDOR CREADO");
        } else {
            System.out.println("ROL_VENDEDOR EXISTENTE");
        }

        if (usuarioService.existsByDni(41419890L)) {
            System.out.println("Superusuario existente");
        } else {
            UsuarioJwt usuario = new UsuarioJwt();
            usuario.setActivo(true);
            usuario.setDniUsuario(41419890L);
            usuario.setCliente(null);
            usuario.setId(41419890L); 
            Set<Rol> roles = new HashSet<>();
            roles.add(rolService.getByRolNombre(RolNombre.ROL_ADMINISTRADOR).get());
            usuario.setRoles(roles);
            usuario.setPassword(passwordEncoder.encode("12345678"));
            usuarioService.save(usuario);
            System.out.println("SUPERUSUARIO CREADO");
        }

        List<String> nombres = new ArrayList<>();
        nombres.add("Accesorios Para Veh??culos");
        nombres.add("Agro");
        nombres.add("Alimentos y Bebidas");
        nombres.add("Animales y Mascotas");
        nombres.add("Antig??edades y Colecciones");
        nombres.add("Arte, Librer??a y Mercer??a");
        nombres.add("Autos, Motos y Otros");
        nombres.add("Beb??s");
        nombres.add("Belleza y Cuidado Personal");
        nombres.add("C??maras y Accesorios");
        nombres.add("Celulares y Tel??fonos");
        nombres.add("Computaci??n");
        nombres.add("Consolas y Videojuegos");
        nombres.add("Construcci??n");
        nombres.add("Deportes y Fitness");
        nombres.add("Electrodom??sticos y Aires Acondicionados");
        nombres.add("Electr??nica, Audio y Video");
        nombres.add("Entradas Para Eventos");
        nombres.add("Herramientas");
        nombres.add("Hogar, Muebles y Jard??n");
        nombres.add("Industrias y Oficinas");
        nombres.add("Inmuebles");
        nombres.add("Instrumentos Musicales");
        nombres.add("Joyas y Relojes");
        nombres.add("Juegos y Juguetes");
        nombres.add("Libros, Revistas y Comics");
        nombres.add("M??sica, Pel??culas y Series");
        nombres.add("Ropa y Accesorios");
        nombres.add("Salud y Equipamiento M??dico");
        nombres.add("Servicios");
        nombres.add("Souvenirs, Cotill??n y Fiestas");
        nombres.add("Otras Categor??as");

        if (categoriaService.buscarTodasLasCategorias().isEmpty()){
            for (String nombre : nombres) {
                Categoria categoria = new Categoria();
                categoria.setActivo(true);
                categoria.setNombre(nombre);
                categoriaService.crearCategoria(categoria);
                System.out.println("Categror??a \"" + nombre + "\" creada con exito.");
            }
        }

        if (paisService.buscarTodosLosPaises().isEmpty()){
            File archivo = null;
            FileReader fr = null;
            BufferedReader br = null;
            String query = "";
            try {
                // Apertura del fichero y creacion de BufferedReader para poder
                // hacer una lectura comoda (disponer del metodo readLine()).
                archivo = new File ("./src/main/resources/pais.txt");
                fr = new FileReader (archivo);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8)); 

                // Lectura del fichero
                String linea;
                while((linea=br.readLine())!=null)
                    query += " " + linea;
                System.out.println(query);

                Connection con = DriverManager.getConnection(url, user, pass);
                Statement stmt = con.createStatement();
                int count = stmt.executeUpdate(query);
                System.out.println("Filas afectadas: " + count);
                stmt.close(); 
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                    if( null != fr ){   
                    fr.close();     
                    }                  
                }catch (Exception e2){ 
                    e2.printStackTrace();
                }
            }
        } 

        if (provinciaService.buscarTodasLasProvincias().isEmpty()){
            File archivo = null;
            FileReader fr = null;
            BufferedReader br = null;
            String query = "";
            try {
                // Apertura del fichero y creacion de BufferedReader para poder
                // hacer una lectura comoda (disponer del metodo readLine()).
                archivo = new File ("./src/main/resources/provincias.txt");
                fr = new FileReader (archivo);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8)); 

                // Lectura del fichero
                String linea;
                while((linea=br.readLine())!=null)
                    query += " " + linea;
                System.out.println(query);

                Connection con = DriverManager.getConnection(url, user, pass);
                Statement stmt = con.createStatement();
                int count = stmt.executeUpdate(query);
                System.out.println("Filas afectadas: " + count);
                stmt.close(); 
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                    if( null != fr ){   
                    fr.close();     
                    }                  
                }catch (Exception e2){ 
                    e2.printStackTrace();
                }
            }
        } 

        if (ciudadService.buscarTodasLasCiudades().isEmpty()){
            File archivo = null;
            FileReader fr = null;
            BufferedReader br = null;
            String query = "";
            try {
                // Apertura del fichero y creacion de BufferedReader para poder
                // hacer una lectura comoda (disponer del metodo readLine()).
                archivo = new File ("./src/main/resources/ciudades.txt");
                fr = new FileReader (archivo);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8)); 

                // Lectura del fichero
                String linea;
                while((linea=br.readLine())!=null)
                    query += " " + linea;
                System.out.println(query);

                Connection con = DriverManager.getConnection(url, user, pass);
                Statement stmt = con.createStatement();
                int count = stmt.executeUpdate(query);
                System.out.println("Filas afectadas: " + count);
                stmt.close(); 
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                    if( null != fr ){   
                    fr.close();     
                    }                  
                }catch (Exception e2){ 
                    e2.printStackTrace();
                }
            }
        }  
    }
}
