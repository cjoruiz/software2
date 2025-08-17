/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.service;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.access.UserRepository;
import co.unicauca.solid.domain.User;
import java.util.List;

/**
 *
 * @author crist
 */
public class UserService {
    private IUserRepository userRepository;
    
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public boolean registerUser(User user) {
        if (user == null) return false;
        
        // Verificar si el usuario ya existe
        User existingUser = userRepository.findByEmail(user.getEmail());
        System.out.println(existingUser);
        if (existingUser != null) {
            return false; // Usuario ya existe
        }
        
        return userRepository.save(user);
    }
    
    public User login(String email, String password) {
        if (email == null || password == null) return null;
        
        User user = userRepository.findByEmail(email);
        if (user != null && ((UserRepository)userRepository).validateLogin(email, password)) {
            return user;
        }
        return null;
    }
    
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getAllUsers() {
        return userRepository.list();
    }
}
