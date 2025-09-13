/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.ProyectoGrado;
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

    public ProyectoGradoRepository() {
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
            pstmt.setString(3, proyecto.getDirectorEmail());
            pstmt.setString(4, proyecto.getCodirectorEmail());
            pstmt.setString(5, proyecto.getEstudiante1Email());
            pstmt.setString(6, proyecto.getEstudiante2Email());
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
    public ProyectoGrado obtenerProyectoPorId(int idProyecto) {
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
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail) {
        String sql = "SELECT * FROM proyectos_grado WHERE estudiante_email = ? ORDER BY fecha_creacion DESC";
        List<ProyectoGrado> proyectos = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, estudianteEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    proyectos.add(mapearProyecto(rs));
                }
            }
            return proyectos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo proyectos por estudiante: " + e.getMessage());
            return proyectos;
        }
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail) {
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
            return proyectos;
        }
    }

    /**
     * Obtiene proyectos que están pendientes de evaluación por el coordinador
     */
    public List<ProyectoGrado> obtenerProyectosPendientesEvaluacion() {
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
            return proyectos;
        }
    }

    @Override
    public List<ProyectoGrado> obtenerTodosProyectos() {
        String sql = "SELECT * FROM proyectos_grado ORDER BY fecha_creacion DESC";
        List<ProyectoGrado> proyectos = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                proyectos.add(mapearProyecto(rs));
            }
            return proyectos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo todos los proyectos: " + e.getMessage());
            return proyectos;
        }
    }

    @Override
    public boolean actualizarProyecto(ProyectoGrado proyecto) {
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
            pstmt.setString(3, proyecto.getDirectorEmail());
            pstmt.setString(4, proyecto.getCodirectorEmail());
            pstmt.setString(5, proyecto.getEstudiante1Email());
            pstmt.setString(6, proyecto.getEstudiante2Email());
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
            return false;
        }
    }

    /**
     * Evalúa un formato A - Método específico para coordinadores
     */
    public boolean evaluarFormatoA(int idProyecto, boolean aprobado, String observaciones) {
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
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error evaluando formato A: " + e.getMessage());
            return false;
        }
    }

    /**
     * Procesa un reintento de formato A
     */
    public boolean procesarReintentoFormatoA(int idProyecto) {
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
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error procesando reintento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado) {
        String sql = 
                "UPDATE proyectos_grado SET "
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
            return false;
        }
    }

    @Override
    public boolean incrementarIntento(int idProyecto) {
        return procesarReintentoFormatoA(idProyecto);
    }

    @Override
    public boolean marcarRechazoDefinitivo(int idProyecto) {
        try (Connection connection = getConnection()) {
            ProyectoGrado proyecto = obtenerProyectoPorId(idProyecto);
            if (proyecto == null) {
                return false;
            }

            proyecto.marcarRechazoDefinitivo();
            return actualizarProyecto(proyecto);

        } catch (SQLException e) {
            System.err.println("Error marcando rechazo definitivo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarProyecto(int idProyecto) {
        String sql = "DELETE FROM proyectos_grado WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando proyecto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mapea ResultSet a ProyectoGrado
     */
     private ProyectoGrado mapearProyecto(ResultSet rs) throws SQLException {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setIdProyecto(rs.getInt("id_proyecto"));
        proyecto.setTitulo(rs.getString("titulo"));
        proyecto.setModalidad(rs.getString("modalidad"));
        proyecto.setDirectorEmail(rs.getString("director_email"));
        proyecto.setCodirectorEmail(rs.getString("codirector_email"));
        proyecto.setEstudiante1Email(rs.getString("estudiante1_email"));
        proyecto.setEstudiante2Email(rs.getString("estudiante2_email"));
        proyecto.setObjetivoGeneral(rs.getString("objetivo_general"));
        proyecto.setObjetivosEspecificos(rs.getString("objetivos_especificos"));
        proyecto.setEstadoActual(rs.getString("estado_actual"));
        proyecto.setNumeroIntento(rs.getInt("numero_intento"));
        proyecto.setObservacionesEvaluacion(rs.getString("observaciones_evaluacion"));

        // Formato ISO para parsear fechas
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Leer fecha_creacion como String
        String fechaCreacionStr = rs.getString("fecha_creacion");
        if (fechaCreacionStr != null && !fechaCreacionStr.trim().isEmpty()) {
            try {
                // Reemplazar espacio por 'T' para que coincida con el formato ISO
                String isoString = fechaCreacionStr.trim().replace(' ', 'T');
                proyecto.setFechaCreacion(LocalDateTime.parse(isoString, isoFormatter));
            } catch (DateTimeParseException e) {
                System.err.println("❌ Error al parsear fecha_creacion: '" + fechaCreacionStr + "'. Se usará la fecha actual.");
                proyecto.setFechaCreacion(LocalDateTime.now());
            }
        }

        // Leer fecha_ultima_actualizacion como String
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

        // Manejo de rechazo definitivo
        String rechazoDef = rs.getString("rechazado_definitivamente");
        if (rechazoDef != null && !rechazoDef.isEmpty()) {
            proyecto.setRechazadoDefinitivamente(rechazoDef.charAt(0));
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
