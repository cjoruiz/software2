package co.unicauca.solid.domain;

/**
 *
 * @author crist
 */
public class User {
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
    private String celular;
    private String programa;
    private String rol;
    
    public User() {}
    
    public User(String email, String password, String nombres, String apellidos, 
                String celular, String programa, String rol) {
        this.email = email;
        this.password = password;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.programa = programa;
        this.rol = rol;
    }

    // Getters and Setters
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
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", programa='" + programa + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}