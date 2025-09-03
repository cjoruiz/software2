package co.unicauca.solid.access;

import co.unicauca.solid.domain.FormatoA;
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
 *
 * @author crist
 */
public class FormatoARepository implements IFormatoARepository {

    public FormatoARepository() {
        initDatabase();
    }

    @Override
    public int insertarDocumento(FormatoA documento) {
        // MODIFICACIÓN: Quitar id_formato de la consulta SQL ya que es AUTOINCREMENT
        String sql = "INSERT INTO formatos_a (id_proyecto, numero_version, "
                + "archivo_pdf, nombre_archivo, tiene_carta_empresa, fecha_subida, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        int maxReintentos = 3;
        int reintentos = 0;

        while (reintentos < maxReintentos) {
            try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // MODIFICACIÓN: Quitar la línea que establece id_formato
                // pstmt.setInt(1, documento.getIdFormato()); // ← ESTA LÍNEA CAUSA EL ERROR
                // Ajustar los índices de los parámetros
                pstmt.setInt(1, documento.getIdProyecto());
                pstmt.setInt(2, documento.getNumeroVersion());
                pstmt.setBytes(3, documento.getContenido());
                pstmt.setString(4, documento.getNombre());
                pstmt.setString(5, String.valueOf(documento.getTieneCartaEmpresa()));
                String fechaFormateada = documento.getFechaSubida().toString().replace("T", " ");
                pstmt.setString(6, fechaFormateada);
                pstmt.setString(7, documento.getEstado());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1); // Retorna el ID generado automáticamente
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

    public List<FormatoA> obtenerFormatosAPorProyecto(Integer idProyecto) {
        String sql = "SELECT * FROM formatos_a WHERE id_proyecto = ? ORDER BY numero_version";
        List<FormatoA> formatos = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    formatos.add(mapearFormatoA(resultSet));
                }
                return formatos;
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo formatos A: " + e.getMessage());
            return formatos;
        }
    }

    // Obtener formato A por ID
    @Override
    public FormatoA obtenerDocumentoPorId(int id) {
        FormatoA formatoA = null;
        String sql = "SELECT * FROM formatos_a WHERE id_formato = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    formatoA = mapearFormatoA(rs);
                }
                return formatoA;
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo formato A: " + e.getMessage());
            return formatoA;
        }
    }

    // Obtener la última versión de un proyecto
    public FormatoA obtenerUltimaVersionPorProyecto(Integer idProyecto) {
        String sql = "SELECT * FROM formatos_a WHERE id_proyecto = ? ORDER BY numero_version DESC LIMIT 1";
        FormatoA formatoA = null;

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    formatoA = mapearFormatoA(rs);
                }
                return formatoA;
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo última versión: " + e.getMessage());
            return formatoA;
        }
    }

    // Obtener todos los formatos A
    @Override
    public List<FormatoA> obtenerTodosDocumentos() {
        String sql = "SELECT * FROM formatos_a ORDER BY id_proyecto, numero_version";
        List<FormatoA> formatos = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                formatos.add(mapearFormatoA(rs));
            }
            return formatos;

        } catch (SQLException e) {
            System.err.println("Error obteniendo formatos A: " + e.getMessage());
            return formatos;
        }
    }

    // Actualizar estado de un formato A
    public boolean actualizarEstadoFormatoA(int idFormato, String nuevoEstado) {
        String sql = "UPDATE formatos_a SET estado = ? WHERE id_formato = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idFormato);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando estado: " + e.getMessage());
            return false;
        }
    }

    // Verificar si un proyecto puede subir nueva versión
    public boolean puedeSubirNuevaVersion(Integer idProyecto) {
        String sql = "SELECT COUNT(*) as total FROM formatos_a WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalVersiones = rs.getInt("total");
                    return totalVersiones < 3;
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error verificando versiones: " + e.getMessage());
            return false;
        }
    }

    // Obtener el próximo número de versión para un proyecto
    public int obtenerProximaVersion(Integer idProyecto) {
        String sql = "SELECT MAX(numero_version) as max_version FROM formatos_a WHERE id_proyecto = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxVersion = rs.getInt("max_version");
                    return maxVersion + 1;
                }
            }
            return 1; // Primera versión

        } catch (SQLException e) {
            System.err.println("Error obteniendo próxima versión: " + e.getMessage());
            return 1;
        }
    }

    // Eliminar formato A
    @Override
    public boolean eliminarDocumento(int id) {
        String sql = "DELETE FROM formatos_a WHERE id_formato = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando formato A: " + e.getMessage());
            return false;
        }
    }

    // Método para mapear ResultSet a FormatoA
    private FormatoA mapearFormatoA(ResultSet rs) throws SQLException {
        FormatoA formatoA = new FormatoA();
        formatoA.setIdFormato(rs.getInt("id_formato"));
        formatoA.setIdProyecto(rs.getInt("id_proyecto"));
        formatoA.setNumeroVersion(rs.getInt("numero_version"));
        formatoA.setContenido(rs.getBytes("archivo_pdf"));
        formatoA.setNombre(rs.getString("nombre_archivo"));

        String cartaEmpresa = rs.getString("tiene_carta_empresa");
        if (cartaEmpresa != null && !cartaEmpresa.isEmpty()) {
            formatoA.setTieneCartaEmpresa(cartaEmpresa.charAt(0));
        }

        String fechaSubidaStr = rs.getString("fecha_subida");
        if (fechaSubidaStr != null) {
            formatoA.setFechaSubida(LocalDateTime.parse(fechaSubidaStr.replace(" ", "T")));
        }

        formatoA.setEstado(rs.getString("estado"));

        return formatoA;
    }

    // Guardar formato A en disco
    @Override
    public boolean guardarEnDisco(int id, String ruta, String nombreArchivo) {
        try {
            System.out.println("Intentando guardar - ID: " + id + ", Ruta: " + ruta + ", Archivo: " + nombreArchivo);

            FormatoA formatoA = obtenerDocumentoPorId(id);
            if (formatoA == null) {
                System.err.println("Formato A no encontrado para ID: " + id);
                return false;
            }

            if (formatoA.getContenido() == null) {
                System.err.println("El contenido del formato A es null");
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

            // Ruta completa
            String rutaCompleta = carpeta.getPath() + File.separator + nombreArchivo;
            File archivoFinal = new File(rutaCompleta);

            System.out.println("Ruta completa: " + archivoFinal.getAbsolutePath());

            try (FileOutputStream fos = new FileOutputStream(archivoFinal)) {
                fos.write(formatoA.getContenido());
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

    // Descargar formato A
    public boolean descargar(int id, String ruta, String nombreArchivo) {
        FormatoA formatoA = obtenerDocumentoPorId(id);
        if (formatoA == null) {
            return false;
        }

        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            if (!carpeta.mkdirs()) {
                return false;
            }
        }

        String rutaCompleta = carpeta.getPath() + File.separator + nombreArchivo;

        try (FileOutputStream fos = new FileOutputStream(rutaCompleta)) {
            fos.write(formatoA.getContenido());
            System.out.println("PDF guardado en: " + new File(rutaCompleta).getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Error guardando en disco: " + e.getMessage());
            return false;
        }
    }

    // Inicializar base de datos
    private void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS formatos_a (
                id_formato INTEGER PRIMARY KEY AUTOINCREMENT,
                id_proyecto INTEGER NOT NULL,
                numero_version INTEGER NOT NULL,
                archivo_pdf BLOB NOT NULL,
                nombre_archivo TEXT NOT NULL,
                tiene_carta_empresa CHAR(1) DEFAULT 'N' CHECK (tiene_carta_empresa IN ('S', 'N')),
                fecha_subida TEXT NOT NULL,
                estado TEXT DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO')),
                UNIQUE(id_proyecto, numero_version),
                FOREIGN KEY (id_proyecto) REFERENCES proyectos_grado(id_proyecto)
            )
            """;

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException ex) {
            Logger.getLogger(FormatoARepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para obtener conexión
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:basedatos.db";
        return DriverManager.getConnection(url);
    }
}
