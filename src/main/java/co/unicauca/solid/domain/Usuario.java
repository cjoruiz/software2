/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

/**
 *
 * @author crist
 */
public abstract class Usuario {
    protected String email;
    protected String password;
    protected String nombres;
    protected String apellidos;
    protected String celular;
    protected String programa;

    // Constructores
    public Usuario() {
    }

    public Usuario(String email, String password, String nombres, String apellidos,
                   String celular, String programa) {
        this.email = email;
        this.password = password;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.programa = programa;
    }

    // Getters y Setters (comunes a todos)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }

    /**
     * Método abstracto para obtener el rol del usuario.
     * Debe ser implementado por las subclases.
     */
    public abstract String getRol();

    @Override
    public String toString() {
        return "Usuario{" +
                "email='" + email + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", programa='" + programa + '\'' +
                ", rol='" + getRol() + '\'' +
                '}';
    }
}
