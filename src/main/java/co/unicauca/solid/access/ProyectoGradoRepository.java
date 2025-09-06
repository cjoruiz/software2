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
                + "    titulo, modalidad, director_email, codirector_email, "
                + "    estudiante_email, objetivo_general, objetivos_especificos, "
                + "    estado_actual, numero_intento, fecha_creacion, "
                + "    fecha_ultima_actualizacion, rechazado_definitivamente,"
                + "    observaciones_evaluacion"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, proyecto.getTitulo());
            pstmt.setString(2, proyecto.getModalidad());
            pstmt.setString(3, proyecto.getDirectorEmail());
            pstmt.setString(4, proyecto.getCodirectorEmail());
            pstmt.setString(5, proyecto.getEstudianteEmail());
            pstmt.setString(6, proyecto.getObjetivoGeneral());
            pstmt.setString(7, proyecto.getObjetivosEspecificos());
            pstmt.setString(8, proyecto.getEstadoActual());
            pstmt.setInt(9, proyecto.getNumeroIntento());
            String fechaFormateada = proyecto.getFechaCreacion().toString().replace("T", " ");
            pstmt.setString(10, fechaFormateada);

            fechaFormateada = proyecto.getFechaUltimaActualizacion().toString().replace("T", " ");
            pstmt.setString(11, fechaFormateada);

            pstmt.setString(12, String.valueOf(proyecto.getRechazadoDefinitivamente()));
            pstmt.setString(13, proyecto.getObservacionesEvaluacion());

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
        String sql = "UPDATE proyectos_grado SET "
                + "    titulo = ?, modalidad = ?, director_email = ?, "
                + "    codirector_email = ?, estudiante_email = ?, "
                + "    objetivo_general = ?, objetivos_especificos = ?, "
                + "    estado_actual = ?, numero_intento = ?, "
                + "    fecha_ultima_actualizacion = ?, rechazado_definitivamente = ?,"
                + "    observaciones_evaluacion = ? "
                + "WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, proyecto.getTitulo());
            pstmt.setString(2, proyecto.getModalidad());
            pstmt.setString(3, proyecto.getDirectorEmail());
            pstmt.setString(4, proyecto.getCodirectorEmail());
            pstmt.setString(5, proyecto.getEstudianteEmail());
            pstmt.setString(6, proyecto.getObjetivoGeneral());
            pstmt.setString(7, proyecto.getObjetivosEspecificos());
            pstmt.setString(8, proyecto.getEstadoActual());
            pstmt.setInt(9, proyecto.getNumeroIntento());
            pstmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(11, String.valueOf(proyecto.getRechazadoDefinitivamente()));
            pstmt.setString(12, proyecto.getObservacionesEvaluacion());
            pstmt.setInt(13, proyecto.getIdProyecto());

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
        String sql = "UPDATE proyectos_grado SET "
                + "    estado_actual = ?, fecha_ultima_actualizacion = ? "
                + "WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
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
        proyecto.setEstudianteEmail(rs.getString("estudiante_email"));
        proyecto.setObjetivoGeneral(rs.getString("objetivo_general"));
        proyecto.setObjetivosEspecificos(rs.getString("objetivos_especificos"));
        proyecto.setEstadoActual(rs.getString("estado_actual"));
        proyecto.setNumeroIntento(rs.getInt("numero_intento"));
        proyecto.setObservacionesEvaluacion(rs.getString("observaciones_evaluacion"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            proyecto.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_ultima_actualizacion");
        if (fechaActualizacion != null) {
            proyecto.setFechaUltimaActualizacion(fechaActualizacion.toLocalDateTime());
        }

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
        String sql = "CREATE TABLE IF NOT EXISTS proyectos_grado ("
                + "    id_proyecto INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "    titulo TEXT NOT NULL,"
                + "    modalidad TEXT NOT NULL CHECK (modalidad IN ('INVESTIGACION', 'PRACTICA_PROFESIONAL')),"
                + "    director_email TEXT NOT NULL,"
                + "    codirector_email TEXT,"
                + "    estudiante_email TEXT NOT NULL,"
                + "    objetivo_general TEXT,"
                + "    objetivos_especificos TEXT,"
                + "    estado_actual TEXT DEFAULT 'EN_PRIMERA_EVALUACION_FORMATO_A' CHECK (estado_actual IN ("
                + "        'EN_PRIMERA_EVALUACION_FORMATO_A',"
                + "        'EN_SEGUNDA_EVALUACION_FORMATO_A', "
                + "        'EN_TERCERA_EVALUACION_FORMATO_A',"
                + "        'FORMATO_A_APROBADO',"
                + "        'FORMATO_A_RECHAZADO',"
                + "        'EN_EVALUACION_FORMATO_B',"
                + "        'FORMATO_B_RECHAZADO',"
                + "        'FORMATO_B_APROBADO',"
                + "        'EN_DESARROLLO',"
                + "        'EN_EVALUACION_FINAL',"
                + "        'APROBADO',"
                + "        'RECHAZADO_DEFINITIVO'"
                + "    )),"
                + "    numero_intento INTEGER DEFAULT 1 CHECK (numero_intento BETWEEN 1 AND 3),"
                + "    fecha_creacion TEXT NOT NULL,"
                + "    fecha_ultima_actualizacion TEXT NOT NULL,"
                + "    rechazado_definitivamente TEXT DEFAULT 'N' CHECK (rechazado_definitivamente IN ('S', 'N')),"
                + "    observaciones_evaluacion TEXT,"
                + "    FOREIGN KEY (director_email) REFERENCES usuarios(email),"
                + "    FOREIGN KEY (codirector_email) REFERENCES usuarios(email),"
                + "    FOREIGN KEY (estudiante_email) REFERENCES usuarios(email)"
                + ")";

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
