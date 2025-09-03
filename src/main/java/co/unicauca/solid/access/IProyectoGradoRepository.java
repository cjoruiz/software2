/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.ProyectoGrado;
import java.util.List;

/**
 *
 * @author crist
 */
public interface IProyectoGradoRepository {
    // Insertar proyecto de grado
    int insertarProyecto(ProyectoGrado proyecto);
    // Obtener proyecto por ID
    ProyectoGrado obtenerProyectoPorId(int idProyecto);
    // Obtener proyectos por estudiante
    List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail);
    // Obtener proyectos por director
    List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail);
    // Obtener todos los proyectos
    List<ProyectoGrado> obtenerTodosProyectos();
    // Actualizar proyecto
    boolean actualizarProyecto(ProyectoGrado proyecto);
    // Actualizar estado del proyecto
    boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado);
    // Incrementar número de intento
    boolean incrementarIntento(int idProyecto);
    // Marcar como rechazado definitivamente
    boolean marcarRechazoDefinitivo(int idProyecto);
    // Eliminar proyecto
    boolean eliminarProyecto(int idProyecto);
}
