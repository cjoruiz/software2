package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {

    @Test
    public void testConstructorStudent() {
        User student = new User(
                "estudiante@unicauca.edu.co",
                "Pass123.",
                "Ana",
                "Gómez",
                "3100000000",
                "INGENIERIA SISTEMAS",  // <-- String
                "Estudiante"
        );

        assertEquals("estudiante@unicauca.edu.co", student.getEmail());
        assertEquals("pass123", student.getPassword());
        assertEquals("Ana", student.getNombres());
        assertEquals("Gómez", student.getApellidos());
        assertEquals("3100000000", student.getCelular());
        assertEquals("INGENIERIA SISTEMAS", student.getPrograma());
        assertEquals("Estudiante", student.getRol());
    }
}
