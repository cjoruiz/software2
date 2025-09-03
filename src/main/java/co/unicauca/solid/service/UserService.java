/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.service;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.access.UserRepository;
import co.unicauca.solid.domain.User;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.LoginException;
import co.unicauca.utilities.exeption.UserAlreadyExistsException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import co.unicauca.utilities.security.PasswordValidator;
import co.unicauca.utilities.validators.ValidationUtil;
import java.util.List;

/**
 *
 * @author crist
 */
public class UserService {
    private final IUserRepository userRepository;
    
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public void registerUser(User user) throws UserAlreadyExistsException, InvalidUserDataException {
        // Validar datos
        validateUser(user);
        
        // Verificar si el usuario ya existe
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new UserAlreadyExistsException("El usuario con email " + user.getEmail() + " ya existe");
        }
        
        // Intentar guardar
        boolean saved = userRepository.save(user);
        if (!saved) {
            throw new RuntimeException("Error al guardar el usuario en la base de datos");
        }
    }
    
    public User login(String email, String password) throws LoginException, InvalidUserDataException {
        // Validar datos de entrada usando ValidationUtil
        ValidationUtil.validarEmail(email, "email");
        ValidationUtil.validarNoVacio(password, "contraseña");
        
        // Buscar usuario
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new LoginException("Correo no encontrado");
        }
        
        // Validar contraseña
        if (!userRepository.validateLogin(email, password)) {
            throw new LoginException("Contraseña incorrecta");
        }
        
        return user;
    }
    
    public  User findByEmail(String email) throws UserNotFoundException, InvalidUserDataException {
        ValidationUtil.validarEmail(email, "email:");
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(email);
        }
        
        return user;
    }
    
    public List<User> getUsersByRole(String role) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(role, "rol");
        return userRepository.findByRole(role);
    }
    
    public List<User> getAllUsers() {
        return userRepository.list();
    }
    
    public void updateUser(User user) throws UserNotFoundException, InvalidUserDataException {
        validateUser(user);
        
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new UserNotFoundException(user.getEmail());
        }
        
        boolean updated = userRepository.save(user);
        if (!updated) {
            throw new RuntimeException("Error al actualizar el usuario");
        }
    }
    
    private void validateUser(User user) throws InvalidUserDataException {
        ValidationUtil.validarNoNulo(user, "usuario");
        
        // Validar email usando ValidationUtil
        ValidationUtil.validarEmail(user.getEmail(), "email");
        
        // Validar contraseña
        if (user.getPassword() == null) {
            throw new InvalidUserDataException("Contraseña requerida");
        }
        
        if (!PasswordValidator.isValid(user.getPassword())) {
            throw new InvalidUserDataException(PasswordValidator.getValidationDetails(user.getPassword()));
        }
        
        // Validar otros campos usando ValidationUtil
        ValidationUtil.validarLongitud(user.getNombres(), "nombre", 2, 50);
        ValidationUtil.validarLongitud(user.getApellidos(), "apellido", 2, 50);
        ValidationUtil.validarLongitud(user.getPrograma(), "programa", 2, 100);
        ValidationUtil.validarLongitud(user.getRol(), "rol", 2, 20);
    }
    
    // Método eliminado porque ya no es necesario (la validación está en ValidationUtil)
    // private boolean isValidEmail(String email) {
    //     return email != null && email.endsWith("@unicauca.edu.co");
    // }
}