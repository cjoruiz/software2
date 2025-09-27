/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.access;

/**
 *
 * @author crist
 */
import co.unicauca.solid.domain.MensajeInterno;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MensajeInternoRepository implements IMensajeInternoRepository {

    private static final Logger LOGGER = Logger.getLogger(MensajeInternoRepository.class.getName());

    public MensajeInternoRepository() {
        initDatabase();
    }

    @Override
    public int enviarMensaje(MensajeInterno mensaje) {
        String sql = "INSERT INTO mensajes_internos (remitente_email, destinatarios_email, asunto, cuerpo, documento_adjunto, nombre_archivo, fecha_envio, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, mensaje.getRemitenteEmail());
            pstmt.setString(2, mensaje.getDestinatariosEmail());
            pstmt.setString(3, mensaje.getAsunto());
            pstmt.setString(4, mensaje.getCuerpo());
            pstmt.setBytes(5, mensaje.getDocumentoAdjunto());
            pstmt.setString(6, mensaje.getNombreArchivo());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(7, mensaje.getFechaEnvio().format(formatter));
            pstmt.setString(8, mensaje.getEstado());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error al enviar mensaje: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<MensajeInterno> obtenerMensajesPorRemitente(String remitenteEmail) {
        String sql = "SELECT * FROM mensajes_internos WHERE remitente_email = ? ORDER BY fecha_envio DESC";
        return ejecutarConsultaLista(sql, remitenteEmail);
    }

    @Override
    public List<MensajeInterno> obtenerMensajesPorDestinatario(String destinatarioEmail) {
        String sql = "SELECT * FROM mensajes_internos WHERE ',' || REPLACE(destinatarios_email, ' ', '') || ',' LIKE ?";
        return ejecutarConsultaLista(sql, "%," + destinatarioEmail + ",%");
    }

    @Override
    public MensajeInterno obtenerMensajePorId(int idMensaje) {
        String sql = "SELECT * FROM mensajes_internos WHERE id_mensaje = ?";
        List<MensajeInterno> mensajes = ejecutarConsultaLista(sql, idMensaje);
        return mensajes.isEmpty() ? null : mensajes.get(0);
    }

    @Override
    public boolean marcarComoLeido(int idMensaje) {
        String sql = "UPDATE mensajes_internos SET estado = ? WHERE id_mensaje = ?";
        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, MensajeInterno.EstadoMensaje.LEIDO.getValor());
            pstmt.setInt(2, idMensaje);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error al marcar mensaje como leído: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarMensaje(int idMensaje) {
        String sql = "DELETE FROM mensajes_internos WHERE id_mensaje = ?";
        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idMensaje);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error al eliminar mensaje: " + e.getMessage());
            return false;
        }
    }

    private List<MensajeInterno> ejecutarConsultaLista(String sql, Object parametro) {
        List<MensajeInterno> mensajes = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (parametro instanceof String) {
                pstmt.setString(1, (String) parametro);
            } else if (parametro instanceof Integer) {
                pstmt.setInt(1, (Integer) parametro);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mensajes.add(mapearMensaje(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error en consulta de mensajes: " + e.getMessage());
        }
        return mensajes;
    }

    private MensajeInterno mapearMensaje(ResultSet rs) throws SQLException {
        MensajeInterno mensaje = new MensajeInterno();
        mensaje.setIdMensaje(rs.getInt("id_mensaje"));
        mensaje.setRemitenteEmail(rs.getString("remitente_email"));
        mensaje.setDestinatariosEmail(rs.getString("destinatarios_email"));
        mensaje.setAsunto(rs.getString("asunto"));
        mensaje.setCuerpo(rs.getString("cuerpo"));
        mensaje.setDocumentoAdjunto(rs.getBytes("documento_adjunto"));
        mensaje.setNombreArchivo(rs.getString("nombre_archivo"));

        String fechaStr = rs.getString("fecha_envio");
        if (fechaStr != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            mensaje.setFechaEnvio(LocalDateTime.parse(fechaStr, formatter));
        }

        mensaje.setEstado(rs.getString("estado"));
        return mensaje;
    }

    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS mensajes_internos ("
                + "id_mensaje INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "remitente_email TEXT NOT NULL, "
                + "destinatarios_email TEXT NOT NULL, "
                + "asunto TEXT NOT NULL, "
                + "cuerpo TEXT NOT NULL, "
                + "documento_adjunto BLOB, "
                + "nombre_archivo TEXT, "
                + "fecha_envio TEXT NOT NULL, "
                + "estado TEXT DEFAULT 'ENVIADO' CHECK (estado IN ('ENVIADO', 'LEIDO', 'RESPONDIDO')), "
                + "FOREIGN KEY (remitente_email) REFERENCES user(email) "
                + ");";

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.severe("Error al inicializar la tabla de mensajes: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:basedatos.db");
    }
}
