package controllers;

import model.usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class REGControllerTest {

    @Test
    void testSeguridadExisteUsuarioEmail() {
        REGController controller = new REGController();

        usuario u = new usuario();
        u.setEmail("emailquenoexiste@example.com");

        boolean resultado = controller.existeUsuario(u);

        assertTrue(resultado, "El email debe existir en la base de datos.");
    }
}
