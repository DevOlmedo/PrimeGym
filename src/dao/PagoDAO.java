package dao;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Registra un nuevo pago en la DB

    public boolean registrarPago(int dni, double monto, String metodo) {
        String sql = "INSERT INTO pagos (socio_dni, monto, fecha, metodo_pago) VALUES (?, ?, ?, ?)";
        String hoy = LocalDate.now().format(formateador);

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dni);
            pstmt.setDouble(2, monto);
            pstmt.setString(3, hoy);
            pstmt.setString(4, metodo);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar pago: " + e.getMessage());
            return false;
        }
    }


    // Recupera los últimos pagos realizados para mostrar en la tabla de la Caja.

    public List<Object[]> obtenerUltimosPagos(int limite) {
        List<Object[]> lista = new ArrayList<>();

        // Ordena por ID descendente para ver lo más nuevo primero

        String sql = "SELECT socio_dni, monto, fecha, metodo_pago FROM pagos ORDER BY id DESC LIMIT ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("socio_dni"),
                        "$ " + String.format("%.2f", rs.getDouble("monto")), // Formato moneda
                        rs.getString("fecha"),
                        rs.getString("metodo_pago")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener historial de pagos: " + e.getMessage());
        }
        return lista;
    }


    // Calcula el total de dinero recaudado en la fecha actual.

    public double obtenerBalanceHoy() {
        String hoy = LocalDate.now().format(formateador);
        String sql = "SELECT SUM(monto) FROM pagos WHERE fecha = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hoy);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener balance: " + e.getMessage());
        }
        return 0.0;
    }
}