// Archivo: co.unicauca.solid.service.UserService.java
package co.unicauca.solid.service;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.LoginException;
import co.unicauca.utilities.exeption.UserAlreadyExistsException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import co.unicauca.utilities.security.PasswordValidator;
import co.unicauca.utilities.validators.ValidationUtil;
import java.util.List;

public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(Usuario user) throws UserAlreadyExistsException, InvalidUserDataException {
        validateUser(user);

        Usuario existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new UserAlreadyExistsException("El usuario con email " + user.getEmail() + " ya existe");
        }

        boolean saved = userRepository.save(user);
        if (!saved) {
            throw new RuntimeException("Error al guardar el usuario en la base de datos");
        }
    }

    public Usuario login(String email, String password) throws LoginException, InvalidUserDataException {
        ValidationUtil.validarEmail(email, "email");
        ValidationUtil.validarNoVacio(password, "contraseña");

        Usuario user = userRepository.findByEmail(email);
        if (user == null) {
            throw new LoginException("Correo no encontrado");
        }

        if (!userRepository.validateLogin(email, password)) {
            throw new LoginException("Contraseña incorrecta");
        }

        return user;
    }

    public Usuario findByEmail(String email) throws UserNotFoundException, InvalidUserDataException {
        ValidationUtil.validarEmail(email, "email:");

        Usuario user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(email);
        }

        return user;
    }

    public List<Usuario> getUsersByRole(String role) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(role, "rol");
        return userRepository.findByRole(role);
    }

    public List<Usuario> getAllUsers() {
        return userRepository.list();
    }

    public void updateUser(Usuario user) throws UserNotFoundException, InvalidUserDataException {
        validateUser(user);

        Usuario existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new UserNotFoundException(user.getEmail());
        }

        boolean updated = userRepository.update(user); // <-- ¡USANDO EL NUEVO MÉTODO!
        if (!updated) {
            throw new RuntimeException("Error al actualizar el usuario");
        }
    }

    private void validateUser(Usuario user) throws InvalidUserDataException {
        ValidationUtil.validarNoNulo(user, "usuario");
        ValidationUtil.validarEmail(user.getEmail(), "email");

        if (user.getPassword() == null) {
            throw new InvalidUserDataException("Contraseña requerida");
        }

        if (!PasswordValidator.isValid(user.getPassword())) {
            throw new InvalidUserDataException(PasswordValidator.getValidationDetails(user.getPassword()));
        }

        ValidationUtil.validarLongitud(user.getNombres(), "nombre", 2, 50);
        ValidationUtil.validarLongitud(user.getApellidos(), "apellido", 2, 50);
        ValidationUtil.validarLongitud(user.getPrograma(), "programa", 2, 100);
        ValidationUtil.validarLongitud(user.getRol(), "rol", 2, 20);
    }
}