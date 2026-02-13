package dao;

import model.Instructor;
import util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    public boolean registrarInstructor(Instructor ins) {
        String sql = "INSERT INTO instructores (nombre, telefono, edad, email, especialidad) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ins.getNombre());
            pstmt.setString(2, ins.getTelefono());
            pstmt.setString(3, ins.getEdad());
            pstmt.setString(4, ins.getEmail());
            pstmt.setString(5, ins.getEspecialidad());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar instructor: " + e.getMessage());
            return false;
        }
    }

    // Mapea todos los campos al modelo

    public List<Instructor> obtenerTodos() {
        List<Instructor> lista = new ArrayList<>();
        String sql = "SELECT * FROM instructores WHERE estado = 1 ORDER BY nombre ASC";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Instructor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("edad"),
                        rs.getString("email"),
                        rs.getString("especialidad")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar instructores: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarInstructor(Instructor ins) {
        String sql = "UPDATE instructores SET nombre=?, telefono=?, edad=?, email=?, especialidad=? WHERE id=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ins.getNombre());
            pstmt.setString(2, ins.getTelefono());
            pstmt.setString(3, ins.getEdad());
            pstmt.setString(4, ins.getEmail());
            pstmt.setString(5, ins.getEspecialidad());
            pstmt.setInt(6, ins.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar instructor: " + e.getMessage());
            return false;
        }
    }

    // Borrado lógico (cambia estado a 0 para no perder historial)

    public boolean eliminarInstructor(int id) {
        String sql = "UPDATE instructores SET estado = 0 WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar instructor: " + e.getMessage());
            return false;
        }
    }
}