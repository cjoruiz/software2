/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

/**
 * Representa a un docente en el sistema.
 * @author crist
 */
public class Docente extends Usuario {
    private String tipoDocente; // "PLANTA" u "OCASIONAL"

    public Docente() {
        super();
    }

    public Docente(String email, String password, String nombres, String apellidos,
                   String celular, String programa, String tipoDocente) {
        super(email, password, nombres, apellidos, celular, programa);
        this.tipoDocente = tipoDocente;
    }

    // Getter y Setter específico de Docente
    public String getTipoDocente() {
        return tipoDocente;
    }

    public void setTipoDocente(String tipoDocente) {
        this.tipoDocente = tipoDocente;
    }

    @Override
    public String getRol() {
        return "DOCENTE";
    }

    @Override
    public String toString() {
        return "Docente{" +
                "email='" + email + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", programa='" + programa + '\'' +
                ", tipoDocente='" + tipoDocente + '\'' +
                '}';
    }
}