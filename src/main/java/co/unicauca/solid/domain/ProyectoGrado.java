/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

import static co.unicauca.solid.domain.ProyectoGrado.Modalidad.values;
import co.unicauca.solid.domain.enums.EstadoEnum;
import java.time.LocalDateTime;

/**
 * Entidad principal que maneja los proyectos de grado Controla los estados,
 * intentos y toda la información del proyecto
 *
 * @author crist
 */
public class ProyectoGrado {

    private Integer idProyecto;
    private String titulo;
    private String modalidad;
    private Docente director;
    private Docente codirector; // Puede ser null
    private Estudiante estudiante1;
    private Estudiante estudiante2; // Puede ser null
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

    public ProyectoGrado(String titulo, String modalidad, Docente director,
            Docente codirector, Estudiante estudiante1,
            String objetivoGeneral, String objetivosEspecificos) {
        this(); // Llama al constructor por defecto
        this.titulo = titulo;
        this.modalidad = modalidad;
        this.director = director;
        this.codirector = codirector;
        this.estudiante1 = estudiante1;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public ProyectoGrado(String titulo, String modalidad, Docente director, Docente codirector, Estudiante estudiante1, Estudiante estudiante2, String objetivoGeneral, String objetivosEspecificos) {
        this(); // Llama al constructor por defecto
        this.titulo = titulo;
        this.modalidad = modalidad;
        this.director = director;
        this.codirector = codirector;
        this.estudiante1 = estudiante1;
        this.estudiante2 = estudiante2;
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

    // Getters y Setters para Docente y Estudiantes
    public Docente getDirector() {
        return director;
    }

    public void setDirector(Docente director) {
        this.director = director;
        this.actualizarFechaModificacion();
    }

    public Docente getCodirector() {
        return codirector;
    }

    public void setCodirector(Docente codirector) {
        this.codirector = codirector;
        this.actualizarFechaModificacion();
    }

    public Estudiante getEstudiante1() {
        return estudiante1;
    }

    public void setEstudiante1(Estudiante estudiante1) {
        this.estudiante1 = estudiante1;
    }

    public Estudiante getEstudiante2() {
        return estudiante2;
    }

    public void setEstudiante2(Estudiante estudiante2) {
        if ("PRACTICA_PROFESIONAL".equals(this.modalidad) && estudiante2 != null) {
            throw new IllegalArgumentException("No se permite segundo estudiante en modalidad PRÁCTICA PROFESIONAL");
        }
        this.estudiante2 = estudiante2;
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
                && director != null // ¡Verificar que el objeto no sea null!
                && estudiante1 != null // ¡Verificar que el objeto no sea null!
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
                + ", directorEmail='" + (director != null ? director.getEmail() : "null") + '\''
                + ", estudiante1Email='" + (estudiante1 != null ? estudiante1.getEmail() : "null") + '\''
                + ", estudiante2Email='" + (estudiante2 != null ? estudiante2.getEmail() : "null") + '\''
                + ", estadoActual='" + estadoActual + '\''
                + ", numeroIntento=" + numeroIntento
                + ", rechazadoDefinitivamente=" + rechazadoDefinitivamente
                + '}';
    }
}
