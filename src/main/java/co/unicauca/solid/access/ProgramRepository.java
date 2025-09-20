/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.Programa;
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

/**
 *
 * @author crist
 */
public class ProgramRepository implements IProgramRepository {

    public ProgramRepository() {
        initDatabase();
    }

    // Insertar programa
    @Override
    public boolean insertarPrograma(Programa programa) {
        String sql = "INSERT INTO programas (id_programa, nombre_completo, facultad) VALUES (?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, programa.getIdPrograma());
            pstmt.setString(2, programa.getNombreCompleto());
            pstmt.setString(3, programa.getFacultad());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error insertando programa: " + e.getMessage());
            return false;
        }
    }

    // Obtener programa por ID
    @Override
    public Programa obtenerProgramaPorId(String idPrograma) {
        String sql = "SELECT * FROM programas WHERE id_programa = ?";
        Programa programa = null;

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, idPrograma);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    programa = mapearPrograma(rs);
                }
            }
            return programa;

        } catch (SQLException e) {
            System.err.println("Error obteniendo programa: " + e.getMessage());
            return null;
        }
    }

    // Obtener todos los programas
    @Override
    public List<Programa> obtenerTodosProgramas() {
        String sql = "SELECT * FROM programas ORDER BY id_programa";
        List<Programa> programas = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                programas.add(mapearPrograma(rs));
            }
            return programas;

        } catch (SQLException e) {
            System.err.println("Error obteniendo programas: " + e.getMessage());
            return programas;
        }
    }

    // Actualizar programa
    @Override
    public boolean actualizarPrograma(Programa programa) {
        String sql = "UPDATE programas SET nombre_completo = ?, facultad = ? WHERE id_programa = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, programa.getNombreCompleto());
            pstmt.setString(2, programa.getFacultad());
            pstmt.setString(3, programa.getIdPrograma());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando programa: " + e.getMessage());
            return false;
        }
    }

    // Eliminar programa
    @Override
    public boolean eliminarPrograma(String idPrograma) {
        String sql = "DELETE FROM programas WHERE id_programa = ?";

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, idPrograma);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando programa: " + e.getMessage());
            return false;
        }
    }

    // Método para mapear ResultSet a Programa
    private Programa mapearPrograma(ResultSet rs) throws SQLException {
        Programa programa = new Programa();
        programa.setIdPrograma(rs.getString("id_programa"));
        programa.setNombreCompleto(rs.getString("nombre_completo"));
        programa.setFacultad(rs.getString("facultad"));
        return programa;
    }

    // Inicializar base de datos con programas por defecto
    private void initDatabase() {
        String sqlProgramas = "CREATE TABLE IF NOT EXISTS programas ("
                + "    id_programa TEXT PRIMARY KEY,"
                + "    nombre_completo TEXT NOT NULL,"
                + "    facultad TEXT DEFAULT 'Facultad de Ingeniería Electrónica y Telecomunicaciones'"
                + ")";

        String sqlUsuarios = "ALTER TABLE user ADD COLUMN programa TEXT " // <-- ¡¡¡ CORREGIDO: 'user' en lugar de 'usuarios' !!!
                + "CHECK (programa IN ("
                + "    'INGENIERIA_SISTEMAS',"
                + "    'INGENIERIA_ELECTRONICA', "
                + "    'AUTOMATICA_INDUSTRIAL',"
                + "    'TECNOLOGIA_TELEMATICA'"
                + "))";

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            stmt.execute(sqlProgramas);
            try {
                stmt.execute(sqlUsuarios);
            } catch (SQLException e) {
                System.out.println("La columna programa ya existe en usuarios o no se pudo agregar: " + e.getMessage());
            }
            insertarProgramasPorDefecto();
        } catch (SQLException ex) {
            Logger.getLogger(ProgramRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Insertar programas por defecto
    private void insertarProgramasPorDefecto() {
        Programa.Programas[] programas = Programa.Programas.values();

        for (Programa.Programas programaEnum : programas) {
            Programa programa = new Programa(
                    programaEnum.getId(),
                    programaEnum.getNombreCompleto(),
                    "Facultad de Ingeniería Electrónica y Telecomunicaciones"
            );

            // Solo insertar si no existe
            if (obtenerProgramaPorId(programaEnum.getId()) == null) {
                insertarPrograma(programa);
            }
        }
    }

    // Método para obtener conexión
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:basedatos.db";
        return DriverManager.getConnection(url);
    }
}
