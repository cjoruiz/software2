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
public class FormatoA {
  private Integer idFormato;
    private Integer idProyecto;
    private Integer numeroVersion;
    private byte[] contenido;
    private String nombre;
    private Character tieneCartaEmpresa;
    private LocalDateTime fechaSubida;
    private String estado;
    
    // Constructores
    public FormatoA() {
    }
    public FormatoA(Integer idFormato, Integer idProyecto, Integer numeroVersion, byte[] contenido, String nombre, Character tieneCartaEmpresa, LocalDateTime fechaSubida, String estado) {
        this.idFormato = idFormato;
        this.idProyecto = idProyecto;
        this.numeroVersion = numeroVersion;
        this.contenido = contenido;
        this.nombre = nombre;
        this.tieneCartaEmpresa = tieneCartaEmpresa;
        this.fechaSubida = fechaSubida;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getIdFormato() {
        return idFormato;
    }
    
    public void setIdFormato(Integer idFormato) {
        this.idFormato = idFormato;
    }
    
    public Integer getIdProyecto() {
        return idProyecto;
    }
    
    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }
    
    public Integer getNumeroVersion() {
        return numeroVersion;
    }
    
    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }
    
    public byte[] getContenido() {
        return contenido;
    }
    
    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Character getTieneCartaEmpresa() {
        return tieneCartaEmpresa;
    }
    
    public void setTieneCartaEmpresa(Character tieneCartaEmpresa) {
        this.tieneCartaEmpresa = tieneCartaEmpresa;
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
    
    // Enumeración para los estados
    public enum Estado {
        PENDIENTE("PENDIENTE"),
        APROBADO("APROBADO"),
        RECHAZADO("RECHAZADO");
        
        private final String valor;
        
        Estado(String valor) {
            this.valor = valor;
        }
        
        public String getValor() {
            return valor;
        }
    }
    
    // Enumeración para la carta de empresa
    public enum CartaEmpresa {
        SI('S'),
        NO('N');
        
        private final Character valor;
        
        CartaEmpresa(Character valor) {
            this.valor = valor;
        }
        
        public Character getValor() {
            return valor;
        }
    }
    
}
