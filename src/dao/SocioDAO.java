package dao;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {

    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Verifica si un DNI existe en la base de datos.
    // Utilizado por el Market para validar socios antes de registrar ventas.

    public boolean existeSocio(int dni) {
        String sql = "SELECT 1 FROM socios WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Retorna true si el DNI existe
            }
        } catch (SQLException e) {
            System.out.println("Error al validar existencia de socio: " + e.getMessage());
            return false;
        }
    }

    // Obtiene la lista detallada de socios con pagos vencidos

    public List<Object[]> obtenerListaMorosos() {
        List<Object[]> morosos = new ArrayList<>();
        List<Object[]> todos = obtenerTodos();
        LocalDate hoy = LocalDate.now();

        for (Object[] socio : todos) {
            if ("DEUDA".equals(socio[5])) {
                String vencimientoStr = (String) socio[4];
                long diasAtraso = 0;

                try {
                    LocalDate fechaVenc = LocalDate.parse(vencimientoStr, formateador);
                    diasAtraso = ChronoUnit.DAYS.between(fechaVenc, hoy);
                } catch (Exception e) {}

                morosos.add(new Object[]{
                        socio[1] + " " + socio[2],
                        socio[0],
                        vencimientoStr,
                        diasAtraso + " días"
                });
            }
        }
        return morosos;
    }

    // Calcula cuántos socios están activos y cuántos son deudores para el tablero

    public int[] obtenerConteoEstados() {
        int activos = 0;
        int deudores = 0;
        List<Object[]> todos = obtenerTodos();

        for (Object[] socio : todos) {
            String estado = (String) socio[5];
            if ("AL DÍA".equals(estado)) {
                activos++;
            } else if ("DEUDA".equals(estado)) {
                deudores++;
            }
        }
        return new int[]{activos, deudores};
    }

    // Renueva la membresía de un socio

    public boolean renovarSocio(int dni) {
        String sqlSelect = "SELECT vencimiento FROM socios WHERE dni = ?";
        String fechaVencimientoActualStr = null;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setInt(1, dni);
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                fechaVencimientoActualStr = rs.getString("vencimiento");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar fecha: " + e.getMessage());
            return false;
        }

        if (fechaVencimientoActualStr == null) return false;

        LocalDate nuevaFecha;
        try {
            LocalDate actual = LocalDate.parse(fechaVencimientoActualStr, formateador);
            LocalDate baseParaCalculo = LocalDate.now().isAfter(actual) ? LocalDate.now() : actual;

            nuevaFecha = baseParaCalculo.plusMonths(1);

            if (baseParaCalculo.getDayOfMonth() == baseParaCalculo.lengthOfMonth()) {
                nuevaFecha = nuevaFecha.withDayOfMonth(nuevaFecha.lengthOfMonth());
            }

        } catch (Exception e) {
            nuevaFecha = LocalDate.now().plusMonths(1);
        }

        String nuevaFechaStr = nuevaFecha.format(formateador);

        String sqlUpdate = "UPDATE socios SET vencimiento = ? WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
            pstmtUpdate.setString(1, nuevaFechaStr);
            pstmtUpdate.setInt(2, dni);
            return pstmtUpdate.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // Busca un socio por DNI y determina su estado en tiempo real

    public Object[] buscarPorDni(int dni) {
        String sql = "SELECT * FROM socios WHERE dni = ?";
        LocalDate hoy = LocalDate.now();

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String vencimientoStr = rs.getString("vencimiento");
                    String estadoVisual = "AL DÍA";

                    try {
                        LocalDate fechaVencimiento = LocalDate.parse(vencimientoStr, formateador);
                        if (hoy.isAfter(fechaVencimiento)) {
                            estadoVisual = "DEUDA";
                        }
                    } catch (Exception e) {
                        estadoVisual = "ERROR FECHA";
                    }

                    return new Object[]{
                            rs.getInt("dni"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("plan"),
                            vencimientoStr,
                            estadoVisual
                    };
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar socio: " + e.getMessage());
        }
        return null;
    }

    // --- MÉTODOS ABM ---

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

    public boolean editarSocio(int dni, String nombre, String apellido, String plan, String vencimiento) {
        String sql = "UPDATE socios SET nombre = ?, apellido = ?, plan = ?, vencimiento = ? WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, plan);
            pstmt.setString(4, vencimiento);
            pstmt.setInt(5, dni);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al editar: " + e.getMessage());
            return false;
        }
    }

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

    // Lista todos los socios con su estado calculado

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