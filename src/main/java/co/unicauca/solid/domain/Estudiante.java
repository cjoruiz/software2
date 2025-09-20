/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

/**
 * Representa a un estudiante en el sistema.
 * @author crist
 */
public class Estudiante extends Usuario {
    
    public Estudiante() {
        super();
    }

    public Estudiante(String email, String password, String nombres, String apellidos,
                      String celular, String programa) {
        super(email, password, nombres, apellidos, celular, programa);
    }

    @Override
    public String getRol() {
        return "ESTUDIANTE";
    }
}
