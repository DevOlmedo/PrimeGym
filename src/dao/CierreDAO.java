package dao;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;

public class CierreDAO {

    // Verifica si el día ya tiene un registro de cierre en la base de datos

    public boolean yaEstaCerrado(LocalDate fecha) {
        String sql = "SELECT COUNT(*) FROM cierres_caja WHERE fecha = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fecha.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar cierre: " + e.getMessage());
        }
        return false;
    }

    // Recupera los datos de un cierre específico para mostrar en la pestaña de Reportes

    public Object[] obtenerCierrePorFecha(LocalDate fecha) {
        String sql = "SELECT efectivo, mercado_pago, total, auto_cerrado FROM cierres_caja WHERE fecha = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fecha.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Object[]{
                        rs.getDouble("efectivo"),
                        rs.getDouble("mercado_pago"),
                        rs.getDouble("total"),
                        rs.getInt("auto_cerrado") == 1 ? "Automático" : "Manual"
                };
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar cierre: " + e.getMessage());
        }
        return null;
    }

    // Ejecuta el proceso de cierre

    public void ejecutarCierre(LocalDate fecha, boolean esAutomatico) {

        PagoDAO pagoDAO = new PagoDAO();
        double[] totales = pagoDAO.obtenerTotalesPorMetodo(fecha, fecha);
        double efectivo = totales[0];
        double mercadoPago = totales[1];
        double totalDia = efectivo + mercadoPago;


        String sql = "INSERT OR REPLACE INTO cierres_caja (fecha, efectivo, mercado_pago, total, auto_cerrado) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fecha.toString());
            pstmt.setDouble(2, efectivo);
            pstmt.setDouble(3, mercadoPago);
            pstmt.setDouble(4, totalDia);
            pstmt.setInt(5, esAutomatico ? 1 : 0);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                String tipo = esAutomatico ? "Sistema (23:59)" : "Manual/Actualización";
                System.out.println("✅ Cierre procesado correctamente: " + tipo + " para " + fecha);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar/actualizar cierre de caja: " + e.getMessage());
        }
    }
}