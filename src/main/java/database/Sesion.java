package database;

import model.comercio;
import model.usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static utilities.LogAdministrador.escribirLogInfo;
import static utilities.LogAdministrador.escribirLogError;


public class Sesion {
    private static SessionFactory sessionFactory;

    //Usuario que está usando la APP
    // No se deben implementar getters y setters para esta variable estática porque representa el usuario actual de la aplicación.
    // Permitir acceso directo mediante getters y setters podría generar problemas de concurrencia o inconsistencias,
    // ya que múltiples hilos podrían modificar o acceder a esta variable de manera no controlada.
    public static usuario usuario;
    public static comercio comercio;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            System.out.println("Error al crear la SessionFactory: " + e.getMessage());
            escribirLogError("Error al crear la SessionFactory: " + e.getMessage());
        }
    }

    public static Session newSession() {
        try {
            return sessionFactory.openSession();
        } catch (Exception e) {
            System.out.println("Error al abrir la sesión: " + e.getMessage());
            escribirLogError("Error al crear la SessionFactory: " + e.getMessage());
            throw e;
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
