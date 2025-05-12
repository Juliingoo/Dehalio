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

    static Date date = new Date();
    static DateFormat fechaHora = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

    static BufferedWriter writerLog;

    public static void comprobarDirectorioLogs(){
        File directorio = new File(rutaDirectorioLogs);

        // Se comprueba si el directorio Logs existe
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
                // Maneja cualquier excepción que ocurra al crear el directorio de logs
                System.out.println("Error al crear el directorio de logs: " + e.getMessage());
            }
        } else {
            System.out.println(fechaHora.format(date) + " - El directorio Logs existe");
            iniciarLogs();
        }
    }

    private static void iniciarLogs(){
        if (writerLog == null) {
            try {
                writerLog = new BufferedWriter(new FileWriter(rutaDirectorioLogs + "\\Log_" + fechaHora.format(date) + ".log"));
            } catch (IOException e) {
                // Maneja cualquier excepción que ocurra al crear el writer del log
                throw new RuntimeException("Error al crear el writer del log", e);
            }
        }
    }

    public static void escribirLogError(String traza) {
        File directorioLogs = new File(rutaDirectorioLogs);
        if (!directorioLogs.exists() || !directorioLogs.isDirectory()) {
            directorioLogs.mkdirs();
        }

        Date fecha = new Date();
        DateFormat fechaHora = new SimpleDateFormat("HH:mm:ss");

        try {
            writerLog.write(fechaHora.format(fecha) + " - " + "ERROR" + " - " + traza + "\n");
            writerLog.flush();
        } catch (IOException e) {
            // Maneja cualquier excepción que ocurra al escribir en el log
            System.out.println("Error entrada/salida: " + e.getMessage());
        }
    }

    public static void escribirLogInfo(String traza) {
        File directorioLogs = new File(rutaDirectorioLogs);
        if (!directorioLogs.exists() || !directorioLogs.isDirectory()) {
            directorioLogs.mkdirs();
        }

        Date fecha = new Date();
        DateFormat fechaHora = new SimpleDateFormat("HH:mm:ss");

        try {
            writerLog.write(fechaHora.format(fecha) + " - " + "INFO" + " - " + traza + "\n");
            writerLog.flush();
        } catch (IOException e) {
            // Maneja cualquier excepción que ocurra al escribir en el log
            System.out.println("Error entrada/salida: " + e.getMessage());
        }
    }
}
