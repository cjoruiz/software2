package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    public void testConstructorCompleto() {
        Usuario u = new Estudiante(
            "ana.lopez@unicauca.edu.co", "Pass123!", "Ana", "López", "3123456789", "ING"
        );
        assertEquals("ana.lopez@unicauca.edu.co", u.getEmail());
        assertEquals("Ana", u.getNombres());
        assertEquals("López", u.getApellidos());
        assertEquals("3123456789", u.getCelular());
        assertEquals("ING", u.getPrograma());
    }

    @Test
    public void testToStringIncluyeRol() {
        Usuario u = new Coordinador(
            "laura.gomez@unicauca.edu.co", "Pass123!", "Laura", "Gómez", "3101234567", "ING"
        );
        String str = u.toString();
        assertTrue(str.contains("COORDINADOR"));
        assertTrue(str.contains("laura.gomez@unicauca.edu.co"));
    }
}