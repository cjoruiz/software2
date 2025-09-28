package co.unicauca.solid.service;

import co.unicauca.solid.access.IProgramRepository;
import co.unicauca.solid.domain.Programa;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {

    @Mock private IProgramRepository repo;
    @InjectMocks private ProgramService service;

    /* ---------- helpers ---------- */
    private Programa programa(String id, String nombre) {
        Programa p = new Programa();
        p.setIdPrograma(id);
        p.setNombreCompleto(nombre);
        return p;
    }
    @Test
    void obtenerOpcionesProgramas() {
        when(repo.obtenerTodosProgramas())
                .thenReturn(List.of(
                        new Programa() {{ setIdPrograma("IS"); }},
                        new Programa() {{ setIdPrograma("TI"); }}));

        assertArrayEquals(new String[]{"IS", "TI"}, service.obtenerOpcionesProgramas());
    }

    @Test
    void obtenerNombreCompletoPrograma_encontrado() {
        Programa p = new Programa();
        p.setNombreCompleto("Ingeniería de Sistemas");
        when(repo.obtenerProgramaPorId("IS")).thenReturn(p);

        assertEquals("Ingeniería de Sistemas",
                service.obtenerNombreCompletoPrograma("IS"));
    }

    @Test
    void obtenerNombreCompletoPrograma_noEncontrado() {
        when(repo.obtenerProgramaPorId("XX")).thenReturn(null);
        assertEquals("Programa no encontrado",
                service.obtenerNombreCompletoPrograma("XX"));
    }
    /* ---------- 12 tests ---------- */

    @Test
    void crearPrograma_ok() {
        Programa p = programa("IS", "Ingeniería de Sistemas");
        when(repo.insertarPrograma(p)).thenReturn(true);

        assertTrue(service.crearPrograma(p));
    }

    @Test
    void crearPrograma_falla() {
        Programa p = programa("IS", "Ingeniería de Sistemas");
        when(repo.insertarPrograma(p)).thenReturn(false);

        assertFalse(service.crearPrograma(p));
    }

    @Test
    void obtenerPrograma_existe() {
        Programa p = programa("IS", "Ingeniería de Sistemas");
        when(repo.obtenerProgramaPorId("IS")).thenReturn(p);

        Programa result = service.obtenerPrograma("IS");
        assertEquals(p, result);
    }

    @Test
    void obtenerPrograma_noExiste() {
        when(repo.obtenerProgramaPorId("XX")).thenReturn(null);

        assertNull(service.obtenerPrograma("XX"));
    }

    @Test
    void obtenerTodosProgramas_ok() {
        when(repo.obtenerTodosProgramas())
                .thenReturn(List.of(
                        programa("IS", "Ingeniería de Sistemas"),
                        programa("TI", "Tecnologías de la Información")));

        List<Programa> lista = service.obtenerTodosProgramas();
        assertEquals(2, lista.size());
    }

    @Test
    void obtenerTodosProgramas_vacio() {
        when(repo.obtenerTodosProgramas()).thenReturn(List.of());

        List<Programa> lista = service.obtenerTodosProgramas();
        assertTrue(lista.isEmpty());
    }

    @Test
    void actualizarPrograma_ok() {
        Programa p = programa("IS", "Ingeniería de Sistemas Actualizada");
        when(repo.actualizarPrograma(p)).thenReturn(true);

        assertTrue(service.actualizarPrograma(p));
    }

    @Test
    void actualizarPrograma_falla() {
        Programa p = programa("IS", "Ingeniería de Sistemas");
        when(repo.actualizarPrograma(p)).thenReturn(false);

        assertFalse(service.actualizarPrograma(p));
    }

    @Test
    void eliminarPrograma_ok() {
        when(repo.eliminarPrograma("IS")).thenReturn(true);

        assertTrue(service.eliminarPrograma("IS"));
    }

    @Test
    void eliminarPrograma_falla() {
        when(repo.eliminarPrograma("XX")).thenReturn(false);

        assertFalse(service.eliminarPrograma("XX"));
    }

    @Test
    void obtenerOpcionesProgramas_ok() {
        when(repo.obtenerTodosProgramas())
                .thenReturn(List.of(
                        programa("IS", "Ingeniería de Sistemas"),
                        programa("TI", "Tecnologías de la Información")));

        String[] ops = service.obtenerOpcionesProgramas();
        assertArrayEquals(new String[]{"IS", "TI"}, ops);
    }

    @Test
    void obtenerOpcionesProgramas_vacio() {
        when(repo.obtenerTodosProgramas()).thenReturn(List.of());

        String[] ops = service.obtenerOpcionesProgramas();
        assertArrayEquals(new String[]{}, ops);
    }

    @Test
    void obtenerNombreCompletoPrograma_ok() {
        Programa p = programa("IS", "Ingeniería de Sistemas");
        when(repo.obtenerProgramaPorId("IS")).thenReturn(p);

        assertEquals("Ingeniería de Sistemas", service.obtenerNombreCompletoPrograma("IS"));
    }

    @Test
    void obtenerNombreCompletoPrograma_noExiste() {
        when(repo.obtenerProgramaPorId("XX")).thenReturn(null);

        assertEquals("Programa no encontrado", service.obtenerNombreCompletoPrograma("XX"));
    }
}