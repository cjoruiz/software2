/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import java.util.List;

/**
 * Interfaz para el repositorio de ProyectoGrado.
 * Define el contrato para la persistencia de proyectos de grado.
 * @author crist
 */
public interface IProyectoGradoRepository {
    
    // Insertar proyecto de grado
    int insertarProyecto(ProyectoGrado proyecto);
    
    // Obtener proyecto por ID
    ProyectoGrado obtenerProyectoPorId(int idProyecto) throws InvalidUserDataException;
    
    // Obtener proyectos por estudiante
    List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail) throws InvalidUserDataException;
    
    // Obtener proyectos por director
    List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail) throws InvalidUserDataException;
    
    // Obtener todos los proyectos
    List<ProyectoGrado> obtenerTodosProyectos() throws InvalidUserDataException;
    
    // Actualizar proyecto
    boolean actualizarProyecto(ProyectoGrado proyecto) throws InvalidUserDataException;
    
    // Actualizar estado del proyecto
    boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado) throws InvalidUserDataException;
    
    // Incrementar número de intento
    boolean incrementarIntento(int idProyecto) throws InvalidUserDataException;
    
    // Marcar como rechazado definitivamente
    boolean marcarRechazoDefinitivo(int idProyecto) throws InvalidUserDataException;
    
    // Eliminar proyecto
    boolean eliminarProyecto(int idProyecto) throws InvalidUserDataException;
    
    boolean evaluarFormatoA(int idProyecto, boolean aprobado, String observaciones) throws InvalidUserDataException;
    
    List<ProyectoGrado> obtenerProyectosPendientesEvaluacion() throws InvalidUserDataException;
    
    boolean procesarReintentoFormatoA(int idProyecto) throws InvalidUserDataException;
}