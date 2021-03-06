package com.mycompany.proyectoboletas.controlador;

import com.mycompany.proyectoboletas.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Esteban E., Maximiliano C., Jorge M.
 */

public class Main {
    public static ClientesController clientesHandler = new ClientesController();
    public static Contabilidad contabilidad = new Contabilidad();
    public static NumComprobanteController numComprobante = new NumComprobanteController();
    public static InventarioController inventarioHandler = new InventarioController();

    public static void main(String[] args) {

        contabilidad.setComprobantesTotales();
        numComprobante.setComprobantes();

        menuDePruebas();
        
    }
    
    public static void menuDePruebas() {
        boolean repetir;
        boolean salir;
        int opcion;
        Scanner teclado = new Scanner(System.in);
        
        do {
            repetir = true;
            do {
                salir = false;
                opcion = -1;
                System.out.println("\nMenu de Ferretería [Beta]");
                System.out.println("------------< >------------");
                System.out.println("1 - Nueva Venta");
                System.out.println("2 - UNDEFINED");
                System.out.println("3 - UNDEFINED");
                System.out.println("4 - Menu Clientes");
                System.out.println("5 - Salir");
                
                try {
                    opcion = teclado.nextInt();
                } catch (InputMismatchException e) {
                    System.err.println("\nError al ingresar opción "+ e);
                    teclado.next();
                }
                
                switch (opcion) {
                    case 1:
                        menuVentas();
                        salir = false;
                        break;
                    case 2:
                        System.out.println("Unsupported operation");
                        salir = false;
                        break;
                    case 3:
                        System.out.println("Unsupported operation");
                        salir = false;
                        break;
                    case 4:
                        menuClientes();
                        salir = false;
                        break;
                    case 5:
                        salir = true;
                        break;
                    default:
                        salir = false;
                }
            } while (!salir);
            
            repetir = confirmarSalida();
            
        } while (repetir); // fin while menu principal

    } 
    
    public static void menuClientes() {
        boolean salir;
        int opcion;
        Scanner teclado = new Scanner(System.in);
        
            do {
                salir = false;
                opcion = -1;
                System.out.println("\n------------< Menu Clientes >------------");
                System.out.println("1 - Ver historial de Clientes");
                System.out.println("2 - Buscar Cliente");
                System.out.println("3 - Eliminar Cliente");
                System.out.println("4 - Salir");
                
                try {
                    opcion = teclado.nextInt();
                } catch (InputMismatchException e) {
                    System.err.println("\nError al ingresar opción "+ e);
                    teclado.next();
                }
                
                switch (opcion) {
                    case 1: // Lista de clientes
                        System.out.println(clientesHandler.toString());
                        break;
                    case 2: // Buscar cliente
                        System.out.println("   Búsqueda de Cliente");
                        String rutCliente = askRut();
                        if (clientesHandler.existeCliente(rutCliente)) {
                            System.out.println("\n-- Cliente encontrado --");
                            System.out.println(clientesHandler.getHistorialCliente(rutCliente));
                        } else {
                            System.out.println("\n-- Cliente no encontrado --");
                        }
                        break;
                    case 3: // Eliminar cliente
                        System.out.println("   Remover Cliente");
                        if (clientesHandler.removeCliente(askRut())) {
                            System.out.println("\n-- Cliente removido exitosamente --");
                            clientesHandler.guardar();
                        } else {
                            System.out.println("\n-- No se pudo eliminar este cliente --"); 
                        }
                        break;
                    case 4: // Salir
                        salir = true;
                        break;
                    default:
                        salir = false;
                }
            } while (!salir); // fin wh menu clientes
            
    }

