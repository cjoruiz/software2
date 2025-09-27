/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository para ProyectoGrado - Maneja toda la lógica de persistencia de los
 * proyectos de grado y su estado
 *
 * @author crist
 */
public class ProyectoGradoRepository implements IProyectoGradoRepository {

    private final UserService userService; 

    public ProyectoGradoRepository(UserService userService) { 
        this.userService = userService;
        initDatabase();
    }

    @Override
    public int insertarProyecto(ProyectoGrado proyecto) {
        String sql = "INSERT INTO proyectos_grado ("
                + "titulo, modalidad, director_email, codirector_email, "
                + "estudiante1_email, estudiante2_email, objetivo_general, objetivos_especificos, "
                + "estado_actual, numero_intento, fecha_creacion, "
                + "fecha_ultima_actualizacion, rechazado_definitivamente, "
                + "observaciones_evaluacion"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, proyecto.getTitulo());
            pstmt.setString(2, proyecto.getModalidad());
            pstmt.setString(3, proyecto.getDirector().getEmail()); 
            pstmt.setString(4, proyecto.getCodirector() != null ? proyecto.getCodirector().getEmail() : null);
            pstmt.setString(5, proyecto.getEstudiante1().getEmail());
            pstmt.setString(6, proyecto.getEstudiante2() != null ? proyecto.getEstudiante2().getEmail() : null);
            pstmt.setString(7, proyecto.getObjetivoGeneral());
            pstmt.setString(8, proyecto.getObjetivosEspecificos());
            pstmt.setString(9, proyecto.getEstadoActual());
            pstmt.setInt(10, proyecto.getNumeroIntento());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(11, proyecto.getFechaCreacion().format(formatter));
            pstmt.setString(12, proyecto.getFechaUltimaActualizacion().format(formatter));

