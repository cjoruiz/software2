package co.unicauca.solid.service;

import co.unicauca.solid.access.MockRepository;
import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.MensajeInterno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MensajeInternoServiceTest {

    private MensajeInternoService service;
    private MockRepository repo;
    private UserService userService;

    @BeforeEach
    void setUp() {
        repo = new MockRepository();
        userService = new UserService(repo);
        service = new MensajeInternoService(repo, userService);
    }

    @Test
    public void testEnviarMensajeDeEstudiante() throws Exception {
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Ea", "Aa", "123", "ING");
        Docente doc1 = new Docente("dir@unicauca.edu.co", "Pass123!", "D1", "Ba", "123", "ING", "PLANTA");
        Docente doc2 = new Docente("codir@unicauca.edu.co", "Pass123!", "D2", "Ca", "123", "ING", "OCASIONAL");
        userService.registerUser(est);
        userService.registerUser(doc1);
        userService.registerUser(doc2);

        int id = service.enviarMensaje(
            "est@unicauca.edu.co",
            "dir@unicauca.edu.co,codir@unicauca.edu.co",
            "Propuesta de proyecto",
            "Hola, adjunto mi idea...",
            "pdf".getBytes(),
            "idea.pdf"
        );
        assertTrue(id > 0);
    }

    @Test
    public void testObtenerMensajesPorEstudiante() throws Exception {
        Estudiante est = new Estudiante("est@unicauca.edu.co", "Pass123!", "Ea", "Aa", "123", "ING");
        Docente doc = new Docente("dir@unicauca.edu.co", "Pass123!", "Da", "Ba", "123", "ING", "PLANTA");
        userService.registerUser(est);
        userService.registerUser(doc);

        service.enviarMensaje("est@unicauca.edu.co", "dir@unicauca.edu.co", "Asunto", "Cuerpo", null, null);
        List<MensajeInterno> mensajes = service.obtenerMensajesEnviadosPorEstudiante("est@unicauca.edu.co");
        assertEquals(1, mensajes.size());
    }
}