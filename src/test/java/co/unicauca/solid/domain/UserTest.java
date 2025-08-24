package co.unicauca.solid.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testConstructorConParametros() {
        User user = new User(
                "sofia@unicauca.edu.co",
                "12345",
                "Sofía",
                "Cortés",
                "3123456789",
                "INGENIERIA SISTEMAS",   // <-- ahora String
                "Estudiante"
        );

        assertEquals("sofia@unicauca.edu.co", user.getEmail());
        assertEquals("12345", user.getPassword());
        assertEquals("Sofía", user.getNombres());
        assertEquals("Cortés", user.getApellidos());
        assertEquals("3123456789", user.getCelular());
        assertEquals("INGENIERIA SISTEMAS", user.getPrograma());
        assertEquals("Estudiante", user.getRol());
    }

    @Test
    public void testSettersYGetters() {
        User user = new User();
        user.setEmail("profesor@unicauca.edu.co");
        user.setPassword("securePass");
        user.setNombres("Carlos");
        user.setApellidos("Ramírez");
        user.setCelular("3111111111");
        user.setPrograma("TELEMATICA");   // <-- String
        user.setRol("Docente");

        assertEquals("profesor@unicauca.edu.co", user.getEmail());
        assertEquals("securePass", user.getPassword());
        assertEquals("Carlos", user.getNombres());
        assertEquals("Ramírez", user.getApellidos());
        assertEquals("3111111111", user.getCelular());
        assertEquals("TELEMATICA", user.getPrograma());
        assertEquals("Docente", user.getRol());
    }
}
