/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.utilities.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 *
 * @author crist
 */
public class PasswordValidator {

    public static boolean isValid(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        if (password.length() < 6) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasUpper = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
            if (Character.isUpperCase(c)) hasUpper = true;
        }

        return hasDigit && hasSpecial && hasUpper;
    }
    
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public static String getValidationDetails(String password) {
        if (password == null) return "La contraseña no puede ser nula";
        
        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasUpper = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
            if (Character.isUpperCase(c)) hasUpper = true;
        }
        
        StringBuilder details = new StringBuilder();
        if (!hasUpper) details.append("• La contraseña debe contener al menos una mayúscula\n");
        if (!hasDigit) details.append("• La contraseña debe contener al menos un dígito\n");
        if (!hasSpecial) details.append("• La contraseña d contener al menos un carácter especial\n");
        
        return details.length() > 0 ? details.toString() : "Contraseña válida";
    }
}