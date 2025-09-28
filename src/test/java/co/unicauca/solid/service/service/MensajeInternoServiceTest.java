package co.unicauca.solid.service;

import co.unicauca.solid.access.IMensajeInternoRepository;
import co.unicauca.solid.domain.*;
import co.unicauca.utilities.exeption.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensajeInternoServiceTest {

    @Mock private IMensajeInternoRepository repo;
    @Mock private UserService userService;
    @InjectMocks private MensajeInternoService service;

    /* ---------- helpers ---------- */
    private Estudiante estudiante(String email) {
        Estudiante e = new Estudiante();
        e.setEmail(email);
        return e;
    }

    private Docente docente(String email) {
        Docente d = new Docente();
        d.setEmail(email);
        return d;
    }

    /* ---------- 15 tests ---------- */

    @Test
    void enviarMensaje_ok() throws Exception {
        Estudiante e = estudiante("e@unicauca.edu.co");
        Docente   d = docente("d@unicauca.edu.co");

        when(userService.findByEmail("e@unicauca.edu.co")).thenReturn(e);
        when(userService.findByEmail("d@unicauca.edu.co")).thenReturn(d);
        when(repo.enviarMensaje(any())).thenReturn(5);

        int id = service.enviarMensaje("e@unicauca.edu.co", "d@unicauca.edu.co",
                                       "Hola", "Body", null, null);
        assertEquals(5, id);
    }

    @Test
    void enviarMensaje_remitenteNoEsEstudiante() throws Exception {
        Docente d = docente("d@unicauca.edu.co");
        lenient().when(userService.findByEmail("d@unicauca.edu.co")).thenReturn(d);

        assertThrows(InvalidUserDataException.class,
                     () -> service.enviarMensaje("d@unicauca.edu.co", "x@unicauca.edu.co",
                                                 "Hola", "Body", null, null));
    }

    @Test
    void enviarMensaje_emailMalFormado() {
        assertThrows(InvalidUserDataException.class,
                () -> service.enviarMensaje("malformado", "d@unicauca.edu.co",
                                            "Hola", "Body", null, null));
    }

    @Test
    void enviarMensaje_destinatarioNoEsDocente() throws Exception {
        Estudiante e = estudiante("e@unicauca.edu.co");
        Estudiante otro = estudiante("otro@unicauca.edu.co");

        when(userService.findByEmail("e@unicauca.edu.co")).thenReturn(e);
        when(userService.findByEmail("otro@unicauca.edu.co")).thenReturn(otro);

        assertThrows(InvalidUserDataException.class,
                () -> service.enviarMensaje("e@unicauca.edu.co", "otro@unicauca.edu.co",
                                            "Hola", "Body", null, null));
    }

    @Test
    void obtenerMensajesEnviadosPorEstudiante_ok() throws Exception {
        Estudiante e = estudiante("e@unicauca.edu.co");
        when(userService.findByEmail("e@unicauca.edu.co")).thenReturn(e);

        List<MensajeInterno> lista = List.of(new MensajeInterno());
        when(repo.obtenerMensajesPorRemitente("e@unicauca.edu.co")).thenReturn(lista);

        List<MensajeInterno> result = service.obtenerMensajesEnviadosPorEstudiante("e@unicauca.edu.co");
        assertEquals(1, result.size());
    }

    @Test
    void obtenerMensajesEnviadosPorEstudiante_usuarioNoEsEstudiante() throws InvalidUserDataException, UserNotFoundException {
        Docente d = docente("d@unicauca.edu.co");
        when(userService.findByEmail("d@unicauca.edu.co")).thenReturn(d);

        assertThrows(InvalidUserDataException.class,
                () -> service.obtenerMensajesEnviadosPorEstudiante("d@unicauca.edu.co"));
    }

    @Test
    void obtenerMensajesRecibidosPorDocente_ok() throws Exception {
        Docente d = docente("d@unicauca.edu.co");
        when(userService.findByEmail("d@unicauca.edu.co")).thenReturn(d);

        List<MensajeInterno> lista = List.of(new MensajeInterno());
        when(repo.obtenerMensajesPorDestinatario("d@unicauca.edu.co")).thenReturn(lista);

        List<MensajeInterno> result = service.obtenerMensajesRecibidosPorDocente("d@unicauca.edu.co");
        assertEquals(1, result.size());
    }

    @Test
    void obtenerMensajesRecibidosPorDocente_usuarioNoEsDocente() throws InvalidUserDataException, UserNotFoundException {
        Estudiante e = estudiante("e@unicauca.edu.co");
        when(userService.findByEmail("e@unicauca.edu.co")).thenReturn(e);

        assertThrows(InvalidUserDataException.class,
                () -> service.obtenerMensajesRecibidosPorDocente("e@unicauca.edu.co"));
    }

    @Test
    void marcarMensajeComoLeido_ok() {
        when(repo.marcarComoLeido(5)).thenReturn(true);
        assertTrue(service.marcarMensajeComoLeido(5));
    }

    @Test
    void marcarMensajeComoLeido_falla() {
        when(repo.marcarComoLeido(99)).thenReturn(false);
        assertFalse(service.marcarMensajeComoLeido(99));
    }

    @Test
    void enviarMensaje_conAdjunto() throws Exception {
        Estudiante e = estudiante("e@unicauca.edu.co");
        Docente   d = docente("d@unicauca.edu.co");

        when(userService.findByEmail("e@unicauca.edu.co")).thenReturn(e);
        when(userService.findByEmail("d@unicauca.edu.co")).thenReturn(d);
        when(repo.enviarMensaje(any())).thenReturn(8);

        byte[] adjunto = "contenido".getBytes();
        int id = service.enviarMensaje("e@unicauca.edu.co", "d@unicauca.edu.co",
                                       "Con adjunto", "Body", adjunto, "file.pdf");
        assertEquals(8, id);
    }

    @Test
    void obtenerMensajesEnviadosPorEstudiante_vacio() throws Exception {
        Estudiante e = estudiante("e@unicauca.edu.co");
        when(userService.findByEmail("e@unicauca.edu.co")).thenReturn(e);
        when(repo.obtenerMensajesPorRemitente("e@unicauca.edu.co")).thenReturn(List.of());

        List<MensajeInterno> lista = service.obtenerMensajesEnviadosPorEstudiante("e@unicauca.edu.co");
        assertTrue(lista.isEmpty());
    }

    @Test
    void obtenerMensajesRecibidosPorDocente_vacio() throws Exception {
        Docente d = docente("d@unicauca.edu.co");
        when(userService.findByEmail("d@unicauca.edu.co")).thenReturn(d);
        when(repo.obtenerMensajesPorDestinatario("d@unicauca.edu.co")).thenReturn(List.of());

        List<MensajeInterno> lista = service.obtenerMensajesRecibidosPorDocente("d@unicauca.edu.co");
        assertTrue(lista.isEmpty());
    }
}