package co.unicauca.solid.service;

import co.unicauca.solid.access.IFilePGRepository;
import co.unicauca.solid.domain.FilePG;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import co.unicauca.utilities.validators.ValidationUtil;
import java.util.List;
import java.util.Arrays;

/**
 * Servicio para gestión de documentos de proyectos de grado
 * Maneja todos los tipos de documentos: Formato A, Carta de Empresa, Formato B, etc.
 */
public class FilePGService {
    
    private final IFilePGRepository filePGRepository;
    private final ProyectoGradoService proyectoGradoService;
    
    // Extensiones permitidas por tipo de documento
    private static final String[] EXTENSIONES_PDF = {".pdf"};
    private static final String[] EXTENSIONES_DOCUMENTOS = {".pdf", ".doc", ".docx"};
    private static final String[] EXTENSIONES_PRESENTACION = {".pdf", ".ppt", ".pptx"};
    
    // Tamaños máximos en bytes (20MB)
    private static final long TAMAÑO_MAXIMO = 20 * 1024 * 1024;

    public FilePGService(IFilePGRepository filePGRepository, ProyectoGradoService proyectoGradoService) {
        this.filePGRepository = filePGRepository;
        this.proyectoGradoService = proyectoGradoService;
    }

    // ========== MÉTODOS GENERALES ==========
    
    /**
     * Inserta cualquier tipo de documento con validaciones
     */
    public int insertarDocumento(FilePG documento) throws InvalidUserDataException, UserNotFoundException {
        validarDocumento(documento);
        return filePGRepository.insertarDocumento(documento);
    }

    /**
     * Obtiene un documento por su ID
     */
    public FilePG obtenerDocumentoPorId(int id) throws UserNotFoundException {
        FilePG documento = filePGRepository.obtenerDocumentoPorId(id);
        if (documento == null) {
            throw new UserNotFoundException("Documento con ID " + id + " no encontrado");
        }
        return documento;
    }

    /**
     * Obtiene todos los documentos de un proyecto
     */
    public List<FilePG> obtenerDocumentosPorProyecto(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        List<FilePG> documentos = filePGRepository.obtenerDocumentosPorProyecto(idProyecto);
        if (documentos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron documentos para el proyecto " + idProyecto);
        }
        return documentos;
    }

