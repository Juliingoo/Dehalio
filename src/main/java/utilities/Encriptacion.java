package utilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Encriptacion {

    /**
     * Genera el hash MD5 en formato hexadecimal del texto recibido.
     * Devuelve null si ocurre un error durante el proceso de encriptación.
     *
     * @param texto texto a encriptar usando MD5
     * @return hash MD5 en formato hexadecimal, o null si hay error
     */
    public static String encriptarMD5(String texto){
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            LogAdministrador.escribirLogError("d");
            return null;
        }
    }
}
