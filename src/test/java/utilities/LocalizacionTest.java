package utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class LocalizacionTest {

    @Test
    public void testRendimientoCalcularDistancia() {
        int iteraciones = 5000000;
        double lat1 = 40.4168;  // Madrid
        double lon1 = -3.7038;
        double lat2 = 41.3879;  // Barcelona
        double lon2 = 2.16992;

        long inicio = System.currentTimeMillis();

        for (int i = 0; i < iteraciones; i++) {
            double distancia = Localizacion.calcularDistancia(lat1, lon1, lat2, lon2);
            assertTrue(distancia > 0);
        }

        long fin = System.currentTimeMillis();
        long total = fin - inicio;
        System.out.println("Tiempo total para " + iteraciones + " iteraciones: " + total + " ms");
        System.out.println("Tiempo promedio por llamada: " + (double) total / iteraciones + " ms");
    }
}