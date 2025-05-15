package utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Localizacion {
    public static double[] obtenerCoordenadasUsuario() {
        System.out.println(LogAdministrador.inicioInfoLogConsola() + "Obteniendo coordenadas del usuario");
        LogAdministrador.escribirLogInfo("Obteniendo coordenadas del usuario");
        try {
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            double lat = json.get("lat").getAsDouble();
            double lon = json.get("lon").getAsDouble();
            return new double[]{lat, lon};
        } catch (Exception e) {
            LogAdministrador.escribirLogError("Error al obtener coordenadas del usuario");
            System.out.println(LogAdministrador.inicioErrorLogConsola() + "Error al obtener coordenadas del usuario: " + e.getMessage());
            return null;
        }
    }

    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        System.out.println(LogAdministrador.inicioInfoLogConsola() + "Calculando distancia");
        LogAdministrador.escribirLogInfo("Calculando distancia");
        double radioTierra = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radioTierra * c;
    }

}

