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
 *
 * @author crist
 */

public class ProyectoGradoRepository implements IProyectoGradoRepository{
    
    public ProyectoGradoRepository() {
        initDatabase();
    }
    
    // Insertar proyecto de grado
    @Override
    public int insertarProyecto(ProyectoGrado proyecto) {
        String sql = "INSERT INTO proyectos_grado (titulo, modalidad, director_email, " +
                    "codirector_email, estudiante_email, objetivo_general, objetivos_especificos, " +
                    "estado_actual, numero_intento, fecha_creacion, fecha_ultima_actualizacion, " +
                    "rechazado_definitivamente) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, proyecto.getTitulo());
            pstmt.setString(2, proyecto.getModalidad());
            pstmt.setString(3, proyecto.getDirectorEmail());
            pstmt.setString(4, proyecto.getCodirectorEmail());
            pstmt.setString(5, proyecto.getEstudianteEmail());
            pstmt.setString(6, proyecto.getObjetivoGeneral());
            pstmt.setString(7, proyecto.getObjetivosEspecificos());
            pstmt.setString(8, proyecto.getEstadoActual());
            pstmt.setInt(9, proyecto.getNumeroIntento());
            pstmt.setTimestamp(10, Timestamp.valueOf(proyecto.getFechaCreacion()));
            pstmt.setTimestamp(11, Timestamp.valueOf(proyecto.getFechaUltimaActualizacion()));
            pstmt.setString(12, String.valueOf(proyecto.getRechazadoDefinitivamente()));
            
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
    
    // Obtener proyecto por ID
    @Override
    public ProyectoGrado obtenerProyectoPorId(int idProyecto) {
        String sql = "SELECT * FROM proyectos_grado WHERE id_proyecto = ?";
        ProyectoGrado proyecto = null;
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, idProyecto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    proyecto = mapearProyecto(rs);
                }
            }
            return proyecto;
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo proyecto: " + e.getMessage());
            return null;
        }
    }
    
    // Obtener proyectos por estudiante
    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail) {
        String sql = "SELECT * FROM proyectos_grado WHERE estudiante_email = ? ORDER BY fecha_creacion DESC";
        List<ProyectoGrado> proyectos = new ArrayList<>();
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
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
    
    // Obtener proyectos por director
    @Override
    public List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail) {
        String sql = "SELECT * FROM proyectos_grado WHERE director_email = ? ORDER BY fecha_creacion DESC";
        List<ProyectoGrado> proyectos = new ArrayList<>();
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
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
    
    // Obtener todos los proyectos
    @Override
    public List<ProyectoGrado> obtenerTodosProyectos() {
        String sql = "SELECT * FROM proyectos_grado ORDER BY fecha_creacion DESC";
        List<ProyectoGrado> proyectos = new ArrayList<>();
        
        try (Connection connection = getConnection(); 
             Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                proyectos.add(mapearProyecto(rs));
            }
            return proyectos;
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo todos los proyectos: " + e.getMessage());
            return proyectos;
        }
    }
    
    // Actualizar proyecto
    @Override
    public boolean actualizarProyecto(ProyectoGrado proyecto) {
        String sql = "UPDATE proyectos_grado SET titulo = ?, modalidad = ?, director_email = ?, " +
                    "codirector_email = ?, estudiante_email = ?, objetivo_general = ?, " +
                    "objetivos_especificos = ?, estado_actual = ?, numero_intento = ?, " +
                    "fecha_ultima_actualizacion = ?, rechazado_definitivamente = ? " +
                    "WHERE id_proyecto = ?";
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
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
            pstmt.setInt(12, proyecto.getIdProyecto());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando proyecto: " + e.getMessage());
            return false;
        }
    }
    
    // Actualizar estado del proyecto
    @Override
    public boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado) {
        String sql = "UPDATE proyectos_grado SET estado_actual = ?, fecha_ultima_actualizacion = ? " +
                    "WHERE id_proyecto = ?";
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
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
    
    // Incrementar número de intento
    @Override
    public boolean incrementarIntento(int idProyecto) {
        String sql = "UPDATE proyectos_grado SET numero_intento = numero_intento + 1, " +
                    "fecha_ultima_actualizacion = ? WHERE id_proyecto = ?";
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, idProyecto);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error incrementando intento: " + e.getMessage());
            return false;
        }
    }
    
    // Marcar como rechazado definitivamente
    @Override
    public boolean marcarRechazoDefinitivo(int idProyecto) {
        String sql = "UPDATE proyectos_grado SET rechazado_definitivamente = 'S', " +
                    "fecha_ultima_actualizacion = ? WHERE id_proyecto = ?";
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, idProyecto);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marcando rechazo definitivo: " + e.getMessage());
            return false;
        }
    }
    
    // Eliminar proyecto
    @Override
    public boolean eliminarProyecto(int idProyecto) {
        String sql = "DELETE FROM proyectos_grado WHERE id_proyecto = ?";
        
        try (Connection connection = getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, idProyecto);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error eliminando proyecto: " + e.getMessage());
            return false;
        }
    }
    
    // Método para mapear ResultSet a ProyectoGrado
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
    
    // Inicializar base de datos
    private void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS proyectos_grado (
                id_proyecto INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                modalidad TEXT NOT NULL CHECK (modalidad IN ('INVESTIGACION', 'PRACTICA_PROFESIONAL')),
                director_email TEXT NOT NULL,
                codirector_email TEXT,
                estudiante_email TEXT NOT NULL,
                objetivo_general TEXT,
                objetivos_especificos TEXT,
                estado_actual TEXT DEFAULT 'EN_PRIMERA_EVALUACION_FORMATO_A',
                numero_intento INTEGER DEFAULT 1 CHECK (numero_intento BETWEEN 1 AND 3),
                fecha_creacion TEXT NOT NULL,
                fecha_ultima_actualizacion TEXT NOT NULL,
                rechazado_definitivamente TEXT DEFAULT 'N' CHECK (rechazado_definitivamente IN ('S', 'N')),
                FOREIGN KEY (director_email) REFERENCES usuarios(email),
                FOREIGN KEY (codirector_email) REFERENCES usuarios(email),
                FOREIGN KEY (estudiante_email) REFERENCES usuarios(email)
            )
            """;
        
        try (Connection connection = getConnection(); 
             Statement stmt = connection.createStatement()) {
            
            stmt.execute(sql);
            
        } catch (SQLException ex) {
            Logger.getLogger(ProyectoGradoRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Método para obtener conexión
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:basedatos.db";
        return DriverManager.getConnection(url);
    }
}
