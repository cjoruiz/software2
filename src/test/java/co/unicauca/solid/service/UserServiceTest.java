package co.unicauca.solid.service;

import co.unicauca.solid.access.MockRepository;
import co.unicauca.solid.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(new MockRepository());
    }

    @Test
    public void testRegisterUserNuevo() {
        User user = new User(
                "sofia@unicauca.edu.co",
                "12345",
                "Sofía",
                "Cortés",
                "3123456789",
                null,
                "Estudiante"
        );

        boolean result = userService.registerUser(user);
        assertTrue(result);
    }

    @Test
    public void testRegisterUserDuplicado() {
        User user1 = new User("sofia@unicauca.edu.co", "12345", "Sofía", "Cortés", "3123456789", null, "Estudiante");
        User user2 = new User("sofia@unicauca.edu.co", "54321", "Otra", "Persona", "3000000000", null, "Docente");

        userService.registerUser(user1);
        boolean result = userService.registerUser(user2);

        assertFalse(result); // No debe permitir duplicado
    }

    @Test
    public void testLoginCorrecto() {
        User user = new User("sofia@unicauca.edu.co", "12345", "Sofía", "Cortés", "3123456789", null, "Estudiante");
        userService.registerUser(user);

        User logged = userService.login("sofia@unicauca.edu.co", "12345");

        assertNotNull(logged);
        assertEquals("sofia@unicauca.edu.co", logged.getEmail());
    }

    @Test
    public void testLoginIncorrecto() {
        User user = new User("sofia@unicauca.edu.co", "12345", "Sofía", "Cortés", "3123456789", null, "Estudiante");
        userService.registerUser(user);

        User logged = userService.login("sofia@unicauca.edu.co", "wrongpass");

        assertNull(logged); // No debe loguear con contraseña incorrecta
    }

    @Test
    public void testGetUsersByRole() {
        User user1 = new User("sofia@unicauca.edu.co", "12345", "Sofía", "Cortés", "3123456789", null, "Estudiante");
        User user2 = new User("profesor@unicauca.edu.co", "abc123", "Carlos", "Ramírez", "3111111111", null, "Docente");

        userService.registerUser(user1);
        userService.registerUser(user2);

        var estudiantes = userService.getUsersByRole("Estudiante");
        var docentes = userService.getUsersByRole("Docente");

        assertEquals(1, estudiantes.size());
        assertEquals(1, docentes.size());
    }
}
