package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProfessorTest {

    @Test
    public void testCrearProfesorConParametros() {
        User professor = new User();
        professor.setEmail("docente@unicauca.edu.co");
        professor.setPassword("pass456");
        professor.setNombres("Marta");
        professor.setApellidos("Gómez");
        professor.setCelular("3201111111");
        professor.setPrograma("INGENIERIA ELECTRONICA"); // <-- ahora String
        professor.setRol("Docente");

        assertEquals("docente@unicauca.edu.co", professor.getEmail());
        assertEquals("pass456", professor.getPassword());
        assertEquals("Marta", professor.getNombres());
        assertEquals("Gómez", professor.getApellidos());
        assertEquals("3201111111", professor.getCelular());
        assertEquals("INGENIERIA ELECTRONICA", professor.getPrograma()); // <-- String
        assertEquals("Docente", professor.getRol());
    }
}
