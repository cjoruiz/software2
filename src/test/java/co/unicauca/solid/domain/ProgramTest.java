package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProgramTest {

    @Test
    public void testEnumValues() {
        Program[] values = Program.values();
        assertEquals(4, values.length, "Program debería tener 4 valores");
    }

    @Test
    public void testEnumContainsIngenieriaSistemas() {
        assertNotNull(Program.valueOf("INGENIERIA_SISTEMAS"));
    }

    @Test
    public void testEnumName() {
        Program prog = Program.AUTOMATICA_INDUSTRIAL;
        assertEquals("AUTOMATICA_INDUSTRIAL", prog.name());
    }
}
