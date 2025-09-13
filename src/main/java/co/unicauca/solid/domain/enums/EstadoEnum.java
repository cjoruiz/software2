/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package co.unicauca.solid.domain.enums;

/**
 *
 * @author crist
 */
public enum EstadoEnum {
    EN_PRIMERA_EVALUACION_FORMATO_A("EN_PRIMERA_EVALUACION_FORMATO_A", "En primera evaluación formato A"),
    EN_SEGUNDA_EVALUACION_FORMATO_A("EN_SEGUNDA_EVALUACION_FORMATO_A", "En segunda evaluación formato A"),
    EN_TERCERA_EVALUACION_FORMATO_A("EN_TERCERA_EVALUACION_FORMATO_A", "En tercera evaluación formato A"),
    FORMATO_A_APROBADO("FORMATO_A_APROBADO", "Formato A aprobado"),
    FORMATO_A_RECHAZADO("FORMATO_A_RECHAZADO", "Formato A rechazado"),
    EN_EVALUACION_FORMATO_B("EN_EVALUACION_FORMATO_B", "En evaluación formato B"),
    FORMATO_B_RECHAZADO("FORMATO_B_RECHAZADO", "Formato B rechazado"),
    FORMATO_B_APROBADO("FORMATO_B_APROBADO", "Formato B aprobado"),
    EN_DESARROLLO("EN_DESARROLLO", "En desarrollo"),
    EN_EVALUACION_FINAL("EN_EVALUACION_FINAL", "En evaluación final"),
    APROBADO("APROBADO", "Aprobado"),
    RECHAZADO_DEFINITIVO("RECHAZADO_DEFINITIVO", "Rechazado definitivamente");

    private final String valor;
    private final String descripcion;

    EstadoEnum(String valor, String descripcion) {
        this.valor = valor;
        this.descripcion = descripcion;
    }

    public String getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoEnum fromValor(String valor) {
        for (EstadoEnum estado : values()) {
            if (estado.valor.equals(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + valor);
    }
}
