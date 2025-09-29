package co.unicauca.solid.service;

import co.unicauca.solid.access.MockRepository;
import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.FilePG;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FilePGServiceTest {

    private FilePGService service;
    private MockRepository repo;
    private ProyectoGradoService proyectoService;
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        repo = new MockRepository();
        userService = new UserService(repo);
        proyectoService = new ProyectoGradoService(repo, userService);
        service = new FilePGService(repo, repo);
    }

    @Test
    public void testSubirFormatoAValido() throws Exception {
        Docente dir = new Docente("dir@unicauca.edu.co", "Pass123!", "Da", "Aa", "123", "ING", "PLANTA");
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Ea", "Ba", "123", "ING");
        userService.registerUser(dir);
        userService.registerUser(est);

        ProyectoGrado p = new ProyectoGrado("Titulo prueba", "INVESTIGACION", dir, null, est, "OGaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "OEaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        int idProyecto = proyectoService.crearProyecto(p);

        int idDoc = service.subirFormatoA(idProyecto, "contenido pdf".getBytes(), "formato.pdf");
        assertTrue(idDoc > 0);

        FilePG doc = service.obtenerFormatoAActual(idProyecto);
        assertEquals("FORMATO_A", doc.getTipoDocumento());
        assertEquals(1, doc.getVersion());
    }

    @Test
    public void testSubirCartaEmpresa() throws Exception {
        Docente dir = new Docente("dir@unicauca.edu.co", "Pass123!", "Da", "Aa", "123", "ING", "PLANTA");
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Ea", "Ba", "123", "ING");
        userService.registerUser(dir);
        userService.registerUser(est);

        ProyectoGrado p = new ProyectoGrado("Titulo de pruba", "PRACTICA_PROFESIONAL", dir, null, est, "OGaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "OEaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        int idProyecto = proyectoService.crearProyecto(p);

        int idDoc = service.subirCartaEmpresa(idProyecto, "carta".getBytes(), "carta.pdf");
        assertTrue(idDoc > 0);
        assertTrue(service.tieneCartaEmpresa(idProyecto));
    }
}