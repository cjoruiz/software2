package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoordinadorTest {

    @Test
    public void testRolCoordinador() {
        Coordinador c = new Coordinador();
        assertEquals("COORDINADOR", c.getRol());
    }

    @Test
    public void testConstructorConDatos() {
        Coordinador c = new Coordinador(
            "laura.gomez@unicauca.edu.co", "Pass123!", "Laura", "Gómez", "3101234567", "ING"
        );
        assertEquals("laura.gomez@unicauca.edu.co", c.getEmail());
        assertEquals("COORDINADOR", c.getRol());
    }
}