/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.FormatoA;
import java.util.List;

/**
 *
 * @author crist
 */
public interface IFormatoARepository {
    int insertarDocumento(FormatoA documento);
    List<FormatoA> obtenerFormatosAPorProyecto(Integer idProyecto);
    // Obtener formato A por ID
    FormatoA obtenerDocumentoPorId(int id);
    // Obtener la última versión de un proyecto
    FormatoA obtenerUltimaVersionPorProyecto(Integer idProyecto);
    // Obtener todos los formatos A
    public List<FormatoA> obtenerTodosDocumentos();
    // Actualizar estado de un formato A
    boolean actualizarEstadoFormatoA(int idFormato, String nuevoEstado);
    // Verificar si un proyecto puede subir nueva versión
    boolean puedeSubirNuevaVersion(Integer idProyecto);
    // Obtener el próximo número de versión para un proyecto
    int obtenerProximaVersion(Integer idProyecto);
    // Eliminar formato A
    boolean eliminarDocumento(int id);

    // Guardar formato A en disco
    boolean guardarEnDisco(int id, String ruta, String nombreArchivo);
    // Descargar formato A
    boolean descargar(int id, String ruta, String nombreArchivo);
}
