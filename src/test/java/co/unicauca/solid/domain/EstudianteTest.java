package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EstudianteTest {

    @Test
    public void testRolEstudiante() {
        Estudiante e = new Estudiante();
        assertEquals("ESTUDIANTE", e.getRol());
    }

    @Test
    public void testConstructorConDatos() {
        Estudiante e = new Estudiante(
            "carlos.ruiz@unicauca.edu.co", "Pass123!", "Carlos", "Ruiz", "3119876543", "ING"
        );
        assertEquals("carlos.ruiz@unicauca.edu.co", e.getEmail());
        assertEquals("ESTUDIANTE", e.getRol());
    }
}