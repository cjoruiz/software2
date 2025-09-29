package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProgramaTest {

    @Test
    public void testConstructorCompleto() {
        Programa p = new Programa("ING", "Ingeniería de Sistemas", "Facultad de Ingeniería");
        assertEquals("ING", p.getIdPrograma());
        assertEquals("Ingeniería de Sistemas", p.getNombreCompleto());
        assertEquals("Facultad de Ingeniería", p.getFacultad());
    }

    @Test
    public void testEnumProgramas() {
        Programa.Programas ing = Programa.Programas.INGENIERIA_SISTEMAS;
        assertEquals("INGENIERIA_SISTEMAS", ing.getId());
        assertEquals("Ingeniería de Sistemas", ing.getNombreCompleto());
    }

    @Test
    public void testFromIdValido() {
        Programa.Programas p = Programa.Programas.fromId("INGENIERIA_SISTEMAS");
        assertEquals(Programa.Programas.INGENIERIA_SISTEMAS, p);
    }

    @Test
    public void testFromIdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Programa.Programas.fromId("INVALIDO"));
    }
}