/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

/**
 *
 * @author crist
 */
public class Programa {
    private String idPrograma;
    private String nombreCompleto;
    private String facultad;
    
    // Constructores
    public Programa() {
    }
    
    public Programa(String idPrograma, String nombreCompleto, String facultad) {
        this.idPrograma = idPrograma;
        this.nombreCompleto = nombreCompleto;
        this.facultad = facultad;
    }
    
    // Getters y Setters
    public String getIdPrograma() {
        return idPrograma;
    }
    
    public void setIdPrograma(String idPrograma) {
        this.idPrograma = idPrograma;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getFacultad() {
        return facultad;
    }
    
    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }
    
    // Enumeración para los programas
    public enum Programas {
        INGENIERIA_SISTEMAS("INGENIERIA_SISTEMAS", "Ingeniería de Sistemas"),
        INGENIERIA_ELECTRONICA("INGENIERIA_ELECTRONICA", "Ingeniería Electrónica"),
        AUTOMATICA_INDUSTRIAL("AUTOMATICA_INDUSTRIAL", "Automática Industrial"),
        TECNOLOGIA_TELEMATICA("TECNOLOGIA_TELEMATICA", "Tecnología en Telemática");
        
        private final String id;
        private final String nombreCompleto;
        
        Programas(String id, String nombreCompleto) {
            this.id = id;
            this.nombreCompleto = nombreCompleto;
        }
        
        public String getId() {
            return id;
        }
        
        public String getNombreCompleto() {
            return nombreCompleto;
        }
        
        public static Programas fromId(String id) {
            for (Programas programa : values()) {
                if (programa.id.equals(id)) {
                    return programa;
                }
            }
            throw new IllegalArgumentException("Programa no válido: " + id);
        }
    }
    
    @Override
    public String toString() {
        return "Programa{" +
                "idPrograma='" + idPrograma + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", facultad='" + facultad + '\'' +
                '}';
    }
}
