package co.unicauca.solid.service;

import co.unicauca.solid.access.MockRepository;
import co.unicauca.solid.domain.*;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProyectoGradoServiceTest {

    private ProyectoGradoService service;
    private MockRepository repo;
    private UserService userService;

    @BeforeEach
    void setUp() {
        repo = new MockRepository();
        userService = new UserService(repo);
        service = new ProyectoGradoService(repo, userService);
    }

    @Test
    public void testCrearProyectoValido() throws Exception {
        Docente dir = new Docente("dir@unicauca.edu.co", "Pass123!", "Director", "Aa", "123", "ING", "PLANTA");
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Estudiante", "Ba", "123", "ING");
        userService.registerUser(dir);
        userService.registerUser(est);

        ProyectoGrado p = new ProyectoGrado(
            "Sistema de Gestión Académica", "INVESTIGACION", dir, null, est,
            "Desarrollar un sistema...aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "1. Analizar...\n2. Diseñar...aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        );
        int id = service.crearProyecto(p);
        assertTrue(id > 0);
    }

    @Test
    public void testEvaluarFormatoAAprobado() throws Exception {
        // Setup
        Docente dir = new Docente("dir@unicauca.edu.co", "Pass123!", "Da", "Aa", "123", "ING", "PLANTA");
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Ea", "Ba", "123", "ING");
        Coordinador coord = new Coordinador("coord@unicauca.edu.co", "Pass123!", "Ca", "Ca", "123", "ING");
        userService.registerUser(dir);
        userService.registerUser(est);
        userService.registerUser(coord);

        ProyectoGrado p = new ProyectoGrado("Titulo prueba", "INVESTIGACION", dir, null, est, "OGaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "OEaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        int id = service.crearProyecto(p);

        // Evaluar
        boolean resultado = service.evaluarFormatoA(id, true, "", "coord@unicauca.edu.co");
        assertTrue(resultado);

        ProyectoGrado actualizado = service.obtenerProyecto(id);
        assertEquals("FORMATO_A_APROBADO", actualizado.getEstadoActual());
    }

    @Test
    public void testProcesarReintentoFormatoA() throws Exception {
        // Setup
        Docente dir = new Docente("dir@unicauca.edu.co", "Pass123!", "Da", "aA", "123", "ING", "PLANTA");
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Ea", "Ba", "123", "ING");
        userService.registerUser(dir);
        userService.registerUser(est);

        ProyectoGrado p = new ProyectoGrado("Titulo prueba", "INVESTIGACION", dir, null, est, "OGaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "OEaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        p.setNumeroIntento(1);
        p.setEstadoActual("FORMATO_A_RECHAZADO");
        int id = service.crearProyecto(p);

        // Reintentar
        boolean resultado = service.procesarReintentoFormatoA(id);
        assertTrue(resultado);

        ProyectoGrado actualizado = service.obtenerProyecto(id);
        assertEquals(2, actualizado.getNumeroIntento());
        assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", actualizado.getEstadoActual());
    }

    @Test
    public void testObtenerProyectosPorEstudiante() throws Exception {
        Docente dir = new Docente("dir@unicauca.edu.co", "Pass123!", "Da", "Aa", "123", "ING", "PLANTA");
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Ea", "Ba", "123", "ING");
        userService.registerUser(dir);
        userService.registerUser(est);

        ProyectoGrado p = new ProyectoGrado("Titulo prueba", "INVESTIGACION", dir, null, est, "OGaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "OEaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        service.crearProyecto(p);

        var proyectos = service.obtenerProyectosPorEstudiante("est@unicauca.edu.co");
        assertEquals(1, proyectos.size());
    }
}