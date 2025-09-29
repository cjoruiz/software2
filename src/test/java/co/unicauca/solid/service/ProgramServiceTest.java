package co.unicauca.solid.service;

import co.unicauca.solid.access.MockRepository;
import co.unicauca.solid.domain.Programa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProgramServiceTest {

    private ProgramService service;
    private MockRepository repo;

    @BeforeEach
    void setUp() {
        repo = new MockRepository();
        service = new ProgramService(repo);
    }

    @Test
    public void testCrearYObtenerPrograma() {
        Programa p = new Programa("ING", "Ingeniería de Sistemas", "Facultad de Ingeniería");
        assertTrue(service.crearPrograma(p));

        Programa obtenido = service.obtenerPrograma("ING");
        assertNotNull(obtenido);
        assertEquals("Ingeniería de Sistemas", obtenido.getNombreCompleto());
        assertEquals("Facultad de Ingeniería", obtenido.getFacultad());
    }

    @Test
    public void testObtenerTodosProgramas() {
        service.crearPrograma(new Programa("ING", "Ingeniería de Sistemas", "FI"));
        service.crearPrograma(new Programa("ELEC", "Ingeniería Electrónica", "FI"));

        var programas = service.obtenerTodosProgramas();
        assertEquals(2, programas.size());
    }
}