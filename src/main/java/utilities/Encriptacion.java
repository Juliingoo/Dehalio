package utilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Encriptacion {
    public static String encriptarMD5(String texto){
        try {
            // Obtiene una instancia de MessageDigest con el algoritmo especificado
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // Convierte el texto a un array de bytes usando UTF-8 y genera el hash
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            // Crea un StringBuilder para construir la representación hexadecimal del hash
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            // Recorre cada byte del hash
            for (byte b : hash) {
                // Convierte el byte a una representación hexadecimal
                String hex = Integer.toHexString(0xff & b);
                // Si la longitud del string hexadecimal es 1, añade un 0 al principio
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                // Añade el string hexadecimal al StringBuilder
                hexString.append(hex);
            }
            // Devuelve el hash en formato hexadecimal como un string
            return hexString.toString();
        } catch (Exception e) {
            // Lanza una RuntimeException si el algoritmo especificado no existe
            System.out.println("Error: " + e.getMessage());
            LogAdministrador.escribirLogError("d");

            return null;
        }
    }
}
