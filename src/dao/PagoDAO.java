package dao;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Registra un nuevo pago en la base de datos (Inscripciones y Market)

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

    // Detecta "Market (Efectivo)" y "Efectivo" mediante .contains()

    public double[] obtenerTotalesPorMetodo() {
        double efectivo = 0, mercadoPago = 0;
        String filtroMes = LocalDate.now().format(DateTimeFormatter.ofPattern("/MM/yyyy"));
        String sql = "SELECT monto, metodo_pago FROM pagos WHERE fecha LIKE ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + filtroMes);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String metodo = rs.getString("metodo_pago");
                double monto = rs.getDouble("monto");

                if (metodo != null) {

                    if (metodo.contains("Efectivo")) {
                        efectivo += monto;
                    } else if (metodo.contains("Mercado Pago")) {
                        mercadoPago += monto;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en totales por método: " + e.getMessage());
        }
        return new double[]{efectivo, mercadoPago};
    }

    // Calcula recaudación en un rango de fechas

    public double obtenerRecaudacionPorPeriodo(LocalDate inicio, LocalDate fin) {
        double total = 0;
        String sql = "SELECT monto, fecha FROM pagos";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String fechaTexto = rs.getString("fecha");
                if (fechaTexto != null) {
                    LocalDate fechaPago = LocalDate.parse(fechaTexto, formateador);

                    if ((fechaPago.isAfter(inicio) || fechaPago.isEqual(inicio)) &&
                            (fechaPago.isBefore(fin) || fechaPago.isEqual(fin))) {
                        total += rs.getDouble("monto");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error en recaudación por periodo: " + e.getMessage());
        }
        return total;
    }

    // Recupera los últimos pagos realizados para mostrar en el historial de la Caja

    public List<Object[]> obtenerUltimosPagos(int limite) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT socio_dni, monto, fecha, metodo_pago FROM pagos ORDER BY id DESC LIMIT ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("socio_dni"),
                        "$ " + String.format("%.2f", rs.getDouble("monto")),
                        rs.getString("fecha"),
                        rs.getString("metodo_pago")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener historial: " + e.getMessage());
        }
        return lista;
    }

    // Calcula el total recaudado específicamente el día de hoy

    public double obtenerBalanceHoy() {
        String hoy = LocalDate.now().format(formateador);
        String sql = "SELECT SUM(monto) FROM pagos WHERE fecha = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hoy);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.out.println("Error al obtener balance hoy: " + e.getMessage());
        }
        return 0.0;
    }
}