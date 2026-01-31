package dao;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {

    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // 1. Si es un socio de "mitad de mes" (ej. día 10), se le suma 1 mes exacto (día 10 del próximo).
    // 2. Si es un socio de "fin de mes" (día 29, 30 o 31), se ajusta al último día del próximo mes.

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

            // Suma el mes de forma estándar (Eje: 14/01 -> 14/02)

            nuevaFecha = baseParaCalculo.plusMonths(1);

            // LÓGICA DE RECUPERACIÓN DE FIN DE MES
            // Si la fecha actual es el último día del mes (28, 29, 30 o 31)
            // forzamos que la nueva fecha TAMBIÉN sea el último día de su respectivo mes.
            // Esto hace que si estás en el 28 de Febrero, salte al 31 de Marzo.

            if (baseParaCalculo.getDayOfMonth() == baseParaCalculo.lengthOfMonth()) {
                nuevaFecha = nuevaFecha.withDayOfMonth(nuevaFecha.lengthOfMonth());
            }

            // Si era el día 14, como NO es el último día del mes (Enero tiene 31),
            // la condición de arriba es falsa y se queda en 14/02.

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

    // Busca un socio por DNI para el Control de Acceso.

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

    // --- MÉTODOS DE GESTIÓN (ABM) ---

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