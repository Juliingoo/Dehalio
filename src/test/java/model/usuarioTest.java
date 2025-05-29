package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class usuarioTest {

    private usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new usuario();
    }

    @Test
    void getIdUsuario() {
        usuario.setIdUsuario(1);
        assertEquals(1, usuario.getIdUsuario());
    }

    @Test
    void setIdUsuario() {
        usuario.setIdUsuario(2);
        assertEquals(2, usuario.getIdUsuario());
    }

    @Test
    void getNombre() {
        usuario.setNombre("Ana");
        assertEquals("Ana", usuario.getNombre());
    }

    @Test
    void setNombre() {
        usuario.setNombre("Luis");
        assertEquals("Luis", usuario.getNombre());
    }

    @Test
    void getApellidos() {
        usuario.setApellidos("Pérez");
        assertEquals("Pérez", usuario.getApellidos());
    }

    @Test
    void setApellidos() {
        usuario.setApellidos("García");
        assertEquals("García", usuario.getApellidos());
    }

    @Test
    void getEmail() {
        usuario.setEmail("prueba@email.com");
        assertEquals("prueba@email.com", usuario.getEmail());
    }

    @Test
    void setEmail() {
        usuario.setEmail("nuevo@email.com");
        assertEquals("nuevo@email.com", usuario.getEmail());
    }

    @Test
    void getContrasenia() {
        usuario.setContrasenia("1234");
        assertEquals("1234", usuario.getContrasenia());
    }

    @Test
    void setContrasenia() {
        usuario.setContrasenia("abcd");
        assertEquals("abcd", usuario.getContrasenia());
    }

    @Test
    void getDireccion() {
        usuario.setDireccion("Calle Falsa 123");
        assertEquals("Calle Falsa 123", usuario.getDireccion());
    }

    @Test
    void setDireccion() {
        usuario.setDireccion("Avenida Siempre Viva");
        assertEquals("Avenida Siempre Viva", usuario.getDireccion());
    }

    @Test
    void getCodigoPostal() {
        usuario.setCodigoPostal("28080");
        assertEquals("28080", usuario.getCodigoPostal());
    }

    @Test
    void setCodigoPostal() {
        usuario.setCodigoPostal("10001");
        assertEquals("10001", usuario.getCodigoPostal());
    }

    @Test
    void getMunicipio() {
        usuario.setMunicipio("Madrid");
        assertEquals("Madrid", usuario.getMunicipio());
    }

    @Test
    void setMunicipio() {
        usuario.setMunicipio("Barcelona");
        assertEquals("Barcelona", usuario.getMunicipio());
    }

    @Test
    void getProvincia() {
        usuario.setProvincia("Madrid");
        assertEquals("Madrid", usuario.getProvincia());
    }

    @Test
    void setProvincia() {
        usuario.setProvincia("Valencia");
        assertEquals("Valencia", usuario.getProvincia());
    }

    @Test
    void isPropietario() {
        usuario.setPropietario(true);
        assertTrue(usuario.isPropietario());
    }

    @Test
    void setPropietario() {
        usuario.setPropietario(false);
        assertFalse(usuario.isPropietario());
    }

    @Test
    void getComercio() {
        comercio comercio = new comercio();
        usuario.setComercio(comercio);
        assertEquals(comercio, usuario.getComercio());
    }

    @Test
    void setComercio() {
        comercio comercio = new comercio();
        usuario.setComercio(comercio);
        assertSame(comercio, usuario.getComercio());
    }
}
