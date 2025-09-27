/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

import java.time.LocalDateTime;

/**
 * Entidad que representa un documento/archivo asociado a un proyecto de grado
 * Se enfoca únicamente en almacenar documentos de diferentes tipos
 * 
 * @author crist
 */
public class FilePG {
    private Integer idDocumento;
    private Integer idProyecto;
    private String tipoDocumento;
    private Integer version;
    private byte[] contenido;
    private String nombreArchivo;
    private String extension;
    private Long tamaño;
    private LocalDateTime fechaSubida;
    private String estado;
    private String observaciones;
    
    // Constructores
    public FilePG() {
        this.fechaSubida = LocalDateTime.now();
        this.estado = EstadoDocumento.PENDIENTE.getValor();
        this.version = 1;
    }
    
    public FilePG(Integer idProyecto, String tipoDocumento, byte[] contenido, 
                  String nombreArchivo) {
        this();
        this.idProyecto = idProyecto;
        this.tipoDocumento = tipoDocumento;
        this.contenido = contenido;
        this.nombreArchivo = nombreArchivo;
        this.extension = extraerExtension(nombreArchivo);
        this.tamaño = contenido != null ? (long) contenido.length : 0L;
    }

    public FilePG(Integer idDocumento, Integer idProyecto, String tipoDocumento, Integer version, byte[] contenido, String nombreArchivo, String extension, Long tamaño, LocalDateTime fechaSubida, String estado, String observaciones) {
        this.idDocumento = idDocumento;
        this.idProyecto = idProyecto;
        this.tipoDocumento = tipoDocumento;
        this.version = version;
        this.contenido = contenido;
        this.nombreArchivo = nombreArchivo;
        this.extension = extension;
        this.tamaño = tamaño;
        this.fechaSubida = fechaSubida;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public FilePG(Integer idProyecto, String tipoDocumento, Integer version, String nombreArchivo) {
    this(); // ✅ Llamar al constructor por defecto para inicializar fechaSubida, estado, etc.
    this.idProyecto = idProyecto;
    this.tipoDocumento = tipoDocumento;
    this.version = version;
    this.nombreArchivo = nombreArchivo;
    this.extension = extraerExtension(nombreArchivo);
}


    
    // Getters y Setters
    public Integer getIdDocumento() {
        return idDocumento;
    }
    
    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }
    
