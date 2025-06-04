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

    //Usuario y/o que está usando la APP
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

    /**
     * Abre una nueva sesión de Hibernate o retorna una existente para interactuar con la base de datos.
     * Registra en el log cualquier error al intentar abrir la sesión.
     *
     * @return una nueva instancia de Session
     * @throws Exception si ocurre un error al abrir la sesión
     */
    public static Session newSession() {
        try {
            return sessionFactory.openSession();
        } catch (Exception e) {
            System.out.println("Error al abrir la sesión: " + e.getMessage());
            escribirLogError("Error al crear la SessionFactory: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cierra la SessionFactory de Hibernate si está inicializada.
     * Libera los recursos asociados a la conexión con la base de datos.
     */
    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
