/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.domain;

/**
 *
 * @author crist
 */
import java.time.LocalDateTime;

/**
 * Entidad que representa un mensaje interno enviado por un estudiante a uno o varios docentes.
 * Sirve para proponer ideas de proyecto antes de la creación formal del ProyectoGrado.
 */
public class MensajeInterno {
    private Integer idMensaje;
    private String remitenteEmail; // Email del estudiante que envía
    private String destinatariosEmail; // Emails separados por coma (e.g., "dir@unicauca.edu.co,codir@unicauca.edu.co")
    private String asunto;
    private String cuerpo;
    private byte[] documentoAdjunto; // Opcional, para enviar un borrador del Formato A
    private String nombreArchivo; // Nombre del archivo adjunto
    private LocalDateTime fechaEnvio;
    private String estado; // "ENVIADO", "LEIDO", "RESPONDIDO"

    // Constructores
    public MensajeInterno() {
        this.fechaEnvio = LocalDateTime.now();
        this.estado = EstadoMensaje.ENVIADO.getValor();
    }

    public MensajeInterno(String remitenteEmail, String destinatariosEmail, String asunto, String cuerpo) {
        this();
        this.remitenteEmail = remitenteEmail;
        this.destinatariosEmail = destinatariosEmail;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
    }

    // Getters y Setters
    public Integer getIdMensaje() { return idMensaje; }
    public void setIdMensaje(Integer idMensaje) { this.idMensaje = idMensaje; }

    public String getRemitenteEmail() { return remitenteEmail; }
    public void setRemitenteEmail(String remitenteEmail) { this.remitenteEmail = remitenteEmail; }

    public String getDestinatariosEmail() { return destinatariosEmail; }
    public void setDestinatariosEmail(String destinatariosEmail) { this.destinatariosEmail = destinatariosEmail; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }

    public byte[] getDocumentoAdjunto() { return documentoAdjunto; }
    public void setDocumentoAdjunto(byte[] documentoAdjunto) { this.documentoAdjunto = documentoAdjunto; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Enumeración para el estado del mensaje
    public enum EstadoMensaje {
        ENVIADO("ENVIADO", "Mensaje enviado"),
        LEIDO("LEIDO", "Mensaje leído"),
        RESPONDIDO("RESPONDIDO", "Mensaje respondido");

        private final String valor;
        private final String descripcion;

        EstadoMensaje(String valor, String descripcion) {
            this.valor = valor;
            this.descripcion = descripcion;
        }

        public String getValor() { return valor; }
        public String getDescripcion() { return descripcion; }

        public static EstadoMensaje fromValor(String valor) {
            for (EstadoMensaje estado : values()) {
                if (estado.valor.equals(valor)) {
                    return estado;
                }
            }
            throw new IllegalArgumentException("Estado de mensaje no válido: " + valor);
        }
    }

    @Override
    public String toString() {
        return "MensajeInterno{" +
                "idMensaje=" + idMensaje +
                ", remitenteEmail='" + remitenteEmail + '\'' +
                ", destinatariosEmail='" + destinatariosEmail + '\'' +
                ", asunto='" + asunto + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                ", estado='" + estado + '\'' +
                '}';
    }
}
