package co.unicauca.solid.service;

import co.unicauca.solid.access.IFilePGRepository;
import co.unicauca.solid.access.IProyectoGradoRepository;
import co.unicauca.solid.domain.FilePG;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import co.unicauca.utilities.validators.ValidationUtil;
import java.util.List;
import java.util.Arrays;

public class FilePGService {
    
    private final IFilePGRepository filePGRepository;
    private final IProyectoGradoRepository proyectoGradoRepository; 
    
    private static final String[] EXTENSIONES_PDF = {"pdf"};
    private static final String[] EXTENSIONES_DOCUMENTOS = {"pdf", "doc", "docx"}; 
    private static final String[] EXTENSIONES_PRESENTACION = {"pdf", "ppt", "pptx"}; 
    
    private static final long TAMAÑO_MAXIMO = 20 * 1024 * 1024;

    public FilePGService(IFilePGRepository filePGRepository, IProyectoGradoRepository proyectoGradoRepository) { 
        this.filePGRepository = filePGRepository;
        this.proyectoGradoRepository = proyectoGradoRepository;
    }

    private void verificarProyectoExiste(Integer idProyecto) throws InvalidUserDataException, UserNotFoundException {
        try {
            co.unicauca.solid.domain.ProyectoGrado proyecto = proyectoGradoRepository.obtenerProyectoPorId(idProyecto);
            if (proyecto == null) {
                throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
            }
        } catch (co.unicauca.utilities.exeption.InvalidUserDataException e) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }
    }

    public int insertarDocumento(FilePG documento) throws InvalidUserDataException, UserNotFoundException {
        validarDocumento(documento);
        return filePGRepository.insertarDocumento(documento);
    }

    public FilePG obtenerDocumentoPorId(int id) throws UserNotFoundException {
        FilePG documento = filePGRepository.obtenerDocumentoPorId(id);
        if (documento == null) {
            throw new UserNotFoundException("Documento con ID " + id + " no encontrado");
        }
        return documento;
    }

    public List<FilePG> obtenerDocumentosPorProyecto(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        verificarProyectoExiste(idProyecto); 
        List<FilePG> documentos = filePGRepository.obtenerDocumentosPorProyecto(idProyecto);
        if (documentos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron documentos para el proyecto " + idProyecto);
        }
        return documentos;
    }

    public List<FilePG> obtenerDocumentosPorProyectoYTipo(Integer idProyecto, String tipoDocumento) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        validarTipoDocumento(tipoDocumento);
        verificarProyectoExiste(idProyecto); 
        return filePGRepository.obtenerDocumentosPorProyectoYTipo(idProyecto, tipoDocumento);
    }

    public boolean actualizarEstadoDocumento(int idDocumento, String nuevoEstado, String observaciones) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idDocumento, "ID del documento");
        validarEstadoDocumento(nuevoEstado);
        obtenerDocumentoPorId(idDocumento);
        return filePGRepository.actualizarEstadoDocumento(idDocumento, nuevoEstado, observaciones);
    }

    public boolean eliminarDocumento(int id) throws UserNotFoundException {
        obtenerDocumentoPorId(id);
        return filePGRepository.eliminarDocumento(id);
    }

    public boolean descargarDocumento(int id, String ruta, String nombreArchivo) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarNoVacio(ruta, "ruta de descarga");
        obtenerDocumentoPorId(id);
        return filePGRepository.guardarEnDisco(id, ruta, nombreArchivo);
    }

  public int subirFormatoA(Integer idProyecto, byte[] contenido, String nombreArchivo) 
        throws InvalidUserDataException, UserNotFoundException {
    ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
    ValidationUtil.validarNoNulo(contenido, "contenido del archivo");
    ValidationUtil.validarNoVacio(nombreArchivo, "nombre del archivo");
    verificarProyectoExiste(idProyecto);
    
    if (!validarExtension(nombreArchivo, EXTENSIONES_PDF)) {
        throw new InvalidUserDataException("El Formato A solo puede ser un archivo PDF");
    }
    if (contenido.length > TAMAÑO_MAXIMO) {
        throw new InvalidUserDataException("El archivo excede el tamaño máximo permitido (20MB)");
    }
    
    //  Obtener el número de intento actual del proyecto
    co.unicauca.solid.domain.ProyectoGrado proyecto = proyectoGradoRepository.obtenerProyectoPorId(idProyecto);
    int version = proyecto.getNumeroIntento();
    
    //  Crear FilePG con la versión correcta usando el constructor adecuado
    FilePG documento = new FilePG(idProyecto, "FORMATO_A", version, nombreArchivo);
    documento.setContenido(contenido); // Esto también establece el tamaño
    
    return filePGRepository.insertarDocumento(documento);
}
    public FilePG obtenerFormatoAActual(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        verificarProyectoExiste(idProyecto);
        FilePG formatoA = filePGRepository.obtenerFormatoAActual(idProyecto);
        if (formatoA == null) {
            throw new UserNotFoundException("No se encontró Formato A para el proyecto " + idProyecto);
        }
        return formatoA;
    }

    public List<FilePG> obtenerHistorialFormatoA(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        verificarProyectoExiste(idProyecto);
        List<FilePG> historial = filePGRepository.obtenerHistorialFormatoA(idProyecto);
        if (historial.isEmpty()) {
            throw new UserNotFoundException("No se encontró historial de Formato A para el proyecto " + idProyecto);
        }
        return historial;
    }

    public int subirCartaEmpresa(Integer idProyecto, byte[] contenido, String nombreArchivo) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        ValidationUtil.validarNoNulo(contenido, "contenido del archivo");
        ValidationUtil.validarNoVacio(nombreArchivo, "nombre del archivo");
        verificarProyectoExiste(idProyecto); 
        if (!validarExtension(nombreArchivo, EXTENSIONES_DOCUMENTOS)) {
            throw new InvalidUserDataException("La carta de empresa debe ser PDF, DOC o DOCX");
        }
        if (contenido.length > TAMAÑO_MAXIMO) {
            throw new InvalidUserDataException("El archivo excede el tamaño máximo permitido (20MB)");
        }
        return filePGRepository.subirCartaEmpresa(idProyecto, contenido, nombreArchivo);
    }

    public FilePG obtenerCartaEmpresa(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        verificarProyectoExiste(idProyecto); 
        FilePG carta = filePGRepository.obtenerCartaEmpresa(idProyecto);
        if (carta == null) {
            throw new UserNotFoundException("No se encontró carta de empresa para el proyecto " + idProyecto);
        }
        return carta;
    }

    public boolean tieneCartaEmpresa(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        verificarProyectoExiste(idProyecto); 
        return filePGRepository.tieneCartaEmpresa(idProyecto);
    }

    public boolean validarDocumentosRequeridos(Integer idProyecto, String modalidad) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        ValidationUtil.validarNoVacio(modalidad, "modalidad");
        verificarProyectoExiste(idProyecto); 
        boolean esPracticaProfesional = "PRACTICA_PROFESIONAL".equals(modalidad);
        return filePGRepository.validarDocumentosRequeridos(idProyecto, esPracticaProfesional);
    }

    public String obtenerEstadisticasProyecto(Integer idProyecto) 
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        verificarProyectoExiste(idProyecto); 
        return filePGRepository.obtenerEstadisticasProyecto(idProyecto);
    }

    private void validarDocumento(FilePG documento) throws InvalidUserDataException {
        ValidationUtil.validarNoNulo(documento, "documento");
        ValidationUtil.validarPositivo(documento.getIdProyecto(), "ID del proyecto");
        ValidationUtil.validarNoVacio(documento.getNombreArchivo(), "nombre del archivo");
        ValidationUtil.validarNoNulo(documento.getContenido(), "contenido del archivo");
        validarTipoDocumento(documento.getTipoDocumento());
        if (documento.getContenido().length > TAMAÑO_MAXIMO) {
            throw new InvalidUserDataException("El archivo excede el tamaño máximo permitido (20MB)");
        }
        validarExtensionPorTipo(documento.getNombreArchivo(), documento.getTipoDocumento());
    }

    private void validarTipoDocumento(String tipoDocumento) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(tipoDocumento, "tipo de documento");
        try {
            FilePG.TipoDocumento.fromValor(tipoDocumento);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserDataException("Tipo de documento no válido: " + tipoDocumento);
        }
    }

    private void validarEstadoDocumento(String estado) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(estado, "estado del documento");
        String[] estadosValidos = {"PENDIENTE", "EN_REVISION", "APROBADO", "RECHAZADO", "OBSOLETO"};
        boolean esValido = Arrays.asList(estadosValidos).contains(estado);
        if (!esValido) {
            throw new InvalidUserDataException("Estado de documento no válido: " + estado);
        }
    }

    private boolean validarExtension(String nombreArchivo, String[] extensionesPermitidas) {
        String extension = obtenerExtension(nombreArchivo);
        return Arrays.asList(extensionesPermitidas).contains(extension.toLowerCase());
    }

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

    private String obtenerExtension(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        return (ultimoPunto > 0) ? nombreArchivo.substring(ultimoPunto + 1) : ""; 
    }
}