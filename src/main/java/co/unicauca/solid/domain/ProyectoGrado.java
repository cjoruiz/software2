/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

import java.time.LocalDateTime;

/**
 *
 * @author crist
 */
public class ProyectoGrado {
    private Integer idProyecto;
    private String titulo;
    private String modalidad;
    private String directorEmail;
    private String codirectorEmail;
    private String estudianteEmail;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String estadoActual;
    private Integer numeroIntento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaActualizacion;
    private Character rechazadoDefinitivamente;
    
    // Constructores
    public ProyectoGrado() {
    }
    
    public ProyectoGrado(Integer idProyecto, String titulo, String modalidad, 
                        String directorEmail, String codirectorEmail, String estudianteEmail,
                        String objetivoGeneral, String objetivosEspecificos, String estadoActual,
                        Integer numeroIntento, LocalDateTime fechaCreacion, 
                        LocalDateTime fechaUltimaActualizacion, Character rechazadoDefinitivamente) {
        this.idProyecto = idProyecto;
        this.titulo = titulo;
        this.modalidad = modalidad;
        this.directorEmail = directorEmail;
        this.codirectorEmail = codirectorEmail;
        this.estudianteEmail = estudianteEmail;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
        this.estadoActual = estadoActual;
        this.numeroIntento = numeroIntento;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
        this.rechazadoDefinitivamente = rechazadoDefinitivamente;
    }
    
    // Getters y Setters
    public Integer getIdProyecto() {
        return idProyecto;
    }
    
    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getModalidad() {
        return modalidad;
    }
    
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }
    
    public String getDirectorEmail() {
        return directorEmail;
    }
    
    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
    }
    
    public String getCodirectorEmail() {
        return codirectorEmail;
    }
    
    public void setCodirectorEmail(String codirectorEmail) {
        this.codirectorEmail = codirectorEmail;
    }
    
    public String getEstudianteEmail() {
        return estudianteEmail;
    }
    
    public void setEstudianteEmail(String estudianteEmail) {
        this.estudianteEmail = estudianteEmail;
    }
    
    public String getObjetivoGeneral() {
        return objetivoGeneral;
    }
    
    public void setObjetivoGeneral(String objetivoGeneral) {
        this.objetivoGeneral = objetivoGeneral;
    }
    
    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }
    
    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
    }
    
    public String getEstadoActual() {
        return estadoActual;
    }
    
    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }
    
    public Integer getNumeroIntento() {
        return numeroIntento;
    }
    
    public void setNumeroIntento(Integer numeroIntento) {
        this.numeroIntento = numeroIntento;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }
    
    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }
    
    public Character getRechazadoDefinitivamente() {
        return rechazadoDefinitivamente;
    }
    
    public void setRechazadoDefinitivamente(Character rechazadoDefinitivamente) {
        this.rechazadoDefinitivamente = rechazadoDefinitivamente;
    }
    
    // Enumeraciones
    public enum Modalidad {
        INVESTIGACION("INVESTIGACION", "Investigación"),
        PRACTICA_PROFESIONAL("PRACTICA_PROFESIONAL", "Práctica Profesional");
        
        private final String valor;
        private final String descripcion;
        
        Modalidad(String valor, String descripcion) {
            this.valor = valor;
            this.descripcion = descripcion;
        }
        
        public String getValor() {
            return valor;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public static Modalidad fromValor(String valor) {
            for (Modalidad mod : values()) {
                if (mod.valor.equals(valor)) {
                    return mod;
                }
            }
            throw new IllegalArgumentException("Modalidad no válida: " + valor);
        }
    }
    
    public enum Estado {
        EN_PRIMERA_EVALUACION_FORMATO_A("EN_PRIMERA_EVALUACION_FORMATO_A", "En primera evaluación formato A"),
        FORMATO_A_RECHAZADO("FORMATO_A_RECHAZADO", "Formato A rechazado"),
        FORMATO_A_APROBADO("FORMATO_A_APROBADO", "Formato A aprobado"),
        EN_EVALUACION_FORMATO_B("EN_EVALUACION_FORMATO_B", "En evaluación formato B"),
        FORMATO_B_RECHAZADO("FORMATO_B_RECHAZADO", "Formato B rechazado"),
        FORMATO_B_APROBADO("FORMATO_B_APROBADO", "Formato B aprobado"),
        EN_DESARROLLO("EN_DESARROLLO", "En desarrollo"),
        EN_EVALUACION_FINAL("EN_EVALUACION_FINAL", "En evaluación final"),
        APROBADO("APROBADO", "Aprobado"),
        RECHAZADO_DEFINITIVO("RECHAZADO_DEFINITIVO", "Rechazado definitivamente");
        
        private final String valor;
        private final String descripcion;
        
        Estado(String valor, String descripcion) {
            this.valor = valor;
            this.descripcion = descripcion;
        }
        
        public String getValor() {
            return valor;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public static Estado fromValor(String valor) {
            for (Estado estado : values()) {
                if (estado.valor.equals(valor)) {
                    return estado;
                }
            }
            throw new IllegalArgumentException("Estado no válido: " + valor);
        }
    }
    
    public enum RechazoDefinitivo {
        SI('S'),
        NO('N');
        
        private final Character valor;
        
        RechazoDefinitivo(Character valor) {
            this.valor = valor;
        }
        
        public Character getValor() {
            return valor;
        }
    }
    
    // Métodos utilitarios
    public boolean puedeReintentar() {
        return numeroIntento < 3 && rechazadoDefinitivamente != RechazoDefinitivo.SI.getValor();
    }
    
    public void incrementarIntento() {
        if (puedeReintentar()) {
            numeroIntento++;
        }
    }
    
    public void marcarRechazoDefinitivo() {
        this.rechazadoDefinitivamente = RechazoDefinitivo.SI.getValor();
    }
    
    public boolean estaRechazadoDefinitivamente() {
        return RechazoDefinitivo.SI.getValor().equals(rechazadoDefinitivamente);
    }
    
    @Override
    public String toString() {
        return "ProyectoGrado{" +
                "idProyecto=" + idProyecto +
                ", titulo='" + titulo + '\'' +
                ", modalidad='" + modalidad + '\'' +
                ", directorEmail='" + directorEmail + '\'' +
                ", codirectorEmail='" + codirectorEmail + '\'' +
                ", estudianteEmail='" + estudianteEmail + '\'' +
                ", estadoActual='" + estadoActual + '\'' +
                ", numeroIntento=" + numeroIntento +
                ", rechazadoDefinitivamente=" + rechazadoDefinitivamente +
                '}';
    }
}