    /**
     * Obtiene documentos por proyecto y tipo específico
     */
    public List<FilePG> obtenerDocumentosPorProyectoYTipo(Integer idProyecto, String tipoDocumento) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        validarTipoDocumento(tipoDocumento);
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        return filePGRepository.obtenerDocumentosPorProyectoYTipo(idProyecto, tipoDocumento);
    }

    /**
     * Actualiza el estado de un documento
     */
    public boolean actualizarEstadoDocumento(int idDocumento, String nuevoEstado, String observaciones) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idDocumento, "ID del documento");
        validarEstadoDocumento(nuevoEstado);
        
        // Verificar que el documento existe
        obtenerDocumentoPorId(idDocumento);
        
        return filePGRepository.actualizarEstadoDocumento(idDocumento, nuevoEstado, observaciones);
    }

    /**
     * Elimina un documento
     */
    public boolean eliminarDocumento(int id) throws UserNotFoundException {
        // Verificar que el documento existe
        obtenerDocumentoPorId(id);
        
        return filePGRepository.eliminarDocumento(id);
    }

    /**
     * Descarga un documento a disco
     */
    public boolean descargarDocumento(int id, String ruta, String nombreArchivo) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarNoVacio(ruta, "ruta de descarga");
        
        // Verificar que el documento existe
        obtenerDocumentoPorId(id);
        
        return filePGRepository.guardarEnDisco(id, ruta, nombreArchivo);
    }

    // ========== MÉTODOS ESPECÍFICOS PARA FORMATO A ==========
    
    /**
     * Sube un nuevo Formato A con validaciones específicas
     */
    public int subirFormatoA(Integer idProyecto, byte[] contenido, String nombreArchivo) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        ValidationUtil.validarNoNulo(contenido, "contenido del archivo");
        ValidationUtil.validarNoVacio(nombreArchivo, "nombre del archivo");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        // Validar extensión para Formato A (solo PDF)
        if (!validarExtension(nombreArchivo, EXTENSIONES_PDF)) {
            throw new InvalidUserDataException("El Formato A solo puede ser un archivo PDF");
        }
        
        // Validar tamaño
        if (contenido.length > TAMAÑO_MAXIMO) {
            throw new InvalidUserDataException("El archivo excede el tamaño máximo permitido (20MB)");
        }
        
        return filePGRepository.subirFormatoA(idProyecto, contenido, nombreArchivo);
    }

    /**
     * Obtiene el Formato A actual de un proyecto
     */
    public FilePG obtenerFormatoAActual(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        FilePG formatoA = filePGRepository.obtenerFormatoAActual(idProyecto);
        if (formatoA == null) {
            throw new UserNotFoundException("No se encontró Formato A para el proyecto " + idProyecto);
        }
        return formatoA;
    }

    /**
     * Obtiene el historial completo de Formatos A de un proyecto
     */
    public List<FilePG> obtenerHistorialFormatoA(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        List<FilePG> historial = filePGRepository.obtenerHistorialFormatoA(idProyecto);
        if (historial.isEmpty()) {
            throw new UserNotFoundException("No se encontró historial de Formato A para el proyecto " + idProyecto);
        }
        return historial;
    }

    // ========== MÉTODOS ESPECÍFICOS PARA CARTA DE EMPRESA ==========
    
    /**
     * Sube carta de empresa (requerida para práctica profesional)
     */
    public int subirCartaEmpresa(Integer idProyecto, byte[] contenido, String nombreArchivo) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        ValidationUtil.validarNoNulo(contenido, "contenido del archivo");
        ValidationUtil.validarNoVacio(nombreArchivo, "nombre del archivo");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        // Validar extensión para carta de empresa
        if (!validarExtension(nombreArchivo, EXTENSIONES_DOCUMENTOS)) {
            throw new InvalidUserDataException("La carta de empresa debe ser PDF, DOC o DOCX");
        }
        
        // Validar tamaño
        if (contenido.length > TAMAÑO_MAXIMO) {
            throw new InvalidUserDataException("El archivo excede el tamaño máximo permitido (20MB)");
        }
        
        return filePGRepository.subirCartaEmpresa(idProyecto, contenido, nombreArchivo);
    }

    /**
     * Obtiene la carta de empresa de un proyecto
     */
    public FilePG obtenerCartaEmpresa(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        FilePG carta = filePGRepository.obtenerCartaEmpresa(idProyecto);
        if (carta == null) {
            throw new UserNotFoundException("No se encontró carta de empresa para el proyecto " + idProyecto);
        }
        return carta;
    }

    /**
     * Verifica si un proyecto tiene carta de empresa
     */
    public boolean tieneCartaEmpresa(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        return filePGRepository.tieneCartaEmpresa(idProyecto);
    }

    // ========== MÉTODOS DE VALIDACIÓN DE DOCUMENTOS ==========
    
    /**
     * Valida que un proyecto tenga todos los documentos requeridos
     */
    public boolean validarDocumentosRequeridos(Integer idProyecto, String modalidad) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        ValidationUtil.validarNoVacio(modalidad, "modalidad");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        boolean esPracticaProfesional = "PRACTICA_PROFESIONAL".equals(modalidad);
        return filePGRepository.validarDocumentosRequeridos(idProyecto, esPracticaProfesional);
    }

    /**
     * Obtiene estadísticas de documentos de un proyecto
     */
    public String obtenerEstadisticasProyecto(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        
        // Verificar que el proyecto existe
        proyectoGradoService.obtenerProyecto(idProyecto);
        
        return filePGRepository.obtenerEstadisticasProyecto(idProyecto);
    }

    // ========== MÉTODOS DE VALIDACIÓN PRIVADOS ==========
    
    /**
     * Valida un documento completo
     */
    private void validarDocumento(FilePG documento) throws InvalidUserDataException {
        ValidationUtil.validarNoNulo(documento, "documento");
        ValidationUtil.validarPositivo(documento.getIdProyecto(), "ID del proyecto");
        ValidationUtil.validarNoVacio(documento.getNombreArchivo(), "nombre del archivo");
        ValidationUtil.validarNoNulo(documento.getContenido(), "contenido del archivo");
        
        validarTipoDocumento(documento.getTipoDocumento());
        
        // Validar tamaño
        if (documento.getContenido().length > TAMAÑO_MAXIMO) {
            throw new InvalidUserDataException("El archivo excede el tamaño máximo permitido (20MB)");
        }
        
        // Validar extensión según tipo de documento
        validarExtensionPorTipo(documento.getNombreArchivo(), documento.getTipoDocumento());
    }

    /**
     * Valida que el tipo de documento sea válido
     */
    private void validarTipoDocumento(String tipoDocumento) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(tipoDocumento, "tipo de documento");
        
        try {
            FilePG.TipoDocumento.fromValor(tipoDocumento);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserDataException("Tipo de documento no válido: " + tipoDocumento);
        }
    }

    /**
     * Valida que el estado del documento sea válido
     */
    private void validarEstadoDocumento(String estado) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(estado, "estado del documento");
        
        String[] estadosValidos = {"PENDIENTE", "EN_REVISION", "APROBADO", "RECHAZADO", "OBSOLETO"};
        boolean esValido = Arrays.asList(estadosValidos).contains(estado);
        
        if (!esValido) {
            throw new InvalidUserDataException("Estado de documento no válido: " + estado);
        }
    }

    /**
     * Valida extensión de archivo
     */
    private boolean validarExtension(String nombreArchivo, String[] extensionesPermitidas) {
        String extension = obtenerExtension(nombreArchivo);
        return Arrays.asList(extensionesPermitidas).contains(extension.toLowerCase());
    }

    /**
     * Valida extensión según tipo de documento
     */
    private void validarExtensionPorTipo(String nombreArchivo, String tipoDocumento) 
            throws InvalidUserDataException {
        
        String[] extensionesPermitidas;
        String tipoTexto;
        
        switch (tipoDocumento) {
            case "FORMATO_A":
            case "FORMATO_B":
                extensionesPermitidas = EXTENSIONES_PDF;
                tipoTexto = "PDF";
                break;
            case "CARTA_EMPRESA":
            case "MONOGRAFIA":
            case "ANEXOS":
                extensionesPermitidas = EXTENSIONES_DOCUMENTOS;
                tipoTexto = "PDF, DOC o DOCX";
                break;
            case "PRESENTACION":
                extensionesPermitidas = EXTENSIONES_PRESENTACION;
                tipoTexto = "PDF, PPT o PPTX";
                break;
            default:
                extensionesPermitidas = EXTENSIONES_DOCUMENTOS;
                tipoTexto = "PDF, DOC o DOCX";
        }
        
        if (!validarExtension(nombreArchivo, extensionesPermitidas)) {
            throw new InvalidUserDataException(
                "El tipo de documento " + tipoDocumento + " solo acepta archivos " + tipoTexto
            );
        }
    }

    /**
     * Obtiene la extensión de un archivo
     */
    private String obtenerExtension(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        return (ultimoPunto > 0) ? nombreArchivo.substring(ultimoPunto) : "";
    }
}