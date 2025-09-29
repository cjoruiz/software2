package co.unicauca.solid.service;

import co.unicauca.solid.access.MockRepository;
import co.unicauca.solid.domain.Coordinador;
import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.utilities.exeption.CoordinadorYaExisteException;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.LoginException;
import co.unicauca.utilities.exeption.UserAlreadyExistsException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private MockRepository repo;

    @BeforeEach
    void setUp() {
        repo = new MockRepository();
        userService = new UserService(repo);
    }

    @Test
    public void testRegistroEstudianteExitoso() throws Exception {
        Estudiante e = new Estudiante(
            "ana.lopez@unicauca.edu.co", "Pass123!", "Ana", "López", "3123456789", "ING"
        );
        userService.registerUser(e);
        Usuario encontrado = userService.findByEmail("ana.lopez@unicauca.edu.co");
        assertNotNull(encontrado);
        assertEquals("ESTUDIANTE", encontrado.getRol());
    }

    @Test
    public void testRegistroCoordinadorExitoso() throws Exception {
        Coordinador c = new Coordinador(
            "laura.gomez@unicauca.edu.co", "Pass123!", "Laura", "Gómez", "3101234567", "ING"
        );
        userService.registerUser(c);
        Usuario encontrado = userService.findByEmail("laura.gomez@unicauca.edu.co");
        assertNotNull(encontrado);
        assertEquals("COORDINADOR", encontrado.getRol());
    }

    @Test
    public void testRegistroCoordinadorDuplicadoMismoPrograma() {
        Coordinador c1 = new Coordinador(
            "laura.gomez@unicauca.edu.co", "Pass123!", "Laura", "Gómez", "3101234567", "ING"
        );
        Coordinador c2 = new Coordinador(
            "carlos.ruiz@unicauca.edu.co", "Pass123!", "Carlos", "Ruiz", "3119876543", "ING"
        );
        assertDoesNotThrow(() -> userService.registerUser(c1));
        assertThrows(CoordinadorYaExisteException.class, () -> userService.registerUser(c2));
    }

    @Test
    public void testLoginExitoso() throws Exception {
        Estudiante e = new Estudiante(
            "ana.lopez@unicauca.edu.co", "Pass123!", "Ana", "López", "3123456789", "ING"
        );
        userService.registerUser(e);
        Usuario logueado = userService.login("ana.lopez@unicauca.edu.co", "Pass123!");
        assertNotNull(logueado);
        assertEquals("ana.lopez@unicauca.edu.co", logueado.getEmail());
    }

    @Test
    public void testLoginConContrasenaIncorrecta() {
        Estudiante e = new Estudiante(
            "ana.lopez@unicauca.edu.co", "Pass123!", "Ana", "López", "3123456789", "ING"
        );
        assertDoesNotThrow(() -> userService.registerUser(e));
        assertThrows(LoginException.class, () -> userService.login("ana.lopez@unicauca.edu.co", "WrongPass!"));
    }

    @Test
    public void testObtenerUsuariosPorRol() throws Exception {
        userService.registerUser(new Estudiante("e1@unicauca.edu.co", "Pass123!", "E1", "Aa", "123", "ING"));
        userService.registerUser(new Docente("d1@unicauca.edu.co", "Pass123!", "D1", "Ba", "123", "ING", "PLANTA"));

        List<Usuario> estudiantes = userService.getUsersByRole("ESTUDIANTE");
        List<Usuario> docentes = userService.getUsersByRole("DOCENTE");

        assertEquals(1, estudiantes.size());
        assertEquals(1, docentes.size());
    }
}