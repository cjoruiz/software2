package co.unicauca.solid.access;

import co.unicauca.solid.domain.User;
import co.unicauca.utilities.security.PasswordValidator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository implements IUserRepository {

    // ELIMINA la conexión global
    // private Connection conn;
    public UserRepository() {
        initDatabase();
    }

    @Override
    public boolean save(User newUser) {
        String sql = "INSERT INTO user (email, password, nombres, apellidos, celular, programa, rol, tipo_docente) " // <-- ¡Agregar tipo_docente!
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // <-- ¡Agregar un ? más!

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String hashedPassword = PasswordValidator.hashPassword(newUser.getPassword());
            pstmt.setString(1, newUser.getEmail());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, newUser.getNombres());
            pstmt.setString(4, newUser.getApellidos());
            pstmt.setString(5, newUser.getCelular());
            pstmt.setString(6, newUser.getPrograma());
            pstmt.setString(7, newUser.getRol());
            pstmt.setString(8, newUser.getTipoDocente()); // <-- ¡Guardar el tipo de docente!

            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean validateLogin(String email, String password) {
        User user = findByEmail(email);
        if (user != null) {
            String hashedPassword = PasswordValidator.hashPassword(password);
            return hashedPassword.equals(user.getPassword());
        }
        return false;
    }

    @Override
    public List<User> list() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setNombres(rs.getString("nombres"));
                user.setApellidos(rs.getString("apellidos"));
                user.setCelular(rs.getString("celular"));
                user.setPrograma(rs.getString("programa"));
                user.setRol(rs.getString("rol"));
                user.setTipoDocente(rs.getString("tipo_docente")); // Cargar el tipo de docente
                users.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setNombres(rs.getString("nombres"));
                user.setApellidos(rs.getString("apellidos"));
                user.setCelular(rs.getString("celular"));
                user.setPrograma(rs.getString("programa"));
                user.setRol(rs.getString("rol"));
                user.setTipoDocente(rs.getString("tipo_docente"));
                return user;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    // Método para obtener conexión (igual que LocalFileRepository)
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:basedatos.db";
        return DriverManager.getConnection(url);
    }

    @Override
    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE rol = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Primero establecer el parámetro
            pstmt.setString(1, role);

            // Luego ejecutar la consulta y obtener el ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setEmail(rs.getString("email"));
                    user.setNombres(rs.getString("nombres"));
                    user.setApellidos(rs.getString("apellidos"));
                    user.setCelular(rs.getString("celular"));
                    user.setPrograma(rs.getString("programa"));
                    user.setRol(rs.getString("rol"));
                    user.setTipoDocente(rs.getString("tipo_docente")); // Cargar el tipo de docente
                    users.add(user);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

}
