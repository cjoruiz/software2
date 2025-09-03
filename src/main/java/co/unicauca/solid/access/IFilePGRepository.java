/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.FilePG;
import java.util.List;

/**
 *
 * @author crist
 */
public interface IFilePGRepository {

    int insertarDocumento(FilePG documento);

    List<FilePG> obtenerDocumentosPorProyectoYTipo(Integer idProyecto, String tipoDocumento);

    List<FilePG> obtenerDocumentosPorProyecto(Integer idProyecto);

    FilePG obtenerDocumentoPorId(int id);

    // Obtener la última versión de un proyecto
    FilePG obtenerUltimaVersionDocumento(Integer idProyecto, String tipoDocumento);

    public List<FilePG> obtenerTodosDocumentos();

    boolean actualizarEstadoDocumento(int idDocumento, String nuevoEstado, String observaciones);

    /**
     * Verifica si existe un documento del tipo especificado para el proyecto
     */
    public boolean existeDocumentoTipo(Integer idProyecto, String tipoDocumento);

    boolean eliminarDocumento(int id);

    boolean guardarEnDisco(int id, String ruta, String nombreArchivo);

    boolean descargar(int id, String ruta, String nombreArchivo);

    int subirFormatoA(Integer idProyecto, byte[] contenido, String nombreArchivo);

    int subirCartaEmpresa(Integer idProyecto, byte[] contenido, String nombreArchivo);

    boolean tieneCartaEmpresa(Integer idProyecto);

    FilePG obtenerFormatoAActual(Integer idProyecto);

    FilePG obtenerCartaEmpresa(Integer idProyecto);

    List<FilePG> obtenerHistorialFormatoA(Integer idProyecto);

    boolean validarDocumentosRequeridos(Integer idProyecto, boolean esPracticaProfesional);

    String obtenerEstadisticasProyecto(Integer idProyecto);
}
