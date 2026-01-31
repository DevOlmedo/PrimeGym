package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static final String URL = "jdbc:sqlite:primegym.db";

    public static Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Error al conectar a SQLite: " + e.getMessage());
        }
        return conn;
    }

    // Este método crea las tablas si no existen

    public static void crearTablas() {

        // Tabla de Socios

        String sqlSocios = "CREATE TABLE IF NOT EXISTS socios ("
                + "dni INTEGER PRIMARY KEY,"
                + "nombre TEXT NOT NULL,"
                + "apellido TEXT,"
                + "plan TEXT,"
                + "vencimiento TEXT,"
                + "cuota_al_dia INTEGER DEFAULT 1"
                + ");";

        // Tabla de Pagos

        String sqlPagos = "CREATE TABLE IF NOT EXISTS pagos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "socio_dni INTEGER,"
                + "monto REAL,"
                + "fecha TEXT,"
                + "metodo_pago TEXT,"
                + "FOREIGN KEY (socio_dni) REFERENCES socios(dni)"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlSocios); //Ejecuta la tabla
            stmt.execute(sqlPagos);  //Ejecuta la tabla

            System.out.println("✅ Base de datos lista: tablas 'socios' y 'pagos' verificadas.");
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }
}