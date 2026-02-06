package dao;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PagoDAO {

    // Usa este formateador solo para MOSTRAR, no para leer de la DB

    private final DateTimeFormatter formatoLatino = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // REGISTRAR PAGO (Guarda en YYYY-MM-DD para que el gráfico funcione)

    public boolean registrarPago(int dni, double monto, String metodo) {
        String sql = "INSERT INTO pagos (socio_dni, monto, fecha, metodo_pago) VALUES (?, ?, ?, ?)";
        String hoy = LocalDate.now().toString(); // Genera formato ISO (2026-02-06)

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

    // TOTALES POR MÉTODO

    public double[] obtenerTotalesPorMetodo(LocalDate inicio, LocalDate fin) {
        double efectivo = 0, mercadoPago = 0;
        String sql = "SELECT monto, fecha, metodo_pago FROM pagos";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String fechaTexto = rs.getString("fecha");
                if (fechaTexto != null) {
                    // Quitamos el formateador para evitar el error "at index 2"
                    LocalDate fechaPago = LocalDate.parse(fechaTexto);

                    if ((fechaPago.isAfter(inicio) || fechaPago.isEqual(inicio)) &&
                            (fechaPago.isBefore(fin) || fechaPago.isEqual(fin))) {

                        String metodo = rs.getString("metodo_pago");
                        double monto = rs.getDouble("monto");

                        if (metodo != null) {
                            if (metodo.contains("Efectivo")) efectivo += monto;
                            else if (metodo.contains("Mercado Pago")) mercadoPago += monto;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error en desglose: " + e.getMessage());
        }
        return new double[]{efectivo, mercadoPago};
    }

    // RECAUDACIÓN POR PERIODO

    public double obtenerRecaudacionPorPeriodo(LocalDate inicio, LocalDate fin) {
        double total = 0;
        String sql = "SELECT monto, fecha FROM pagos";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String fechaTexto = rs.getString("fecha");
                if (fechaTexto != null) {
                    // Cambio clave para eliminar errores de consola
                    LocalDate fechaPago = LocalDate.parse(fechaTexto);

                    if ((fechaPago.isAfter(inicio) || fechaPago.isEqual(inicio)) &&
                            (fechaPago.isBefore(fin) || fechaPago.isEqual(fin))) {
                        total += rs.getDouble("monto");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error en recaudación: " + e.getMessage());
        }
        return total;
    }

    // ÚLTIMOS PAGOS

    public List<Object[]> obtenerUltimosPagos(int limite) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT socio_dni, monto, fecha, metodo_pago FROM pagos ORDER BY id DESC LIMIT ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String fechaDB = rs.getString("fecha");
                String fechaLinda = (fechaDB != null) ?
                        LocalDate.parse(fechaDB).format(formatoLatino) : "";

                lista.add(new Object[]{
                        rs.getInt("socio_dni"),
                        "$ " + String.format("%.2f", rs.getDouble("monto")),
                        fechaLinda,
                        rs.getString("metodo_pago")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error en historial: " + e.getMessage());
        }
        return lista;
    }

    // BALANCE HOY

    public double obtenerBalanceHoy() {
        String hoy = LocalDate.now().toString(); // "2026-02-06"
        String sql = "SELECT SUM(monto) FROM pagos WHERE fecha = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hoy);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.out.println("Error balance hoy: " + e.getMessage());
        }
        return 0.0;
    }

    // HISTORIAL POR SOCIO (Muestra DD/MM/YYYY para el usuario)

    public List<Object[]> obtenerHistorialPorSocio(int dni) {
        List<Object[]> historial = new ArrayList<>();
        String sql = "SELECT monto, fecha, metodo_pago FROM pagos WHERE socio_dni = ? ORDER BY id DESC";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dni);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String metodoCompleto = rs.getString("metodo_pago");
                String fechaDB = rs.getString("fecha");
                String fechaLinda = (fechaDB != null) ?
                        LocalDate.parse(fechaDB).format(formatoLatino) : "";

                String concepto = "Cuota Gimnasio";
                String metodoPure = metodoCompleto;

                if (metodoCompleto.contains("Compra: ")) {
                    concepto = metodoCompleto.substring(metodoCompleto.indexOf(":") + 2, metodoCompleto.indexOf(" ("));
                    metodoPure = metodoCompleto.substring(metodoCompleto.indexOf("(") + 1, metodoCompleto.indexOf(")"));
                }

                historial.add(new Object[]{
                        fechaLinda,
                        concepto,
                        metodoPure,
                        "$ " + String.format("%.2f", rs.getDouble("monto"))
                });
            }
        } catch (SQLException e) {
            System.out.println("Error historial socio: " + e.getMessage());
        }
        return historial;
    }

    // INGRESOS MENSUALES (Para el gráfico anual)

    public Map<Integer, Double> obtenerIngresosMensuales() {
        Map<Integer, Double> datos = new HashMap<>();
        String sql = "SELECT strftime('%m', fecha) as mes, SUM(monto) as total " +
                "FROM pagos WHERE strftime('%Y', fecha) = strftime('%Y', 'now') " +
                "GROUP BY mes";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int mes = Integer.parseInt(rs.getString("mes"));
                datos.put(mes, rs.getDouble("total"));
            }
        } catch (Exception e) {
            System.out.println("Error en gráfico: " + e.getMessage());
        }
        return datos;
    }
}