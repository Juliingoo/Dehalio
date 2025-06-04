package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogAdministrador {
    static String rutaDirectorioLogs = Paths.get(System.getProperty("user.dir"), "logs").toString();

    private static Date date;
    private static DateFormat fechaHora = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

    static BufferedWriter writerLog;

    /**
     * Genera una cadena con la fecha, hora y el prefijo INFO para mostrar mensajes informativos en consola y logs.
     * Facilita la identificación de mensajes informativos en los registros.
     *
     * @return cadena formateada con fecha, hora y prefijo INFO
     */
    public static String inicioInfoLogConsola(){
        date = new Date();
        return fechaHora.format(date) + " - INFO - ";
    }

    /**
     * Genera una cadena con la fecha, hora y el prefijo ERROR para mostrar mensajes de error en consola y logs.
     * Permite distinguir fácilmente los mensajes de error en los registros.
     *
     * @return cadena formateada con fecha, hora y prefijo ERROR
     */
    public static String inicioErrorLogConsola(){
        date = new Date();
        return fechaHora.format(date) + " - ERROR - ";
    }

    /**
     * Verifica si el directorio de logs existe y lo crea si es necesario.
     * Inicializa el sistema de logs y maneja posibles errores de creación de directorio.
     * Es esencial para asegurar que los logs puedan ser escritos correctamente.
     */
    public static void comprobarDirectorioLogs(){
        File directorio = new File(rutaDirectorioLogs);

        if (!directorio.exists()) {
            try {
                if (directorio.mkdirs()) {
                    System.out.println("Directorio de logs creado");
                    iniciarLogs();
                } else {
                    System.out.println(fechaHora.format(date) + " - No se pudo crear el directorio Logs");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error al crear el directorio de logs: " + e.getMessage());
            }
        } else {
            System.out.println(inicioInfoLogConsola() + "El directorio Logs existe");
            iniciarLogs();
        }
    }

    /**
     * Inicializa el BufferedWriter para escribir en el archivo de log, usando un nombre basado en la fecha y hora actual.
     * Solo se ejecuta si el writer aún no ha sido creado.
     * Maneja errores de entrada/salida al abrir el archivo de log.
     */
    private static void iniciarLogs(){
        if (writerLog == null) {
            try {
                writerLog = new BufferedWriter(new FileWriter(rutaDirectorioLogs + "\\Log_" + fechaHora.format(date) + ".log"));
            } catch (IOException e) {
                System.out.println(inicioErrorLogConsola() + "Error al iniciar los logs: " + e.getMessage());
            }
        }
    }

    /**
     * Escribe una entrada de error en el archivo de log, incluyendo la fecha, hora y el mensaje recibido.
     * Crea el directorio de logs si no existe y maneja posibles errores de escritura.
     *
     * @param traza mensaje o traza de error a registrar
     */
    public static void escribirLogError(String traza) {
        File directorioLogs = new File(rutaDirectorioLogs);

        if (!directorioLogs.exists() || !directorioLogs.isDirectory()) {
            directorioLogs.mkdirs();
        }

        Date fecha = new Date();
        DateFormat fechaHora = new SimpleDateFormat("HH:mm:ss");

        try {
            writerLog.write(inicioErrorLogConsola() + traza + "\n");
            writerLog.flush();
        } catch (IOException e) {
            System.out.println(inicioErrorLogConsola() + "Error entrada/salida: " + e.getMessage());
        }
    }

    /**
     * Escribe una entrada informativa en el archivo de log, incluyendo la fecha, hora y el mensaje recibido.
     * Crea el directorio de logs si no existe y maneja posibles errores de escritura.
     *
     * @param traza mensaje informativo a registrar
     */
    public static void escribirLogInfo(String traza) {
        File directorioLogs = new File(rutaDirectorioLogs);
        if (!directorioLogs.exists() || !directorioLogs.isDirectory()) {
            directorioLogs.mkdirs();
        }

        Date fecha = new Date();
        DateFormat fechaHora = new SimpleDateFormat("HH:mm:ss");

        try {
            writerLog.write(inicioInfoLogConsola() + traza + "\n");
            writerLog.flush();
        } catch (IOException e) {
            System.out.println(inicioErrorLogConsola() + "Error entrada/salida: " + e.getMessage());
        }
    }
}
