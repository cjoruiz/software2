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
    
    /**
     * Registra un nuevo usuario
     * @param user Usuario a registrar
     * @throws UserAlreadyExistsException si el usuario ya existe
     * @throws InvalidUserDataException si los datos son inválidos
     */
    public void registerUser(User user) throws UserAlreadyExistsException, InvalidUserDataException {
        // Validar datos
        validateUser(user);
        
        // Verificar si el usuario ya existe
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        
        // Intentar guardar
        boolean saved = userRepository.save(user);
        if (!saved) {
            throw new RuntimeException("Error al guardar el usuario en la base de datos");
        }
    }
    
    /**
     * Autentica un usuario
     * @param email Email del usuario
     * @param password Contraseña
     * @return Usuario autenticado
     * @throws LoginException si las credenciales son incorrectas
     * @throws InvalidUserDataException si los datos son inválidos
     */
    public User login(String email, String password) throws LoginException, InvalidUserDataException {
        // Validar datos de entrada
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("Email requerido");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidUserDataException("Contraseña requerida");
        }
        
        // Buscar usuario
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new LoginException("Email o contraseña incorrectos");
        }
        
        // Validar contraseña
        if (!((UserRepository)userRepository).validateLogin(email, password)) {
            throw new LoginException("Email o contraseña incorrectos");
        }
        
        return user;
    }
    
    /**
     * Busca un usuario por email
     * @param email Email del usuario
     * @return Usuario encontrado
     * @throws UserNotFoundException si no se encuentra el usuario
     * @throws InvalidUserDataException si el email es inválido
     */
    public User findByEmail(String email) throws UserNotFoundException, InvalidUserDataException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("Email requerido");
        }
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(email);
        }
        
        return user;
    }
    
    /**
     * Obtiene usuarios por rol
     * @param role Rol a buscar
     * @return Lista de usuarios
     * @throws InvalidUserDataException si el rol es inválido
     */
    public List<User> getUsersByRole(String role) throws InvalidUserDataException {
        if (role == null || role.trim().isEmpty()) {
            throw new InvalidUserDataException("Rol requerido");
        }
        return userRepository.findByRole(role);
    }
    
    /**
     * Obtiene todos los usuarios
     * @return Lista de todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.list();
    }
    
    /**
     * Actualiza un usuario existente
     * @param user Usuario con datos actualizados
     * @throws UserNotFoundException si el usuario no existe
     * @throws InvalidUserDataException si los datos son inválidos
     */
    public void updateUser(User user) throws UserNotFoundException, InvalidUserDataException {
        validateUser(user);
        
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new UserNotFoundException(user.getEmail());
        }
        
        boolean updated = userRepository.save(user); // Asumiendo que save hace update si existe
        if (!updated) {
            throw new RuntimeException("Error al actualizar el usuario");
        }
    }
    
    /**
     * Elimina un usuario
     * @param email Email del usuario a eliminar
     * @throws UserNotFoundException si el usuario no existe
     * @throws InvalidUserDataException si el email es inválido
     */
    public void deleteUser(String email) throws UserNotFoundException, InvalidUserDataException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("Email requerido");
        }
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(email);
        }
        
        // Implementar método delete en el repositorio
        // userRepository.delete(email);
    }
    
    /**
     * Valida los datos de un usuario
     * @param user Usuario a validar
     * @throws InvalidUserDataException si los datos son inválidos
     */
    private void validateUser(User user) throws InvalidUserDataException {
        if (user == null) {
            throw new InvalidUserDataException("Usuario no puede ser null");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new InvalidUserDataException("Email requerido");
        }
        
        if (!isValidEmail(user.getEmail())) {
            throw new InvalidUserDataException("Formato de email inválido");
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new InvalidUserDataException("Contraseña debe tener al menos 6 caracteres");
        }
        
        if (user.getNombres() == null || user.getNombres().trim().isEmpty()) {
            throw new InvalidUserDataException("Nombre requerido");
        }
    }
    
    /**
     * Valida formato de email básico
     * @param email Email a validar
     * @return true si es válido
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}