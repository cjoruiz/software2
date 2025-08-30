/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.utilities.security;

import java.util.regex.Pattern;

/**
 *
 * @author crist
 */
public class PasswordValidator {

    // Debe tener mínimo 6 caracteres, una mayúscula, un dígito y un caracter especial
    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValid(String password) {
        if (password == null) return false;
        return pattern.matcher(password).matches();
    }
}