            pstmt.setString(13, String.valueOf(proyecto.getRechazadoDefinitivamente()));
            pstmt.setString(14, proyecto.getObservacionesEvaluacion());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Error insertando proyecto: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public ProyectoGrado obtenerProyectoPorId(int idProyecto) throws InvalidUserDataException { 
        String sql = "SELECT * FROM proyectos_grado WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProyecto(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error obteniendo proyecto: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail) throws InvalidUserDataException { 
        String sql = "SELECT * FROM proyectos_grado WHERE estudiante1_email = ? OR estudiante2_email = ? ORDER BY fecha_creacion DESC"; // <-- ¡CORREGIDO! (La tabla no tiene 'estudiante_email')
        List<ProyectoGrado> proyectos = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, estudianteEmail);
            pstmt.setString(2, estudianteEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    proyectos.add(mapearProyecto(rs));
                }
            }
            return proyectos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo proyectos por estudiante: " + e.getMessage());
            throw new InvalidUserDataException("Error al obtener proyectos por estudiante: " + e.getMessage()); 
        }
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail) throws InvalidUserDataException { 
        String sql = "SELECT * FROM proyectos_grado WHERE director_email = ? ORDER BY fecha_creacion DESC";
        List<ProyectoGrado> proyectos = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, directorEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    proyectos.add(mapearProyecto(rs));
                }
            }
            return proyectos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo proyectos por director: " + e.getMessage());
            throw new InvalidUserDataException("Error al obtener proyectos por director: " + e.getMessage()); 
        }
    }

    /**
     * Obtiene proyectos que están pendientes de evaluación por el coordinador
     */
    @Override
    public List<ProyectoGrado> obtenerProyectosPendientesEvaluacion() throws InvalidUserDataException {
        String sql = "SELECT * FROM proyectos_grado "
                + "WHERE estado_actual IN ("
                + "    'EN_PRIMERA_EVALUACION_FORMATO_A', "
                + "    'EN_SEGUNDA_EVALUACION_FORMATO_A', "
                + "    'EN_TERCERA_EVALUACION_FORMATO_A'"
                + ") "
                + "ORDER BY fecha_creacion ASC";
        List<ProyectoGrado> proyectos = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                proyectos.add(mapearProyecto(rs));
            }
            return proyectos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo proyectos pendientes: " + e.getMessage());
            throw new InvalidUserDataException("Error al obtener proyectos pendientes: " + e.getMessage()); 
        }
    }

    @Override
    public List<ProyectoGrado> obtenerTodosProyectos() throws InvalidUserDataException { 
        String sql = "SELECT * FROM proyectos_grado ORDER BY fecha_creacion DESC";
        List<ProyectoGrado> proyectos = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                proyectos.add(mapearProyecto(rs));
            }
            return proyectos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo todos los proyectos: " + e.getMessage());
            throw new InvalidUserDataException("Error al obtener todos los proyectos: " + e.getMessage()); 
        }
    }

    @Override
    public boolean actualizarProyecto(ProyectoGrado proyecto) throws InvalidUserDataException { 
        String sql
                = "UPDATE proyectos_grado SET "
                + "titulo = ?, modalidad = ?, director_email = ?, "
                + "codirector_email = ?, estudiante1_email = ?, estudiante2_email = ?, "
                + "objetivo_general = ?, objetivos_especificos = ?, "
                + "estado_actual = ?, numero_intento = ?, "
                + "fecha_ultima_actualizacion = ?, rechazado_definitivamente = ?, "
                + "observaciones_evaluacion = ? "
                + "WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, proyecto.getTitulo());
            pstmt.setString(2, proyecto.getModalidad());
            pstmt.setString(3, proyecto.getDirector().getEmail()); 
            pstmt.setString(4, proyecto.getCodirector() != null ? proyecto.getCodirector().getEmail() : null); 
            pstmt.setString(5, proyecto.getEstudiante1().getEmail()); 
            pstmt.setString(6, proyecto.getEstudiante2() != null ? proyecto.getEstudiante2().getEmail() : null); 
            pstmt.setString(7, proyecto.getObjetivoGeneral());
            pstmt.setString(8, proyecto.getObjetivosEspecificos());
            pstmt.setString(9, proyecto.getEstadoActual());
            pstmt.setInt(10, proyecto.getNumeroIntento());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(11, LocalDateTime.now().format(formatter));
            pstmt.setString(12, String.valueOf(proyecto.getRechazadoDefinitivamente()));
            pstmt.setString(13, proyecto.getObservacionesEvaluacion());
            pstmt.setInt(14, proyecto.getIdProyecto());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando proyecto: " + e.getMessage());
            throw new InvalidUserDataException("Error al actualizar el proyecto: " + e.getMessage()); 
        }
    }

    /**
     * Evalúa un formato A - Método específico para coordinadores
     */
    @Override
    public boolean evaluarFormatoA(int idProyecto, boolean aprobado, String observaciones) throws InvalidUserDataException { 
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Obtener el proyecto actual
                ProyectoGrado proyecto = obtenerProyectoPorId(idProyecto);
                if (proyecto == null) {
                    throw new SQLException("Proyecto no encontrado");
                }

                if (aprobado) {
                    proyecto.aprobarFormatoA();
                } else {
                    proyecto.rechazarFormatoA(observaciones);
                }

                // Actualizar proyecto
                boolean actualizado = actualizarProyecto(proyecto);

                if (actualizado) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }

            } catch (Exception e) {
                connection.rollback();
                if (e instanceof InvalidUserDataException) {
                    throw e;
                } else {
                    throw new InvalidUserDataException("Error al evaluar el formato A: " + e.getMessage()); 
                }
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error evaluando formato A: " + e.getMessage());
            throw new InvalidUserDataException("Error al evaluar el formato A: " + e.getMessage());
        }
    }

    /**
     * Procesa un reintento de formato A
     */
    @Override
    public boolean procesarReintentoFormatoA(int idProyecto) throws InvalidUserDataException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                ProyectoGrado proyecto = obtenerProyectoPorId(idProyecto);
                if (proyecto == null) {
                    throw new SQLException("Proyecto no encontrado");
                }

                if (!proyecto.puedeReintentar()) {
                    throw new IllegalStateException("El proyecto no puede reintentar más veces");
                }

                proyecto.procesarReintentoFormatoA();

                boolean actualizado = actualizarProyecto(proyecto);

                if (actualizado) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }

            } catch (Exception e) {
                connection.rollback();
                if (e instanceof InvalidUserDataException) {
                    throw e;
                } else {
                    throw new InvalidUserDataException("Error al procesar el reintento: " + e.getMessage());
                }
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error procesando reintento: " + e.getMessage());
            throw new InvalidUserDataException("Error al procesar el reintento: " + e.getMessage()); 
        }
    }

    @Override
    public boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado) throws InvalidUserDataException { 
        String sql
                = "UPDATE proyectos_grado SET "
                + "    estado_actual = ?, fecha_ultima_actualizacion = ? "
                + "WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(2, LocalDateTime.now().format(formatter));
            pstmt.setInt(3, idProyecto);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando estado del proyecto: " + e.getMessage());
            throw new InvalidUserDataException("Error al actualizar el estado del proyecto: " + e.getMessage()); 
        }
    }

    @Override
    public boolean incrementarIntento(int idProyecto) throws InvalidUserDataException {
        return procesarReintentoFormatoA(idProyecto);
    }

    @Override
    public boolean marcarRechazoDefinitivo(int idProyecto) throws InvalidUserDataException {
        try (Connection connection = getConnection()) {
            ProyectoGrado proyecto = obtenerProyectoPorId(idProyecto);
            if (proyecto == null) {
                return false;
            }

            proyecto.marcarRechazoDefinitivo();
            return actualizarProyecto(proyecto);

        } catch (SQLException e) {
            System.err.println("Error marcando rechazo definitivo: " + e.getMessage());
            throw new InvalidUserDataException("Error al marcar rechazo definitivo: " + e.getMessage()); 
        }
    }

    @Override
    public boolean eliminarProyecto(int idProyecto) throws InvalidUserDataException {
        String sql = "DELETE FROM proyectos_grado WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando proyecto: " + e.getMessage());
            throw new InvalidUserDataException("Error al eliminar el proyecto: " + e.getMessage()); 
        }
    }

    /**
     * Mapea ResultSet a ProyectoGrado
     */
    private ProyectoGrado mapearProyecto(ResultSet rs) throws SQLException, InvalidUserDataException {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setIdProyecto(rs.getInt("id_proyecto"));
        proyecto.setTitulo(rs.getString("titulo"));
        proyecto.setModalidad(rs.getString("modalidad"));
        proyecto.setObjetivoGeneral(rs.getString("objetivo_general"));
        proyecto.setObjetivosEspecificos(rs.getString("objetivos_especificos"));
        proyecto.setEstadoActual(rs.getString("estado_actual"));
        proyecto.setNumeroIntento(rs.getInt("numero_intento"));
        proyecto.setObservacionesEvaluacion(rs.getString("observaciones_evaluacion"));

        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String fechaCreacionStr = rs.getString("fecha_creacion");
        if (fechaCreacionStr != null && !fechaCreacionStr.trim().isEmpty()) {
            try {
                String isoString = fechaCreacionStr.trim().replace(' ', 'T');
                proyecto.setFechaCreacion(LocalDateTime.parse(isoString, isoFormatter));
            } catch (DateTimeParseException e) {
                System.err.println("❌ Error al parsear fecha_creacion: '" + fechaCreacionStr + "'. Se usará la fecha actual.");
                proyecto.setFechaCreacion(LocalDateTime.now());
            }
        }

        String fechaActualizacionStr = rs.getString("fecha_ultima_actualizacion");
        if (fechaActualizacionStr != null && !fechaActualizacionStr.trim().isEmpty()) {
            try {
                String isoString = fechaActualizacionStr.trim().replace(' ', 'T');
                proyecto.setFechaUltimaActualizacion(LocalDateTime.parse(isoString, isoFormatter));
            } catch (DateTimeParseException e) {
                System.err.println("❌ Error al parsear fecha_ultima_actualizacion: '" + fechaActualizacionStr + "'. Se usará la fecha actual.");
                proyecto.setFechaUltimaActualizacion(LocalDateTime.now());
            }
        }

        String rechazoDef = rs.getString("rechazado_definitivamente");
        if (rechazoDef != null && !rechazoDef.isEmpty()) {
            proyecto.setRechazadoDefinitivamente(rechazoDef.charAt(0));
        }

        try {
            // Director (siempre obligatorio)
            String directorEmail = rs.getString("director_email");
            Usuario director = userService.findByEmail(directorEmail);
            if (director == null) { // <-- ¡¡¡ NUEVA VERIFICACIÓN DE NULL !!!
                throw new SQLException("No se encontró el usuario director con email: " + directorEmail);
            }
            if (!(director instanceof Docente)) {
                throw new SQLException("El director con email " + directorEmail + " no es un docente.");
            }
            proyecto.setDirector((Docente) director);

            // Codirector (opcional)
            String codirectorEmail = rs.getString("codirector_email");
            if (codirectorEmail != null && !codirectorEmail.trim().isEmpty()) {
                Usuario codirector = userService.findByEmail(codirectorEmail);
                if (codirector == null) { 
                    throw new SQLException("No se encontró el usuario codirector con email: " + codirectorEmail);
                }
                if (!(codirector instanceof Docente)) {
                    throw new SQLException("El codirector con email " + codirectorEmail + " no es un docente.");
                }
                proyecto.setCodirector((Docente) codirector);
            }

            // Estudiante 1 (siempre obligatorio)
            String estudiante1Email = rs.getString("estudiante1_email");
            Usuario estudiante1 = userService.findByEmail(estudiante1Email);
            if (estudiante1 == null) { 
                throw new SQLException("No se encontró el usuario estudiante con email: " + estudiante1Email);
            }
            if (!(estudiante1 instanceof Estudiante)) {
                throw new SQLException("El estudiante con email " + estudiante1Email + " no es un estudiante.");
            }
            proyecto.setEstudiante1((Estudiante) estudiante1);

            // Estudiante 2 (opcional)
            String estudiante2Email = rs.getString("estudiante2_email");
            if (estudiante2Email != null && !estudiante2Email.trim().isEmpty()) {
                Usuario estudiante2 = userService.findByEmail(estudiante2Email);
                if (estudiante2 == null) { 
                    throw new SQLException("No se encontró el usuario estudiante con email: " + estudiante2Email);
                }
                if (!(estudiante2 instanceof Estudiante)) {
                    throw new SQLException("El estudiante con email " + estudiante2Email + " no es un estudiante.");
                }
                proyecto.setEstudiante2((Estudiante) estudiante2);
            }

        } catch (UserNotFoundException e) {
            throw new SQLException("Error al mapear usuarios del proyecto: " + e.getMessage(), e);
        }

        return proyecto;
    }

    /**
     * Inicializa la base de datos con la nueva estructura
     */
    private void initDatabase() {
        String sql
                = "CREATE TABLE IF NOT EXISTS proyectos_grado (\n"
                + "    id_proyecto INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    titulo TEXT NOT NULL,\n"
                + "    modalidad TEXT NOT NULL CHECK (modalidad IN ('INVESTIGACION', 'PRACTICA_PROFESIONAL')),\n"
                + "    director_email TEXT NOT NULL,\n"
                + "    codirector_email TEXT,\n"
                + "    estudiante1_email TEXT NOT NULL,\n"
                + "    estudiante2_email TEXT,\n"
                + "    objetivo_general TEXT,\n"
                + "    objetivos_especificos TEXT,\n"
                + "    estado_actual TEXT DEFAULT 'EN_PRIMERA_EVALUACION_FORMATO_A',\n"
                + "    numero_intento INTEGER DEFAULT 1 CHECK (numero_intento BETWEEN 1 AND 3),\n"
                + "    fecha_creacion TEXT NOT NULL,\n"
                + "    fecha_ultima_actualizacion TEXT NOT NULL,\n"
                + "    rechazado_definitivamente TEXT DEFAULT 'N' CHECK (rechazado_definitivamente IN ('S', 'N')),\n"
                + "    observaciones_evaluacion TEXT,\n"
                + "    FOREIGN KEY (director_email) REFERENCES user(email) ON DELETE CASCADE,\n"
                + "    FOREIGN KEY (codirector_email) REFERENCES user(email) ON DELETE CASCADE,\n"
                + "    FOREIGN KEY (estudiante1_email) REFERENCES user(email) ON DELETE CASCADE,\n"
                + "    FOREIGN KEY (estudiante2_email) REFERENCES user(email) ON DELETE CASCADE\n"
                + ");";

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException ex) {
            Logger.getLogger(ProyectoGradoRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtiene conexión a la base de datos
     */
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:basedatos.db";
        return DriverManager.getConnection(url);
    }
}
