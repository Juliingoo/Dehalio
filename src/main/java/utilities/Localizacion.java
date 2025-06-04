package utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Localizacion {

    /**
     * Consulta un servicio externo para obtener la latitud y longitud aproximadas del usuario según su IP pública.
     *
     * @return un array de dos posiciones con la latitud y longitud, o null si ocurre un error
     */
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

    /**
     * Calcula la distancia en kilómetros entre dos puntos geográficos usando la fórmula del Haversine.
     * Considera la curvatura de la Tierra para obtener una distancia precisa entre las coordenadas dadas.
     *
     * @param lat1 latitud del primer punto
     * @param lon1 longitud del primer punto
     * @param lat2 latitud del segundo punto
     * @param lon2 longitud del segundo punto
     * @return distancia en kilómetros entre ambos puntos
     */
    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
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

