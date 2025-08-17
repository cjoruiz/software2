package co.unicauca.solid.access;

import co.unicauca.solid.domain.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
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

    private Connection conn;

    public UserRepository() {
        initDatabase();
    }

    @Override
    public boolean save(User newUser) {

        try {
            //Validate USER
            //if (newUser == null || newUser.getEmail()< 0 || newProduct.getName().isBlank()) {
//                return false;
//            }
            if (!isValidUser(newUser)) {
                return false;
            }
            //this.connect();
            String hashedPassword = hashPassword(newUser.getPassword());
            String sql = "INSERT INTO User (email, password, nombres, apellidos, celular, programa, rol) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newUser.getEmail());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, newUser.getNombres());
            pstmt.setString(4, newUser.getApellidos());
            pstmt.setString(5, newUser.getCelular());
            pstmt.setString(6, newUser.getPrograma());
            pstmt.setString(7, newUser.getRol());
            pstmt.executeUpdate();
            //this.disconnect();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<User> list() {

        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT * FROM User";
            //this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setNombres(rs.getString("nombres"));
                user.setApellidos(rs.getString("apellidos"));
                user.setCelular(rs.getString("celular"));
                user.setPrograma(rs.getString("programa"));
                user.setRol(rs.getString("rol"));
                users.add(user);
            }
            //this.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    private boolean isValidUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null
                || user.getNombres() == null || user.getApellidos() == null
                || user.getPrograma() == null || user.getRol() == null) {
            return false;
        }

        // Validar email institucional
        if (!user.getEmail().endsWith("@unicauca.edu.co")) {
            return false;
        }

        // Validar contraseña
        return isValidPassword(user.getPassword());
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasUpper = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }
        }

        return hasDigit && hasSpecial && hasUpper;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean validateLogin(String email, String password) {
        System.out.println(email + "SOLO");
        User user = findByEmail(email);
        System.out.println(password);
        if (user != null) {
            String hashedPassword = hashPassword(password);
            return hashedPassword.equals(user.getPassword());
        }
        return false;
    }

    private void initDatabase() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS User (\n"
                + "    email TEXT PRIMARY KEY,\n"
                + "    password TEXT NOT NULL,\n"
                + "    nombres TEXT NOT NULL,\n"
                + "    apellidos TEXT NOT NULL,\n"
                + "    celular TEXT,\n"
                + "    programa TEXT NOT NULL,\n"
                + "    rol TEXT NOT NULL\n"
                + ");";

        try {
            this.connect();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            //this.disconnect();

        } catch (SQLException ex) {
            Logger.getLogger(Provider.Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        // SQLite connection string
        //String url = "jdbc:sqlite:./mydatabase.db";
        String url = "jdbc:sqlite::memory:";

        try {
            conn = DriverManager.getConnection(url);

        } catch (SQLException ex) {
            Logger.getLogger(Provider.Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public User findByEmail(String email) {
        try {
            String sql = "SELECT * FROM User WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
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
                return user;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT * FROM User WHERE rol = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setNombres(rs.getString("nombres"));
                user.setApellidos(rs.getString("apellidos"));
                user.setCelular(rs.getString("celular"));
                user.setPrograma(rs.getString("programa"));
                user.setRol(rs.getString("rol"));
                users.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

}
