package co.unicauca.solid.access;

import co.unicauca.solid.domain.FilePG;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository para FilePG - Maneja únicamente la persistencia de documentos Se
 * enfoca en almacenar y recuperar archivos de diferentes tipos
 *
 * @author crist
 */
public class FilePGRepository implements IFilePGRepository {

    public FilePGRepository() {
        initDatabase();
    }

    @Override
    public int insertarDocumento(FilePG documento) {
        String sql = "INSERT INTO documentos_proyecto ("
                + "id_proyecto, tipo_documento, version, "
                + "contenido, nombre_archivo, extension, tamaño, "
                + "fecha_subida, estado, observaciones"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int maxReintentos = 3;
        int reintentos = 0;

        while (reintentos < maxReintentos) {
            try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // Obtener la siguiente versión si no está establecida
                if (documento.getVersion() == null) {
                    documento.setVersion(obtenerSiguienteVersion(documento.getIdProyecto(), documento.getTipoDocumento()));
                }

                pstmt.setInt(1, documento.getIdProyecto());
                pstmt.setString(2, documento.getTipoDocumento());
                pstmt.setInt(3, documento.getVersion());
                pstmt.setBytes(4, documento.getContenido());
                pstmt.setString(5, documento.getNombreArchivo());
                pstmt.setString(6, documento.getExtension());
                pstmt.setLong(7, documento.getTamaño());
                String fechaFormateada = documento.getFechaSubida().toString().replace("T", " ");
                pstmt.setString(8, fechaFormateada);
                pstmt.setString(9, documento.getEstado());
                pstmt.setString(10, documento.getObservaciones());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int idGenerado = rs.getInt(1);
                            // Marcar versiones anteriores como obsoletas
                            marcarVersionesAnterioresObsoletas(documento.getIdProyecto(),
                                    documento.getTipoDocumento(),
                                    documento.getVersion());
                            return idGenerado;
                        }
                    }
                }
                return -1;

            } catch (SQLException e) {
                reintentos++;
                System.err.println("Error insertando documento (reintento " + reintentos + "/" + maxReintentos + "): " + e.getMessage());

                if (reintentos >= maxReintentos) {
                    return -1;
                }

                if (e.getMessage().contains("locked") || e.getMessage().contains("SQLITE_BUSY")) {
                    try {
                        Thread.sleep(100 * reintentos);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * Obtiene documentos por proyecto y tipo
     */
    public List<FilePG> obtenerDocumentosPorProyectoYTipo(Integer idProyecto, String tipoDocumento) {
        String sql = "SELECT * FROM documentos_proyecto WHERE id_proyecto = ? AND tipo_documento = ? ORDER BY version DESC";
        List<FilePG> documentos = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            pstmt.setString(2, tipoDocumento);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    documentos.add(mapearDocumento(rs));
                }
            }
            return documentos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo documentos por proyecto y tipo: " + e.getMessage());
            return documentos;
        }
    }

    /**
     * Obtiene todos los documentos de un proyecto
     */
    public List<FilePG> obtenerDocumentosPorProyecto(Integer idProyecto) {
        String sql = "SELECT * FROM documentos_proyecto WHERE id_proyecto = ? ORDER BY tipo_documento, version DESC";
        List<FilePG> documentos = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    documentos.add(mapearDocumento(rs));
                }
            }
            return documentos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo documentos por proyecto: " + e.getMessage());
            return documentos;
        }
    }

    /**
     * Obtiene la última versión de un documento específico
     */
    public FilePG obtenerUltimaVersionDocumento(Integer idProyecto, String tipoDocumento) {
        String sql = "SELECT * FROM documentos_proyecto "
                + "WHERE id_proyecto = ? AND tipo_documento = ? "
                + "ORDER BY version DESC LIMIT 1";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            pstmt.setString(2, tipoDocumento);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearDocumento(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error obteniendo última versión: " + e.getMessage());
            return null;
        }
    }

    @Override
    public FilePG obtenerDocumentoPorId(int id) {
        String sql = "SELECT * FROM documentos_proyecto WHERE id_documento = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearDocumento(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error obteniendo documento: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<FilePG> obtenerTodosDocumentos() {
        String sql = "SELECT * FROM documentos_proyecto ORDER BY id_proyecto, tipo_documento, version DESC";
        List<FilePG> documentos = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documentos.add(mapearDocumento(rs));
            }
            return documentos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo todos los documentos: " + e.getMessage());
            return documentos;
        }
    }

    /**
     * Actualiza el estado de un documento
     */
    public boolean actualizarEstadoDocumento(int idDocumento, String nuevoEstado, String observaciones) {
        String sql = "UPDATE documentos_proyecto SET estado = ?, observaciones = ? WHERE id_documento = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setString(2, observaciones);
            pstmt.setInt(3, idDocumento);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando estado del documento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si existe un documento del tipo especificado para el proyecto
     */
    public boolean existeDocumentoTipo(Integer idProyecto, String tipoDocumento) {
        String sql = "SELECT COUNT(*) as total FROM documentos_proyecto WHERE id_proyecto = ? AND tipo_documento = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            pstmt.setString(2, tipoDocumento);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error verificando existencia de documento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la siguiente versión para un tipo de documento
     */
    private int obtenerSiguienteVersion(Integer idProyecto, String tipoDocumento) {
        String sql = "SELECT MAX(version) as max_version FROM documentos_proyecto WHERE id_proyecto = ? AND tipo_documento = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            pstmt.setString(2, tipoDocumento);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxVersion = rs.getInt("max_version");
                    return maxVersion + 1;
                }
            }
            return 1; // Primera versión

        } catch (SQLException e) {
            System.err.println("Error obteniendo siguiente versión: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Marca versiones anteriores como obsoletas cuando se sube una nueva
     * versión
     */
    private void marcarVersionesAnterioresObsoletas(Integer idProyecto, String tipoDocumento, Integer versionActual) {
        String sql = "UPDATE documentos_proyecto "
                + "SET estado = 'OBSOLETO' "
                + "WHERE id_proyecto = ? AND tipo_documento = ? "
                + "AND version < ? AND estado != 'OBSOLETO'";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            pstmt.setString(2, tipoDocumento);
            pstmt.setInt(3, versionActual);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error marcando versiones anteriores como obsoletas: " + e.getMessage());
        }
    }

    @Override
    public boolean eliminarDocumento(int id) {
        String sql = "DELETE FROM documentos_proyecto WHERE id_documento = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando documento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean guardarEnDisco(int id, String ruta, String nombreArchivo) {
        try {
            System.out.println("Intentando guardar - ID: " + id + ", Ruta: " + ruta + ", Archivo: " + nombreArchivo);

            FilePG documento = obtenerDocumentoPorId(id);
            if (documento == null) {
                System.err.println("Documento no encontrado para ID: " + id);
                return false;
            }

            if (documento.getContenido() == null) {
                System.err.println("El contenido del documento es null");
                return false;
            }

            // Crear carpeta si no existe
            File carpeta = new File(ruta);
            if (!carpeta.exists()) {
                System.out.println("Creando carpeta: " + carpeta.getAbsolutePath());
                if (!carpeta.mkdirs()) {
                    System.err.println("No se pudo crear la carpeta: " + carpeta.getAbsolutePath());
                    return false;
                }
            }

            // Verificar permisos de escritura
            if (!carpeta.canWrite()) {
                System.err.println("No hay permisos de escritura en: " + carpeta.getAbsolutePath());
                return false;
            }

            // Usar el nombre único generado si no se especifica uno
            if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
                nombreArchivo = documento.generarNombreUnico();
            }

            // Ruta completa
            String rutaCompleta = carpeta.getPath() + File.separator + nombreArchivo;
            File archivoFinal = new File(rutaCompleta);

            System.out.println("Ruta completa: " + archivoFinal.getAbsolutePath());

            try (FileOutputStream fos = new FileOutputStream(archivoFinal)) {
                fos.write(documento.getContenido());
                fos.flush();
            }

            if (archivoFinal.exists()) {
                System.out.println("Archivo guardado exitosamente: " + archivoFinal.getAbsolutePath());
                return true;
            } else {
                System.err.println("El archivo no se creó después de la escritura");
                return false;
            }

        } catch (SecurityException e) {
            System.err.println("Error de seguridad/permisos: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error guardando en disco: " + e.getMessage());
            return false;
        }
    }

    /**
     * Descargar documento con nombre personalizable
     */
    public boolean descargar(int id, String ruta, String nombreArchivo) {
        return guardarEnDisco(id, ruta, nombreArchivo);
    }

    /**
     * Mapea ResultSet a FilePG
     */
    private FilePG mapearDocumento(ResultSet rs) throws SQLException {
        FilePG documento = new FilePG();
        documento.setIdDocumento(rs.getInt("id_documento"));
        documento.setIdProyecto(rs.getInt("id_proyecto"));
        documento.setTipoDocumento(rs.getString("tipo_documento"));
        documento.setVersion(rs.getInt("version"));
        documento.setContenido(rs.getBytes("contenido"));
        documento.setNombreArchivo(rs.getString("nombre_archivo"));
        documento.setExtension(rs.getString("extension"));
        documento.setTamaño(rs.getLong("tamaño"));
        documento.setEstado(rs.getString("estado"));
        documento.setObservaciones(rs.getString("observaciones"));

        String fechaSubidaStr = rs.getString("fecha_subida");
        if (fechaSubidaStr != null) {
            documento.setFechaSubida(LocalDateTime.parse(fechaSubidaStr.replace(" ", "T")));
        }

        return documento;
    }

    /**
     * Inicializa la base de datos con la nueva estructura de documentos
     */
    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS documentos_proyecto ("
                + "    id_documento INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "    id_proyecto INTEGER NOT NULL, "
                + "    tipo_documento TEXT NOT NULL CHECK (tipo_documento IN ( "
                + "        'FORMATO_A', "
                + "        'CARTA_EMPRESA', "
                + "        'FORMATO_B', "
                + "        'MONOGRAFIA', "
                + "        'ANEXOS', "
                + "        'PRESENTACION' "
                + "    )), "
                + "    version INTEGER NOT NULL DEFAULT 1, "
                + "    contenido BLOB NOT NULL, "
                + "    nombre_archivo TEXT NOT NULL, "
                + "    extension TEXT, "
                + "    tamaño INTEGER DEFAULT 0, "
                + "    fecha_subida TEXT NOT NULL, "
                + "    estado TEXT DEFAULT 'PENDIENTE' CHECK (estado IN ( "
                + "        'PENDIENTE', "
                + "        'EN_REVISION', "
                + "        'APROBADO', "
                + "        'RECHAZADO', "
                + "        'OBSOLETO' "
                + "    )), "
                + "    observaciones TEXT, "
                + "    UNIQUE(id_proyecto, tipo_documento, version), "
                + "    FOREIGN KEY (id_proyecto) REFERENCES proyectos_grado(id_proyecto) ON DELETE CASCADE "
                + ")";

        // Crear índices para mejorar el rendimiento
        String[] indices = {
            "CREATE INDEX IF NOT EXISTS idx_documentos_proyecto ON documentos_proyecto(id_proyecto)",
            "CREATE INDEX IF NOT EXISTS idx_documentos_tipo ON documentos_proyecto(tipo_documento)",
            "CREATE INDEX IF NOT EXISTS idx_documentos_estado ON documentos_proyecto(estado)",
            "CREATE INDEX IF NOT EXISTS idx_documentos_version ON documentos_proyecto(id_proyecto, tipo_documento, version)"
        };

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {

            // Crear tabla principal
            stmt.execute(sql);

            // Crear índices
            for (String indice : indices) {
                try {
                    stmt.execute(indice);
                } catch (SQLException e) {
                    System.err.println("Error creando índice: " + e.getMessage());
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(FilePGRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtiene conexión a la base de datos
     */
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:basedatos.db";
        return DriverManager.getConnection(url);
    }

    /**
     * Métodos específicos para los tipos de documento según los requisitos
     */
    /**
     * Sube un nuevo Formato A
     */
    public int subirFormatoA(Integer idProyecto, byte[] contenido, String nombreArchivo) {
        FilePG formatoA = new FilePG(idProyecto, FilePG.TipoDocumento.FORMATO_A.getValor(),
                contenido, nombreArchivo);
        return insertarDocumento(formatoA);
    }

    /**
     * Sube una carta de empresa (requerida para práctica profesional)
     */
    public int subirCartaEmpresa(Integer idProyecto, byte[] contenido, String nombreArchivo) {
        FilePG cartaEmpresa = new FilePG(idProyecto, FilePG.TipoDocumento.CARTA_EMPRESA.getValor(),
                contenido, nombreArchivo);
        return insertarDocumento(cartaEmpresa);
    }

    /**
     * Verifica si un proyecto tiene carta de empresa subida
     */
    public boolean tieneCartaEmpresa(Integer idProyecto) {
        return existeDocumentoTipo(idProyecto, FilePG.TipoDocumento.CARTA_EMPRESA.getValor());
    }

    /**
     * Obtiene el formato A actual de un proyecto
     */
    public FilePG obtenerFormatoAActual(Integer idProyecto) {
        return obtenerUltimaVersionDocumento(idProyecto, FilePG.TipoDocumento.FORMATO_A.getValor());
    }

    /**
     * Obtiene la carta de empresa de un proyecto
     */
    public FilePG obtenerCartaEmpresa(Integer idProyecto) {
        return obtenerUltimaVersionDocumento(idProyecto, FilePG.TipoDocumento.CARTA_EMPRESA.getValor());
    }

    /**
     * Obtiene todos los formatos A de un proyecto (todas las versiones)
     */
    public List<FilePG> obtenerHistorialFormatoA(Integer idProyecto) {
        return obtenerDocumentosPorProyectoYTipo(idProyecto, FilePG.TipoDocumento.FORMATO_A.getValor());
    }

    /**
     * Valida que un proyecto de práctica profesional tenga carta de empresa
     */
    public boolean validarDocumentosRequeridos(Integer idProyecto, boolean esPracticaProfesional) {
        // Verificar que tenga formato A
        boolean tieneFormatoA = existeDocumentoTipo(idProyecto, FilePG.TipoDocumento.FORMATO_A.getValor());

        if (!tieneFormatoA) {
            return false;
        }

        // Si es práctica profesional, verificar carta de empresa
        if (esPracticaProfesional) {
            return tieneCartaEmpresa(idProyecto);
        }

        return true;
    }

    /**
     * Obtiene estadísticas de documentos por proyecto
     */
    public String obtenerEstadisticasProyecto(Integer idProyecto) {
        String sql = "SELECT tipo_documento, COUNT(*) as cantidad, MAX(version) as version_actual "
                + "FROM documentos_proyecto "
                + "WHERE id_proyecto = ? "
                + "GROUP BY tipo_documento";

        StringBuilder estadisticas = new StringBuilder();
        estadisticas.append("Estadísticas de documentos del proyecto ").append(idProyecto).append(":\n");

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo_documento");
                    int cantidad = rs.getInt("cantidad");
                    int versionActual = rs.getInt("version_actual");

                    estadisticas.append("- ").append(tipo).append(": ")
                            .append(cantidad).append(" versión(es), actual v")
                            .append(versionActual).append("\n");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo estadísticas: " + e.getMessage());
            return "Error obteniendo estadísticas del proyecto";
        }

        return estadisticas.toString();
    }
}
