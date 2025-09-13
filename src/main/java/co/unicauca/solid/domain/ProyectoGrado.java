/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

import static co.unicauca.solid.domain.ProyectoGrado.Modalidad.values;
import co.unicauca.solid.domain.enums.EstadoEnum;
import java.time.LocalDateTime;

/**
 * Entidad principal que maneja los proyectos de grado
 * Controla los estados, intentos y toda la información del proyecto
 * 
 * @author crist
 */
public class ProyectoGrado {

    private Integer idProyecto;
    private String titulo;
    private String modalidad;
    private String directorEmail;
    private String codirectorEmail;
    private String estudiante1Email;
    private String estudiante2Email;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String estadoActual;
    private Integer numeroIntento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaActualizacion;
    private Character rechazadoDefinitivamente;
    private String observacionesEvaluacion; // Nuevo campo para observaciones del coordinador

    // Constructores
    public ProyectoGrado() {
        this.numeroIntento = 1;
        this.estadoActual = EstadoEnum.EN_PRIMERA_EVALUACION_FORMATO_A.getValor();
        this.rechazadoDefinitivamente = RechazoDefinitivo.NO.getValor();
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    public ProyectoGrado(String titulo, String modalidad, String directorEmail,
            String codirectorEmail, String estudianteEmail,
            String objetivoGeneral, String objetivosEspecificos) {
        this();
        this.titulo = titulo;
        this.modalidad = modalidad;
        this.directorEmail = directorEmail;
        this.codirectorEmail = codirectorEmail;
        this.estudiante1Email = estudianteEmail;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public ProyectoGrado(String titulo, String modalidad, String directorEmail, String codirectorEmail, String estudiante1Email, String estudiante2Email, String objetivoGeneral, String objetivosEspecificos) {
        this();
        this.titulo = titulo;
        this.modalidad = modalidad;
        this.directorEmail = directorEmail;
        this.codirectorEmail = codirectorEmail;
        this.estudiante1Email = estudiante1Email;
        this.estudiante2Email = estudiante2Email;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
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
        this.actualizarFechaModificacion();
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
        this.actualizarFechaModificacion();
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
        this.actualizarFechaModificacion();
    }

    public String getCodirectorEmail() {
        return codirectorEmail;
    }

    public void setCodirectorEmail(String codirectorEmail) {
        this.codirectorEmail = codirectorEmail;
        this.actualizarFechaModificacion();
    }

    public String getEstudiante1Email() {
        return estudiante1Email;
    }

    public void setEstudiante1Email(String estudiante1Email) {
        this.estudiante1Email = estudiante1Email;
    }

    public String getEstudiante2Email() {
        return estudiante2Email;
    }

    public void setEstudiante2Email(String estudiante2Email) {
        if ("PRACTICA_PROFESIONAL".equals(this.modalidad) && estudiante2Email != null) {
            throw new IllegalArgumentException("No se permite segundo estudiante en modalidad PRÁCTICA PROFESIONAL");
        }
        this.estudiante2Email = estudiante2Email;
    }

    public String getObjetivoGeneral() {
        return objetivoGeneral;
    }

    public void setObjetivoGeneral(String objetivoGeneral) {
        this.objetivoGeneral = objetivoGeneral;
        this.actualizarFechaModificacion();
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
        this.actualizarFechaModificacion();
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
        this.actualizarFechaModificacion();
    }

    public Integer getNumeroIntento() {
        return numeroIntento;
    }

    public void setNumeroIntento(Integer numeroIntento) {
        this.numeroIntento = numeroIntento;
        this.actualizarFechaModificacion();
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
        this.actualizarFechaModificacion();
    }

    public String getObservacionesEvaluacion() {
        return observacionesEvaluacion;
    }

    public void setObservacionesEvaluacion(String observacionesEvaluacion) {
        this.observacionesEvaluacion = observacionesEvaluacion;
        this.actualizarFechaModificacion();
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

    // Métodos de negocio - Lógica principal del proyecto
    /**
     * Determina si el proyecto puede tener un nuevo intento
     */
    public boolean puedeReintentar() {
        return numeroIntento < 3 && !estaRechazadoDefinitivamente();
    }

    /**
     * Incrementa el número de intento y actualiza el estado correspondiente
     */
    public void procesarReintentoFormatoA() {
        if (!puedeReintentar()) {
            throw new IllegalStateException("El proyecto no puede reintentar más veces");
        }

        numeroIntento++;

        switch (numeroIntento) {
            case 2:
                this.estadoActual = EstadoEnum.EN_SEGUNDA_EVALUACION_FORMATO_A.getValor();
                break;
            case 3:
                this.estadoActual = EstadoEnum.EN_TERCERA_EVALUACION_FORMATO_A.getValor();
                break;
        }

        this.actualizarFechaModificacion();
    }

    /**
     * Aprueba el formato A y cambia el estado
     */
    public void aprobarFormatoA() {
        this.estadoActual = EstadoEnum.FORMATO_A_APROBADO.getValor();
        this.actualizarFechaModificacion();
    }

    /**
     * Rechaza el formato A. Si es el tercer intento, marca como rechazado
     * definitivo
     */
    public void rechazarFormatoA(String observaciones) {
        this.observacionesEvaluacion = observaciones;

        if (numeroIntento >= 3) {
            marcarRechazoDefinitivo();
        } else {
            this.estadoActual = EstadoEnum.FORMATO_A_RECHAZADO.getValor();
        }

        this.actualizarFechaModificacion();
    }

    /**
     * Marca el proyecto como rechazado definitivamente
     */
    public void marcarRechazoDefinitivo() {
        this.rechazadoDefinitivamente = RechazoDefinitivo.SI.getValor();
        this.estadoActual = EstadoEnum.RECHAZADO_DEFINITIVO.getValor();
        this.actualizarFechaModificacion();
    }

    /**
     * Verifica si el proyecto está rechazado definitivamente
     */
    public boolean estaRechazadoDefinitivamente() {
        return RechazoDefinitivo.SI.getValor().equals(rechazadoDefinitivamente);
    }

    /**
     * Obtiene el estado actual como enum
     */
    public EstadoEnum getEstadoActualEnum() {
        return EstadoEnum.fromValor(estadoActual);
    }

    /**
     * Obtiene la modalidad como enum
     */
    public Modalidad getModalidadEnum() {
        return Modalidad.fromValor(modalidad);
    }

    /**
     * Verifica si el proyecto requiere carta de empresa (Práctica Profesional)
     */
    public boolean requiereCartaEmpresa() {
        return Modalidad.PRACTICA_PROFESIONAL.getValor().equals(modalidad);
    }

    /**
     * Verifica si el proyecto está en evaluación de formato A
     */
    public boolean estaEnEvaluacionFormatoA() {
        return estadoActual.contains("EVALUACION_FORMATO_A");
    }

    /**
     * Obtiene el mensaje de estado para mostrar al usuario
     */
    public String getEstadoDescriptivo() {
        try {
            return EstadoEnum.fromValor(estadoActual).getDescripcion();
        } catch (IllegalArgumentException e) {
            return "Estado desconocido";
        }
    }

    /**
     * Actualiza automáticamente la fecha de última modificación
     */
    private void actualizarFechaModificacion() {
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    /**
     * Valida si el proyecto tiene todos los datos mínimos requeridos
     */
    public boolean esValido() {
        return titulo != null && !titulo.trim().isEmpty()
                && modalidad != null && !modalidad.trim().isEmpty()
                && directorEmail != null && !directorEmail.trim().isEmpty()
                && estudiante1Email != null && !estudiante1Email.trim().isEmpty()
                && objetivoGeneral != null && !objetivoGeneral.trim().isEmpty();
    }

    /**
     * Obtiene información resumida del proyecto
     */
    public String getResumen() {
        return String.format("Proyecto: %s - Estado: %s - Intento: %d/%d",
                titulo != null ? titulo : "Sin título",
                getEstadoDescriptivo(),
                numeroIntento,
                3);
    }

    @Override
    public String toString() {
        return "ProyectoGrado{"
                + "idProyecto=" + idProyecto
                + ", titulo='" + titulo + '\''
                + ", modalidad='" + modalidad + '\''
                + ", directorEmail='" + directorEmail + '\''
                + ", estudiante1Email='" + estudiante1Email + '\''
                + ", estudiante2Email='" + estudiante2Email + '\''
                + ", estadoActual='" + estadoActual + '\''
                + ", numeroIntento=" + numeroIntento
                + ", rechazadoDefinitivamente=" + rechazadoDefinitivamente
                + '}';
    }
}
