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
        String sqlSocios = "CREATE TABLE IF NOT EXISTS socios ("
                + "dni INTEGER PRIMARY KEY,"
                + "nombre TEXT NOT NULL,"
                + "apellido TEXT,"
                + "plan TEXT,"
                + "vencimiento TEXT,"
                + "cuota_al_dia INTEGER DEFAULT 1" // 1 = Al día, 0 = Deuda
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlSocios);
            System.out.println("✅ Base de datos lista y tabla 'socios' verificada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }
}