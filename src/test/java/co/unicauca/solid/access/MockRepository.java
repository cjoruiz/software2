// Archivo: co/unicauca/solid/access/MockRepository.java
package co.unicauca.solid.access;

import co.unicauca.solid.domain.*;
import co.unicauca.solid.domain.enums.EstadoEnum;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MockRepository implements 
    IUserRepository, 
    IFilePGRepository, 
    IProyectoGradoRepository,
    IMensajeInternoRepository,
    IProgramRepository {

    // --- Usuarios ---
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();
    
    // --- Proyectos ---
    private final Map<Integer, ProyectoGrado> proyectos = new ConcurrentHashMap<>();
    private final AtomicInteger nextProyectoId = new AtomicInteger(1);
    
    // --- Documentos ---
    private final Map<Integer, FilePG> documentos = new ConcurrentHashMap<>();
    private final AtomicInteger nextDocumentoId = new AtomicInteger(1);
    
    // --- Mensajes ---
    private final Map<Integer, MensajeInterno> mensajes = new ConcurrentHashMap<>();
    private final AtomicInteger nextMensajeId = new AtomicInteger(1);
    
    // --- Programas ---
    private final Map<String, Programa> programas = new ConcurrentHashMap<>();

    // ========== IUserRepository ==========
    @Override
    public boolean save(Usuario newUser) {
        if (usuarios.containsKey(newUser.getEmail())) return false;
        usuarios.put(newUser.getEmail(), newUser);
        return true;
    }

    @Override
    public boolean update(Usuario user) {
        if (!usuarios.containsKey(user.getEmail())) return false;
        usuarios.put(user.getEmail(), user);
        return true;
    }

    @Override
    public boolean validateLogin(String email, String password) {
        Usuario u = usuarios.get(email);
        return u != null && u.getPassword().equals(password);
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarios.get(email);
    }

    @Override
    public List<Usuario> findByRole(String role) {
        return usuarios.values().stream()
                .filter(u -> u.getRol().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> list() {
        return new ArrayList<>(usuarios.values());
    }

    // ========== IProyectoGradoRepository ==========
    @Override
    public int insertarProyecto(ProyectoGrado proyecto) {
        int id = nextProyectoId.getAndIncrement();
        proyecto.setIdProyecto(id);
        proyectos.put(id, proyecto);
        return id;
    }

    @Override
    public ProyectoGrado obtenerProyectoPorId(int idProyecto) throws InvalidUserDataException {
        return proyectos.get(idProyecto);
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail) throws InvalidUserDataException {
        return proyectos.values().stream()
                .filter(p -> p.getEstudiante1() != null && p.getEstudiante1().getEmail().equals(estudianteEmail))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail) throws InvalidUserDataException {
        return proyectos.values().stream()
                .filter(p -> p.getDirector() != null && p.getDirector().getEmail().equals(directorEmail))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProyectoGrado> obtenerTodosProyectos() throws InvalidUserDataException {
        return new ArrayList<>(proyectos.values());
    }

    @Override
    public boolean actualizarProyecto(ProyectoGrado proyecto) throws InvalidUserDataException {
        if (!proyectos.containsKey(proyecto.getIdProyecto())) return false;
        proyectos.put(proyecto.getIdProyecto(), proyecto);
        return true;
    }

    @Override
    public boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado) throws InvalidUserDataException {
        ProyectoGrado p = proyectos.get(idProyecto);
        if (p == null) return false;
        p.setEstadoActual(nuevoEstado);
        return true;
    }

    @Override
    public boolean incrementarIntento(int idProyecto) throws InvalidUserDataException {
        ProyectoGrado p = proyectos.get(idProyecto);
        if (p == null) return false;
        p.setNumeroIntento(p.getNumeroIntento() + 1);
        return true;
    }

    @Override
    public boolean marcarRechazoDefinitivo(int idProyecto) throws InvalidUserDataException {
        ProyectoGrado p = proyectos.get(idProyecto);
        if (p == null) return false;
        p.setRechazadoDefinitivamente('S');
        p.setEstadoActual(EstadoEnum.RECHAZADO_DEFINITIVO.getValor());
        return true;
    }

    @Override
    public boolean eliminarProyecto(int idProyecto) throws InvalidUserDataException {
        return proyectos.remove(idProyecto) != null;
    }

    @Override
    public boolean evaluarFormatoA(int idProyecto, boolean aprobado, String observaciones) throws InvalidUserDataException {
        ProyectoGrado p = proyectos.get(idProyecto);
        if (p == null) return false;
        p.setObservacionesEvaluacion(observaciones);
        if (aprobado) {
            p.setEstadoActual(EstadoEnum.FORMATO_A_APROBADO.getValor());
        } else {
            p.setEstadoActual(EstadoEnum.FORMATO_A_RECHAZADO.getValor());
            if (p.getNumeroIntento() >= 3) {
                marcarRechazoDefinitivo(idProyecto);
            }
        }
        return true;
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPendientesEvaluacion() throws InvalidUserDataException {
        return proyectos.values().stream()
                .filter(p -> p.getEstadoActual().contains("EVALUACION_FORMATO_A"))
                .collect(Collectors.toList());
    }

    @Override
    public boolean procesarReintentoFormatoA(int idProyecto) throws InvalidUserDataException {
        ProyectoGrado p = proyectos.get(idProyecto);
        if (p == null || !p.puedeReintentar()) return false;
        p.procesarReintentoFormatoA();
        return true;
    }

    // ========== IFilePGRepository ==========
    @Override
    public int insertarDocumento(FilePG documento) {
        int id = nextDocumentoId.getAndIncrement();
        documento.setIdDocumento(id);
        documentos.put(id, documento);
        return id;
    }

    @Override
    public List<FilePG> obtenerDocumentosPorProyectoYTipo(Integer idProyecto, String tipoDocumento) {
        return documentos.values().stream()
                .filter(d -> d.getIdProyecto().equals(idProyecto) && d.getTipoDocumento().equals(tipoDocumento))
                .collect(Collectors.toList());
    }

    @Override
    public List<FilePG> obtenerDocumentosPorProyecto(Integer idProyecto) {
        return documentos.values().stream()
                .filter(d -> d.getIdProyecto().equals(idProyecto))
                .collect(Collectors.toList());
    }

    @Override
    public FilePG obtenerDocumentoPorId(int id) {
        return documentos.get(id);
    }

    @Override
    public FilePG obtenerUltimaVersionDocumento(Integer idProyecto, String tipoDocumento) {
        return documentos.values().stream()
                .filter(d -> d.getIdProyecto().equals(idProyecto) && d.getTipoDocumento().equals(tipoDocumento))
                .max(Comparator.comparing(FilePG::getVersion))
                .orElse(null);
    }

    @Override
    public List<FilePG> obtenerTodosDocumentos() {
        return new ArrayList<>(documentos.values());
    }

    @Override
    public boolean actualizarEstadoDocumento(int idDocumento, String nuevoEstado, String observaciones) {
        FilePG doc = documentos.get(idDocumento);
        if (doc == null) return false;
        doc.setEstado(nuevoEstado);
        doc.setObservaciones(observaciones);
        return true;
    }

    @Override
    public boolean existeDocumentoTipo(Integer idProyecto, String tipoDocumento) {
        return documentos.values().stream()
                .anyMatch(d -> d.getIdProyecto().equals(idProyecto) && d.getTipoDocumento().equals(tipoDocumento));
    }

    @Override
    public boolean eliminarDocumento(int id) {
        return documentos.remove(id) != null;
    }

    @Override
    public boolean guardarEnDisco(int id, String ruta, String nombreArchivo) {
        return documentos.containsKey(id); // Simulación
    }

    @Override
    public boolean descargar(int id, String ruta, String nombreArchivo) {
        return guardarEnDisco(id, ruta, nombreArchivo);
    }

    @Override
    public int subirFormatoA(Integer idProyecto, byte[] contenido, String nombreArchivo) {
        FilePG doc = new FilePG(idProyecto, "FORMATO_A", contenido, nombreArchivo);
        return insertarDocumento(doc);
    }

    @Override
    public int subirCartaEmpresa(Integer idProyecto, byte[] contenido, String nombreArchivo) {
        FilePG doc = new FilePG(idProyecto, "CARTA_EMPRESA", contenido, nombreArchivo);
        return insertarDocumento(doc);
    }

    @Override
    public boolean tieneCartaEmpresa(Integer idProyecto) {
        return existeDocumentoTipo(idProyecto, "CARTA_EMPRESA");
    }

    @Override
    public FilePG obtenerFormatoAActual(Integer idProyecto) {
        return obtenerUltimaVersionDocumento(idProyecto, "FORMATO_A");
    }

    @Override
    public FilePG obtenerCartaEmpresa(Integer idProyecto) {
        return obtenerUltimaVersionDocumento(idProyecto, "CARTA_EMPRESA");
    }

    @Override
    public List<FilePG> obtenerHistorialFormatoA(Integer idProyecto) {
        return obtenerDocumentosPorProyectoYTipo(idProyecto, "FORMATO_A");
    }

    @Override
    public boolean validarDocumentosRequeridos(Integer idProyecto, boolean esPracticaProfesional) {
        boolean tieneFormatoA = existeDocumentoTipo(idProyecto, "FORMATO_A");
        boolean tieneCarta = !esPracticaProfesional || tieneCartaEmpresa(idProyecto);
        return tieneFormatoA && tieneCarta;
    }

    @Override
    public String obtenerEstadisticasProyecto(Integer idProyecto) {
        long total = documentos.values().stream().filter(d -> d.getIdProyecto().equals(idProyecto)).count();
        return "Proyecto " + idProyecto + " tiene " + total + " documentos.";
    }

    // ========== IMensajeInternoRepository ==========
    @Override
    public int enviarMensaje(MensajeInterno mensaje) {
        int id = nextMensajeId.getAndIncrement();
        mensaje.setIdMensaje(id);
        mensajes.put(id, mensaje);
        return id;
    }

    @Override
    public List<MensajeInterno> obtenerMensajesPorRemitente(String remitenteEmail) {
        return mensajes.values().stream()
                .filter(m -> m.getRemitenteEmail().equals(remitenteEmail))
                .collect(Collectors.toList());
    }

    @Override
    public List<MensajeInterno> obtenerMensajesPorDestinatario(String destinatarioEmail) {
        return mensajes.values().stream()
                .filter(m -> Arrays.stream(m.getDestinatariosEmail().split(","))
                        .anyMatch(d -> d.trim().equals(destinatarioEmail)))
                .collect(Collectors.toList());
    }

    @Override
    public MensajeInterno obtenerMensajePorId(int idMensaje) {
        return mensajes.get(idMensaje);
    }

    @Override
    public boolean marcarComoLeido(int idMensaje) {
        MensajeInterno m = mensajes.get(idMensaje);
        if (m == null) return false;
        m.setEstado(MensajeInterno.EstadoMensaje.LEIDO.getValor());
        return true;
    }

    @Override
    public boolean eliminarMensaje(int idMensaje) {
        return mensajes.remove(idMensaje) != null;
    }

    // ========== IProgramRepository ==========
    @Override
    public boolean insertarPrograma(Programa programa) {
        if (programas.containsKey(programa.getIdPrograma())) return false;
        programas.put(programa.getIdPrograma(), programa);
        return true;
    }

    @Override
    public Programa obtenerProgramaPorId(String idPrograma) {
        return programas.get(idPrograma);
    }

    @Override
    public List<Programa> obtenerTodosProgramas() {
        return new ArrayList<>(programas.values());
    }

    @Override
    public boolean actualizarPrograma(Programa programa) {
        if (!programas.containsKey(programa.getIdPrograma())) return false;
        programas.put(programa.getIdPrograma(), programa);
        return true;
    }

    @Override
    public boolean eliminarPrograma(String idPrograma) {
        return programas.remove(idPrograma) != null;
    }
}