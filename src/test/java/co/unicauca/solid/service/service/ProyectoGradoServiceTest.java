package co.unicauca.solid.service;

import co.unicauca.solid.access.IProyectoGradoRepository;
import co.unicauca.solid.domain.*;
import co.unicauca.solid.domain.enums.EstadoEnum;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoServiceTest {

    @Mock private IProyectoGradoRepository repo;
    @Mock private UserService userService;
    @InjectMocks private ProyectoGradoService service;

    /* ---------- helpers ---------- */
    private Estudiante estudiante(String email) {
        Estudiante e = new Estudiante();
        e.setEmail(email);
        return e;
    }

    private Docente docente(String email, String tipo) {
        Docente d = new Docente();
        d.setEmail(email);
        d.setTipoDocente(tipo);
        return d;
    }

    private Coordinador coordinador(String email) {
        Coordinador c = new Coordinador();
        c.setEmail(email);
        return c;
    }

    private ProyectoGrado proyectoMinimo() {
        Estudiante e = estudiante("e@unicauca.edu.co");
        Docente d = docente("d@unicauca.edu.co", "PLANTA");

        ProyectoGrado p = new ProyectoGrado();
        p.setTitulo("Título suficientemente largo para validación");
        p.setModalidad("INVESTIGACION");
        p.setEstudiante1(e);
        p.setDirector(d);
        p.setObjetivoGeneral("Objetivo general de prueba con más de 20 caracteres");
        p.setObjetivosEspecificos("Objetivos específicos de prueba con más de 30 caracteres");
        return p;
    }

    /* ---------- 17 tests ---------- */

    @Test
    void crearProyecto_ok() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        when(repo.insertarProyecto(p)).thenReturn(10);

        int id = service.crearProyecto(p);
        assertEquals(10, id);
        assertNotNull(p.getFechaCreacion());
        assertEquals(1, p.getNumeroIntento());
        assertEquals("EN_PRIMERA_EVALUACION_FORMATO_A", p.getEstadoActual());
    }

    @Test
    void crearProyecto_tituloCorto() {
        ProyectoGrado p = new ProyectoGrado();
        p.setTitulo("Corto");

        assertThrows(InvalidUserDataException.class, () -> service.crearProyecto(p));
    }

    @Test
    void obtenerProyecto_existe() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setIdProyecto(1);
        when(repo.obtenerProyectoPorId(1)).thenReturn(p);

        ProyectoGrado result = service.obtenerProyecto(1);
        assertEquals(p, result);
    }

    @Test
    void obtenerProyecto_noExiste() throws InvalidUserDataException {
        when(repo.obtenerProyectoPorId(99)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> service.obtenerProyecto(99));
    }

    @Test
    void obtenerProyectosPorEstudiante_ok() throws Exception {
        when(repo.obtenerProyectosPorEstudiante("e@unicauca.edu.co"))
                .thenReturn(List.of(proyectoMinimo()));

        Estudiante e = estudiante("e@unicauca.edu.co");
        when(userService.findByEmail("e@unicauca.edu.co")).thenReturn(e);

        List<ProyectoGrado> lista = service.obtenerProyectosPorEstudiante("e@unicauca.edu.co");
        assertEquals(1, lista.size());
    }

    @Test
    void obtenerProyectosPorEstudiante_usuarioNoEsEstudiante() throws InvalidUserDataException, UserNotFoundException {
        Docente d = docente("d@unicauca.edu.co", "PLANTA");
        when(userService.findByEmail("d@unicauca.edu.co")).thenReturn(d);

        assertThrows(InvalidUserDataException.class,
                () -> service.obtenerProyectosPorEstudiante("d@unicauca.edu.co"));
    }

    @Test
    void evaluarFormatoA_aprueba() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setIdProyecto(1);
        p.setEstadoActual(EstadoEnum.EN_PRIMERA_EVALUACION_FORMATO_A.getValor());
        when(repo.obtenerProyectoPorId(1)).thenReturn(p);

        Coordinador c = coordinador("coord@unicauca.edu.co");
        when(userService.findByEmail("coord@unicauca.edu.co")).thenReturn(c);

        when(repo.evaluarFormatoA(1, true, "Bien")).thenReturn(true);

        boolean ok = service.evaluarFormatoA(1, true, "Bien", "coord@unicauca.edu.co");
        assertTrue(ok);
    }

    @Test
    void procesarReintentoFormatoA_ok() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setIdProyecto(1);
        p.setEstadoActual(EstadoEnum.FORMATO_A_RECHAZADO.getValor());
        p.setNumeroIntento(1);
        when(repo.obtenerProyectoPorId(1)).thenReturn(p);
        when(repo.procesarReintentoFormatoA(1)).thenReturn(true);

        boolean ok = service.procesarReintentoFormatoA(1);
        assertTrue(ok);
    }

    @Test
    void puedeReintentarFormato_falsePorLimite() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setIdProyecto(1);
        p.setNumeroIntento(3); // límite alcanzado
        when(repo.obtenerProyectoPorId(1)).thenReturn(p);

        boolean puede = service.puedeReintentarFormato(1);
        assertFalse(puede);
    }

    @Test
    void obtenerEstadoLegible() {
        String legible = service.obtenerEstadoLegible("EN_PRIMERA_EVALUACION_FORMATO_A");
        assertEquals("En primera evaluación formato A", legible);
    }

    @Test
    void actualizarProyecto_ok() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setIdProyecto(1);
        when(repo.obtenerProyectoPorId(1)).thenReturn(p);
        when(repo.actualizarProyecto(p)).thenReturn(true);

        boolean ok = service.actualizarProyecto(p);
        assertTrue(ok);
    }

    @Test
    void eliminarProyecto_ok() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setIdProyecto(1);
        when(repo.obtenerProyectoPorId(1)).thenReturn(p);
        when(repo.eliminarProyecto(1)).thenReturn(true);

        boolean ok = service.eliminarProyecto(1);
        assertTrue(ok);
    }

    @Test
    void marcarRechazoDefinitivo_ok() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setIdProyecto(1);
        when(repo.obtenerProyectoPorId(1)).thenReturn(p);
        when(repo.marcarRechazoDefinitivo(1)).thenReturn(true);

        boolean ok = service.marcarRechazoDefinitivo(1);
        assertTrue(ok);
    }

    @Test
    void obtenerProyectosPorEstado_ok() throws Exception {
        ProyectoGrado p = proyectoMinimo();
        p.setEstadoActual("EN_PRIMERA_EVALUACION_FORMATO_A");
        when(repo.obtenerTodosProyectos()).thenReturn(List.of(p));

        List<ProyectoGrado> lista = service.obtenerProyectosPorEstado("EN_PRIMERA_EVALUACION_FORMATO_A");
        assertEquals(1, lista.size());
    }

    @Test
    void obtenerEstadisticasGenerales() throws InvalidUserDataException {
        ProyectoGrado p1 = proyectoMinimo();
        p1.setModalidad("INVESTIGACION");
        p1.setEstadoActual("EN_PRIMERA_EVALUACION_FORMATO_A");

        when(repo.obtenerTodosProyectos()).thenReturn(List.of(p1));

        String stats = service.obtenerEstadisticasGenerales();
        assertTrue(stats.contains("Total de proyectos: 1"));
        assertTrue(stats.contains("Proyectos de Investigación: 1"));
    }
}