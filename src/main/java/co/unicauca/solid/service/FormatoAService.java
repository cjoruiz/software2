/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.service;

import co.unicauca.solid.access.IFormatoARepository;
import co.unicauca.solid.domain.FormatoA;
import java.util.List;

public class FormatoAService {
    private final IFormatoARepository formatoARepository;
    
    public FormatoAService(IFormatoARepository formatoARepository) {
        this.formatoARepository = formatoARepository;
    }
    
    // Subir nuevo formato A con validación de versiones
    public int subirFormatoA(FormatoA formatoA) {
        // Verificar si puede subir nueva versión
        if (!formatoARepository.puedeSubirNuevaVersion(formatoA.getIdProyecto())) {
            throw new IllegalStateException("No se pueden subir más de 3 versiones del Formato A");
        }
        
        // Obtener la próxima versión
        int proximaVersion = formatoARepository.obtenerProximaVersion(formatoA.getIdProyecto());
        formatoA.setNumeroVersion(proximaVersion);
        
        return formatoARepository.insertarDocumento(formatoA);
    }
    
    // Obtener formatos A de un proyecto
    public List<FormatoA> obtenerFormatosAPorProyecto(Integer idProyecto) {
        return formatoARepository.obtenerFormatosAPorProyecto(idProyecto);
    }
    
    // Obtener la última versión de un proyecto
    public FormatoA obtenerUltimaVersion(Integer idProyecto) {
        return formatoARepository.obtenerUltimaVersionPorProyecto(idProyecto);
    }
    
    // Aprobar formato A
    public boolean aprobarFormatoA(int idFormato) {
        return formatoARepository.actualizarEstadoFormatoA(idFormato, "APROBADO");
    }
    
    // Rechazar formato A
    public boolean rechazarFormatoA(int idFormato) {
        return formatoARepository.actualizarEstadoFormatoA(idFormato, "RECHAZADO");
    }
    
    // Verificar si puede subir nueva versión
    public boolean puedeSubirNuevaVersion(Integer idProyecto) {
        return formatoARepository.puedeSubirNuevaVersion(idProyecto);
    }
    
    // Obtener todos los formatos A
    public List<FormatoA> obtenerTodosFormatosA() {
        return formatoARepository.obtenerTodosDocumentos();
    }
    
    // Descargar formato A
    public boolean descargarFormatoA(int idFormato, String ruta, String nombreArchivo) {
        return formatoARepository.descargar(idFormato, ruta, nombreArchivo);
    }
    
    // Eliminar formato A
    public boolean eliminarFormatoA(int idFormato) {
        return formatoARepository.eliminarDocumento(idFormato);
    }
}