    public Integer getIdProyecto() {
        return idProyecto;
    }
    
    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }
    
    public String getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public byte[] getContenido() {
        return contenido;
    }
    
    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
        this.tamaño = contenido != null ? (long) contenido.length : 0L;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        this.extension = extraerExtension(nombreArchivo);
    }
    
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public Long getTamaño() {
        return tamaño;
    }
    
    public void setTamaño(Long tamaño) {
        this.tamaño = tamaño;
    }
    
    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }
    
    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    // Enumeraciones
    public enum TipoDocumento {
        FORMATO_A("FORMATO_A", "Formato A", "Documento inicial del proyecto"),
        CARTA_EMPRESA("CARTA_EMPRESA", "Carta de Empresa", "Carta de aceptación de la empresa"),
        FORMATO_B("FORMATO_B", "Formato B", "Documento de seguimiento del proyecto"),
        MONOGRAFIA("MONOGRAFIA", "Monografía", "Documento final del proyecto"),
        ANEXOS("ANEXOS", "Anexos", "Documentos anexos al proyecto"),
        PRESENTACION("PRESENTACION", "Presentación", "Presentación para sustentación");
        
        private final String valor;
        private final String nombre;
        private final String descripcion;
        
        TipoDocumento(String valor, String nombre, String descripcion) {
            this.valor = valor;
            this.nombre = nombre;
            this.descripcion = descripcion;
        }
        
        public String getValor() {
            return valor;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public static TipoDocumento fromValor(String valor) {
            for (TipoDocumento tipo : values()) {
                if (tipo.valor.equals(valor)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Tipo de documento no válido: " + valor);
        }
    }
    
    public enum EstadoDocumento {
        PENDIENTE("PENDIENTE", "Pendiente de revisión"),
        EN_REVISION("EN_REVISION", "En revisión"),
        APROBADO("APROBADO", "Aprobado"),
        RECHAZADO("RECHAZADO", "Rechazado"),
        OBSOLETO("OBSOLETO", "Versión obsoleta");
        
        private final String valor;
        private final String descripcion;
        
        EstadoDocumento(String valor, String descripcion) {
            this.valor = valor;
            this.descripcion = descripcion;
        }
        
        public String getValor() {
            return valor;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public static EstadoDocumento fromValor(String valor) {
            for (EstadoDocumento estado : values()) {
                if (estado.valor.equals(valor)) {
                    return estado;
                }
            }
            throw new IllegalArgumentException("Estado de documento no válido: " + valor);
        }
    }
    
    // Métodos de negocio para documentos
    
    /**
     * Verifica si el documento es de tipo Formato A
     */
    public boolean esFormatoA() {
        return TipoDocumento.FORMATO_A.getValor().equals(tipoDocumento);
    }
    
    /**
     * Verifica si el documento es una carta de empresa
     */
    public boolean esCartaEmpresa() {
        return TipoDocumento.CARTA_EMPRESA.getValor().equals(tipoDocumento);
    }
    
    /**
     * Verifica si el documento es PDF
     */
    public boolean esPDF() {
        return extension != null && extension.toLowerCase().equals("pdf");
    }
    
    /**
     * Obtiene el tipo de documento como enum
     */
    public TipoDocumento getTipoDocumentoEnum() {
        return TipoDocumento.fromValor(tipoDocumento);
    }
    
    /**
     * Obtiene el estado del documento como enum
     */
    public EstadoDocumento getEstadoDocumentoEnum() {
        return EstadoDocumento.fromValor(estado);
    }
    
    /**
     * Marca el documento como aprobado
     */
    public void aprobar() {
        this.estado = EstadoDocumento.APROBADO.getValor();
    }
    
    /**
     * Marca el documento como rechazado
     */
    public void rechazar(String observaciones) {
        this.estado = EstadoDocumento.RECHAZADO.getValor();
        this.observaciones = observaciones;
    }
    
    /**
     * Marca el documento como obsoleto (cuando hay una nueva versión)
     */
    public void marcarObsoleto() {
        this.estado = EstadoDocumento.OBSOLETO.getValor();
    }
    
    /**
     * Valida si el documento es válido
     */
    public boolean esValido() {
        return idProyecto != null && 
               tipoDocumento != null && !tipoDocumento.trim().isEmpty() &&
               contenido != null && contenido.length > 0 &&
               nombreArchivo != null && !nombreArchivo.trim().isEmpty() &&
               esPDF();
    }
    
    /**
     * Obtiene el tamaño del archivo en formato legible
     */
    public String getTamañoFormateado() {
        if (tamaño == null || tamaño == 0) {
            return "0 KB";
        }
        
        if (tamaño < 1024) {
            return tamaño + " bytes";
        } else if (tamaño < 1024 * 1024) {
            return String.format("%.1f KB", tamaño / 1024.0);
        } else {
            return String.format("%.1f MB", tamaño / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Genera un nombre de archivo único basado en tipo y versión
     */
    public String generarNombreUnico() {
        String tipoNombre = getTipoDocumentoEnum().getNombre().replace(" ", "_");
        return String.format("%s_v%d_Proyecto_%d.pdf", 
                           tipoNombre, version, idProyecto);
    }
    
    /**
     * Extrae la extensión del nombre del archivo
     */
    private String extraerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
    }
    
    /**
     * Obtiene información resumida del documento
     */
    public String getResumen() {
        return String.format("%s - %s (v%d) - %s - %s", 
                           getTipoDocumentoEnum().getNombre(),
                           nombreArchivo,
                           version,
                           getTamañoFormateado(),
                           getEstadoDocumentoEnum().getDescripcion());
    }
    
    @Override
    public String toString() {
        return "FilePG{" +
                "idDocumento=" + idDocumento +
                ", idProyecto=" + idProyecto +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", version=" + version +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", tamaño=" + tamaño +
                ", estado='" + estado + '\'' +
                '}';
    }
}