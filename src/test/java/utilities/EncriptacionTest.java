package utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncriptacionTest {

    @Test
    public void testEncriptarMD5() {
        String texto = "test";
        String resultadoEsperado = "098f6bcd4621d373cade4e832627b4f6";

        String resultadoObtenido = Encriptacion.encriptarMD5(texto);

        assertEquals(resultadoEsperado, resultadoObtenido);
    }
}