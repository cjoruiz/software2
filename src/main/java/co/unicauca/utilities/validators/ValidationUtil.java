/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.utilities.validators;

import co.unicauca.utilities.exeption.InvalidUserDataException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author crist
 */
public class ValidationUtil {

    public static void validarEmail(String email, String campo) throws InvalidUserDataException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("El " + campo + " es obligatorio");
        }

        if (!email.endsWith("@unicauca.edu.co")) {
            throw new InvalidUserDataException("El " + campo + " debe ser del dominio @unicauca.edu.co");
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new InvalidUserDataException("Formato de " + campo + " inválido");
        }
    }

    public static void validarLongitud(String valor, String campo, int min, int max)
            throws InvalidUserDataException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new InvalidUserDataException("El " + campo + " es obligatorio");
        }
        if (valor.length() < min) {
            throw new InvalidUserDataException("El " + campo + " debe tener al menos " + min + " caracteres");
        }
        if (valor.length() > max) {
            throw new InvalidUserDataException("El " + campo + " no puede tener más de " + max + " caracteres");
        }
    }

    public static void validarNoNulo(Object objeto, String campo) throws InvalidUserDataException {
        if (objeto == null) {
            throw new InvalidUserDataException("El " + campo + " no puede ser nulo");
        }
    }

    public static void validarNoVacio(String valor, String campo) throws InvalidUserDataException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new InvalidUserDataException("El " + campo + " es obligatorio");
        }
    }
}
