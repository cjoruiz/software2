/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.utilities.exeption;

/**
 *
 * @author crist
 */
public class LoginException extends Exception {
    public LoginException(String message) {
        super("Error de autenticación: " + message);
    }
}
