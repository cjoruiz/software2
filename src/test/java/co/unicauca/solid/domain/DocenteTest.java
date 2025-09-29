package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DocenteTest {

    @Test
    public void testRolDocente() {
        Docente d = new Docente();
        assertEquals("DOCENTE", d.getRol());
    }

    @Test
    public void testConstructorConTipoDocente() {
        Docente d = new Docente(
            "juan.perez@unicauca.edu.co", "Pass123!", "Juan", "Pérez", "3156789012", "ING", "PLANTA"
        );
        assertEquals("juan.perez@unicauca.edu.co", d.getEmail());
        assertEquals("PLANTA", d.getTipoDocente());
        assertEquals("DOCENTE", d.getRol());
    }

    @Test
    public void testToStringIncluyeTipoDocente() {
        Docente d = new Docente(
            "maria.sanchez@unicauca.edu.co", "Pass123!", "María", "Sánchez", "3167890123", "ING", "OCASIONAL"
        );
        String str = d.toString();
        assertTrue(str.contains("maria.sanchez@unicauca.edu.co"));
        assertTrue(str.contains("OCASIONAL"));
    }
}