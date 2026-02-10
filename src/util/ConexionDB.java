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

    // Crea y verifica la existencia de todas las tablas necesarias
    public static void crearTablas() {

        // 1. Tabla de Socios

        String sqlSocios = "CREATE TABLE IF NOT EXISTS socios ("
                + "dni INTEGER PRIMARY KEY,"
                + "nombre TEXT NOT NULL,"
                + "apellido TEXT,"
                + "plan TEXT,"
                + "vencimiento TEXT,"
                + "cuota_al_dia INTEGER DEFAULT 1"
                + ");";

        // 2. Tabla de Pagos

        String sqlPagos = "CREATE TABLE IF NOT EXISTS pagos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "socio_dni INTEGER,"
                + "monto REAL,"
                + "fecha TEXT,"
                + "metodo_pago TEXT,"
                + "FOREIGN KEY (socio_dni) REFERENCES socios(dni)"
                + ");";

        // 3. Tabla de Productos para el Market

        String sqlProductos = "CREATE TABLE IF NOT EXISTS productos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "descripcion TEXT,"
                + "precio REAL NOT NULL,"
                + "stock INTEGER NOT NULL,"
                + "ruta_imagen TEXT"
                + ");";

        // 4. Tabla de Cierres de Caja

        String sqlCierres = "CREATE TABLE IF NOT EXISTS cierres_caja ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "fecha TEXT UNIQUE," // UNIQUE evita dos cierres el mismo día
                + "efectivo REAL,"
                + "mercado_pago REAL,"
                + "total REAL,"
                + "auto_cerrado INTEGER DEFAULT 0" // 1 si fue por el timer, 0 si fue manual
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlSocios);
            stmt.execute(sqlPagos);
            stmt.execute(sqlProductos);
            stmt.execute(sqlCierres);

            System.out.println("✅ Base de datos lista: tablas verificadas (Socios, Pagos, Productos, Cierres).");
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }
}