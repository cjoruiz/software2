// Archivo: co.unicauca.solid.access.UserRepository.java
package co.unicauca.solid.access;

import co.unicauca.solid.domain.Coordinador;
import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.utilities.security.PasswordValidator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository implements IUserRepository {

    public UserRepository() {
        initDatabase();
    }

    @Override
    public boolean save(Usuario newUser) {
        // Verificar si el usuario ya existe
        if (findByEmail(newUser.getEmail()) != null) {
            System.err.println("Error: El usuario con email " + newUser.getEmail() + " ya existe.");
            return false;
        }

        String sql = "INSERT INTO user (email, password, nombres, apellidos, celular, programa, rol, tipo_docente) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String hashedPassword = PasswordValidator.hashPassword(newUser.getPassword());
            pstmt.setString(1, newUser.getEmail());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, newUser.getNombres());
            pstmt.setString(4, newUser.getApellidos());
            pstmt.setString(5, newUser.getCelular());
            pstmt.setString(6, newUser.getPrograma());
            pstmt.setString(7, newUser.getRol());
            String tipoDocente = (newUser instanceof Docente) ? ((Docente) newUser).getTipoDocente() : null;
            pstmt.setString(8, tipoDocente);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean update(Usuario user) { // <-- NUEVO MÉTODO
        String sql = "UPDATE user SET password = ?, nombres = ?, apellidos = ?, celular = ?, programa = ?, rol = ?, tipo_docente = ? WHERE email = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String hashedPassword = PasswordValidator.hashPassword(user.getPassword());
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, user.getNombres());
            pstmt.setString(3, user.getApellidos());
            pstmt.setString(4, user.getCelular());
            pstmt.setString(5, user.getPrograma());
            pstmt.setString(6, user.getRol());

            String tipoDocente = (user instanceof Docente) ? ((Docente) user).getTipoDocente() : null;
            pstmt.setString(7, tipoDocente);

            pstmt.setString(8, user.getEmail()); // La clave primaria

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean validateLogin(String email, String password) {
        String hashedInput = PasswordValidator.hashPassword(password);
        String sql = "SELECT COUNT(*) FROM user WHERE email = ? AND password = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, hashedInput);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Usuario> list() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usuarios;
    }

    @Override
    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Usuario> findByRole(String role) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE rol = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usuarios;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        String rol = rs.getString("rol");
        Usuario usuario = null;

        switch (rol) {
            case "ESTUDIANTE":
                usuario = new Estudiante();
                break;
            case "COORDINADOR":
                usuario = new Coordinador();
                break;
            case "DOCENTE":
                Docente docente = new Docente();
                docente.setTipoDocente(rs.getString("tipo_docente"));
                usuario = docente;
                break;
            default:
                throw new SQLException("Rol de usuario no válido: " + rol);
        }

        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setCelular(rs.getString("celular"));
        usuario.setPrograma(rs.getString("programa"));

        return usuario;
    }

    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS user ("
                + "    email TEXT PRIMARY KEY,"
                + "    password TEXT NOT NULL,"
                + "    nombres TEXT NOT NULL,"
                + "    apellidos TEXT NOT NULL,"
                + "    celular TEXT,"
                + "    programa TEXT NOT NULL,"
                + "    rol TEXT NOT NULL CHECK (rol IN ('ESTUDIANTE', 'DOCENTE', 'COORDINADOR')), "
                + "    tipo_docente TEXT CHECK (tipo_docente IN ('PLANTA', 'OCASIONAL')) "
                + ");";

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:basedatos.db";
        return DriverManager.getConnection(url);
    }
}