    // TODO IMPORTANTE: FIXME, al quitar productos ya agregados a la canasta, recobrar su stock
    public static void menuVentas() {
        boolean salir;
        int opcion;
        Scanner teclado = new Scanner(System.in);
        Cliente clienteComprando = new Cliente();
        InventarioController inventarioVolatil = inventarioHandler;
        
            do {
                salir = false;
                opcion = -1;
                System.out.println("\n------------< Nueva Venta >------------");
                System.out.println("1 - Añadir Productos");
                System.out.println("2 - Quitar productos");
                System.out.println("3 - Ver Canasta");
                System.out.println("4 - Hacer Venta");
                System.out.println("5 - Salir");
                
                try {
                    opcion = teclado.nextInt();
                } catch (InputMismatchException e) {
                    System.err.println("\nError al ingresar opción "+ e);
                    teclado.next();
                }
                
                switch (opcion) {
                    case 1: // Añadir productos
                        inventarioVolatil.imprimir();
                        
                        System.out.println("   Añadir Producto");
                        int idProducto = askIdProducto();
                        boolean existe;
                        
                        existe = inventarioVolatil.existeProducto(idProducto);
                        
                        if (existe) {
                            boolean agregado = false;
                            boolean stockReducido = false;
                            Producto pdctoAgregado;
                            
                            try {
                                pdctoAgregado = inventarioVolatil.getProducto(idProducto);
                                stockReducido = inventarioVolatil.reducirStock(idProducto);
                                agregado = clienteComprando.getCanasta().addProducto(pdctoAgregado);
                            } catch (StockInsuficienteException e) {
                                System.out.println("Error: "+ e);
                            } catch (Exception e) {
                                System.err.println("Error al agregar producto: "+ e);
                            }
                            
                            if (agregado && stockReducido) {
                                inventarioVolatil.imprimir();
                                System.out.println("\n-- Producto Agregado a la Canasta --");
                                System.out.println(clienteComprando.getCanasta());
                            } else {
                                System.out.println("\n-- Producto no agregado --");
                            }
                        }
                        idProducto = -1;
                        salir = false;
                        break;
                    case 2: // Quitar productos 
                         try {
                             if (clienteComprando.getCanasta().getProductos().isEmpty()) {
                                 throw new CanastaVaciaException("No hay productos para eliminar");
                             } else {
                                 System.out.println(clienteComprando.getCanasta());
                                 System.out.println("   Remover Producto");
                                 int idProductoRemover = askIdProducto();
                                 if (clienteComprando.getCanasta().removeProducto(idProductoRemover)) {
                                     System.out.println("\n-- Producto Removido --");
                                     System.out.println(clienteComprando.getCanasta());
                                     inventarioVolatil.aumentarStock(idProductoRemover);
                                 } else {
                                     System.out.println("\n-- Producto no removido --");
                                 }
                             }
                        } catch (CanastaVaciaException e) {
                            System.err.println("Error: "+ e.getMessage()+ " "+
                                    e.getClass().getSimpleName());
                        } catch (Exception e) { 
                            System.err.println("Error: "+ e);
                        }
                        salir = false;
                        break;

                    case 3: // Ver canasta
                        try {
                            if (clienteComprando.getCanasta().getProductos().isEmpty()) {
                                throw new CanastaVaciaException("La canasta no tiene productos");
                            } else {
                                System.out.println(clienteComprando.getCanasta());
                            }
                        } catch (CanastaVaciaException e) {
                            System.err.println(e.getMessage() + " "+
                                    e.getClass().getSimpleName());
                        }
                        salir = false;
                        break;
                    case 4: // Hacer Venta
                        try {
                            salir = clienteComprando.hacerVenta();
                            if (salir) {
                                inventarioHandler.guardar();
                            }
                        } catch (CanastaVaciaException e) {
                            System.err.println("Error: "+ e.getMessage()+
                                    " "+ e.getClass().getCanonicalName()); 
                        }
                        break;
                    case 5: // Salir
                        inventarioVolatil.cargar();
                        salir = true;
                        break;
                    default:
                        salir = false;
                }
            } while (!salir); // fin wh menu venta
            
    } 
    
    public static boolean confirmarSalida() {
        Scanner teclado = new Scanner(System.in);
        boolean confirmar;
        String respuesta = "";
        
        System.out.println("\n¿Desea salir?");
        System.out.print("\n[y - Si] ");
        System.out.println(" [n - No]");

        try {
            respuesta = teclado.next();
        } catch (Exception e) { System.err.println("Error "+ e); }
        
        confirmar = respuesta.toLowerCase().charAt(0) == 'y';

        return !confirmar;
    }
    
    // FIXME añadir control de excepciones
    public static String askNombre() {
        Scanner teclado = new Scanner(System.in);
        System.out.println("\n   < Ingrese Nombre >");
        return teclado.nextLine();
    }
    
    // FIXME añadir control de excepciones
    public static String askRut() {
        Scanner teclado = new Scanner(System.in);
        System.out.println("   < Ingrese Rut >");
        return teclado.next();
    }
    
    public static int askIdProducto() {
        int id = -1;
        Scanner teclado = new Scanner(System.in);
        int inventarioSize = inventarioHandler.getInventario().size();
        
        while (id < 1 || id > inventarioSize) {
            id = -1;
            System.out.println("   < Ingrese ID >");
            try {
                id = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("Error: Ingrese un número "+ e);
                teclado.next();
            } catch (Exception e) {
                System.err.println("Error: "+ e);
                teclado.next();
            }
        }
        System.out.println("\nSeleccionado: "+ id);
        
        return id;
    }
    
}
