package dao;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {

    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Método para insertar un nuevo socio
    public boolean guardarSocio(int dni, String nombre, String apellido, String plan, String vencimiento) {
        String sql = "INSERT INTO socios(dni, nombre, apellido, plan, vencimiento, cuota_al_dia) VALUES(?,?,?,?,?,1)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dni);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellido);
            pstmt.setString(4, plan);
            pstmt.setString(5, vencimiento);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar: " + e.getMessage());
            return false;
        }
    }

    // NUEVO: Método para editar los datos de un socio existente
    public boolean editarSocio(int dni, String nombre, String apellido, String plan, String vencimiento) {
        String sql = "UPDATE socios SET nombre = ?, apellido = ?, plan = ?, vencimiento = ? WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, plan);
            pstmt.setString(4, vencimiento);
            pstmt.setInt(5, dni); // El DNI se usa en el WHERE para identificar al socio

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al editar: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un socio (Dar de baja)
    public boolean eliminarSocio(int dni) {
        String sql = "DELETE FROM socios WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dni);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    // Método para listar todos los socios con cálculo de estado dinámico
    public List<Object[]> obtenerTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM socios";
        LocalDate hoy = LocalDate.now();

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String vencimientoStr = rs.getString("vencimiento");
                String estadoVisual = "AL DÍA";

                try {
                    LocalDate fechaVencimiento = LocalDate.parse(vencimientoStr, formateador);
                    // Si la fecha de hoy es posterior al vencimiento, marcamos deuda
                    if (hoy.isAfter(fechaVencimiento)) {
                        estadoVisual = "DEUDA";
                    }
                } catch (Exception e) {
                    estadoVisual = "ERROR FECHA";
                }

                lista.add(new Object[]{
                        rs.getInt("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("plan"),
                        vencimientoStr,
                        estadoVisual
                });
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener socios: " + e.getMessage());
        }
        return lista;
    }
}