package dao;

import model.Actividad;
import util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    // Guarda la actividad vinculándola al ID del instructor

    public boolean registrarActividad(Actividad act) {
        String sql = "INSERT INTO actividades (nombre, instructor_id, cupo_maximo, horario, dias) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, act.getNombre());
            pstmt.setInt(2, act.getInstructorId());
            pstmt.setInt(3, act.getCupoMaximo());
            pstmt.setString(4, act.getHorario());
            pstmt.setString(5, act.getDias());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar actividad: " + e.getMessage());
            return false;
        }
    }

    // Usa un JOIN para traer el nombre del instructor

    public List<Actividad> obtenerTodas() {
        List<Actividad> lista = new ArrayList<>();

        // Se unen las tablas 'actividades' e 'instructores' por el ID

        String sql = "SELECT a.*, i.nombre AS nombre_instructor " +
                "FROM actividades a " +
                "JOIN instructores i ON a.instructor_id = i.id " +
                "ORDER BY a.nombre ASC";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Actividad(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("instructor_id"),
                        rs.getString("nombre_instructor"),
                        rs.getInt("cupo_maximo"),
                        rs.getString("horario"),
                        rs.getString("dias")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar actividades: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarActividad(Actividad act) {
        String sql = "UPDATE actividades SET nombre=?, instructor_id=?, cupo_maximo=?, horario=?, dias=? WHERE id=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, act.getNombre());
            pstmt.setInt(2, act.getInstructorId());
            pstmt.setInt(3, act.getCupoMaximo());
            pstmt.setString(4, act.getHorario());
            pstmt.setString(5, act.getDias());
            pstmt.setInt(6, act.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // Borra la actividad por su ID

    public boolean eliminarActividad(int id) {
        String sql = "DELETE FROM actividades WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar actividad: " + e.getMessage());
            return false;
        }
    }
}