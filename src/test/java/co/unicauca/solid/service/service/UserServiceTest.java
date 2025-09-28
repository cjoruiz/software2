package co.unicauca.solid.service;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.User;
import co.unicauca.utilities.exeption.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private IUserRepository repo;
    @InjectMocks private UserService service;

    /* ---------- helper ---------- */
    private static class TestUser extends User {
        @Override public String getRol() { return "ESTUDIANTE"; }
    }

    private User usuarioValido(String email) {
        User u = new TestUser();
        u.setEmail(email);
        u.setPassword("Valid1234!");          // cumple reglas de seguridad
        u.setNombres("Ana");
        u.setApellidos("Pérez");
        u.setPrograma("IS");
        return u;
    }

    /* ---------- 15 tests ---------- */

    @Test
    void registerUser_ok() throws Exception {
        User u = usuarioValido("new@unicauca.edu.co");
        when(repo.findByEmail("new@unicauca.edu.co")).thenReturn(null);
        when(repo.save(u)).thenReturn(true);

        assertDoesNotThrow(() -> service.registerUser(u));
    }

    @Test
    void registerUser_alreadyExists() {
        User existing = usuarioValido("old@unicauca.edu.co");
        when(repo.findByEmail("old@unicauca.edu.co")).thenReturn(existing);

        assertThrows(UserAlreadyExistsException.class,
                     () -> service.registerUser(existing));
    }

    @Test
    void registerUser_passwordDebil() {
        User u = usuarioValido("new@unicauca.edu.co");
        u.setPassword("123"); // no cumple política

        assertThrows(InvalidUserDataException.class,
                     () -> service.registerUser(u));
    }

    @Test
    void registerUser_emailMalFormado() {
        User u = usuarioValido("malformado");
        assertThrows(InvalidUserDataException.class,
                     () -> service.registerUser(u));
    }

    @Test
    void login_ok() throws Exception {
        User u = usuarioValido("ana@unicauca.edu.co");
        when(repo.findByEmail("ana@unicauca.edu.co")).thenReturn(u);
        when(repo.validateLogin("ana@unicauca.edu.co", "Valid1234!")).thenReturn(true);

        User logged = service.login("ana@unicauca.edu.co", "Valid1234!");
        assertEquals(u, logged);
    }

    @Test
    void login_emailNoExiste() {
        when(repo.findByEmail("no@unicauca.edu.co")).thenReturn(null);

        assertThrows(LoginException.class,
                     () -> service.login("no@unicauca.edu.co", "Pass123!"));
    }

    @Test
    void login_passwordIncorrecta() {
        User u = usuarioValido("ana@unicauca.edu.co");
        when(repo.findByEmail("ana@unicauca.edu.co")).thenReturn(u);
        when(repo.validateLogin("ana@unicauca.edu.co", "WrongPass")).thenReturn(false);

        assertThrows(LoginException.class,
                     () -> service.login("ana@unicauca.edu.co", "WrongPass"));
    }

    @Test
    void findByEmail_existe() throws Exception {
        User u = usuarioValido("ana@unicauca.edu.co");
        when(repo.findByEmail("ana@unicauca.edu.co")).thenReturn(u);

        User result = service.findByEmail("ana@unicauca.edu.co");
        assertEquals(u, result);
    }

    @Test
    void findByEmail_noExiste() {
        when(repo.findByEmail("no@unicauca.edu.co")).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                     () -> service.findByEmail("no@unicauca.edu.co"));
    }

    @Test
    void getUsersByRole_ok() throws Exception {
        List<User> lista = List.of(usuarioValido("a@unicauca.edu.co"));
        when(repo.findByRole("ESTUDIANTE")).thenReturn(lista);

        List<User> result = service.getUsersByRole("ESTUDIANTE");
        assertEquals(1, result.size());
    }

    @Test
    void getUsersByRole_vacio() throws InvalidUserDataException {
        when(repo.findByRole("ADMIN")).thenReturn(List.of());

        List<User> result = service.getUsersByRole("ADMIN");
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_ok() {
        List<User> lista = List.of(usuarioValido("a@unicauca.edu.co"));
        when(repo.list()).thenReturn(lista);

        List<User> result = service.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void updateUser_ok() throws Exception {
        User u = usuarioValido("ana@unicauca.edu.co");
        when(repo.findByEmail("ana@unicauca.edu.co")).thenReturn(u);
        when(repo.update(u)).thenReturn(true);

        assertDoesNotThrow(() -> service.updateUser(u));
    }

    @Test
    void updateUser_noExiste() {
        User u = usuarioValido("no@unicauca.edu.co");
        when(repo.findByEmail("no@unicauca.edu.co")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> service.updateUser(u));
    }
}