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

    public static void crearTablas() {

        // TABLAS

        String sqlSocios = "CREATE TABLE IF NOT EXISTS socios ("
                + "dni INTEGER PRIMARY KEY,"
                + "nombre TEXT NOT NULL,"
                + "apellido TEXT,"
                + "plan TEXT,"
                + "telefono TEXT," // Campo vital para WhatsApp
                + "vencimiento TEXT,"
                + "cuota_al_dia INTEGER DEFAULT 1"
                + ");";

        String sqlPagos = "CREATE TABLE IF NOT EXISTS pagos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "socio_dni INTEGER,"
                + "monto REAL,"
                + "fecha TEXT,"
                + "metodo_pago TEXT,"
                + "FOREIGN KEY (socio_dni) REFERENCES socios(dni)"
                + ");";

        String sqlProductos = "CREATE TABLE IF NOT EXISTS productos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "descripcion TEXT,"
                + "precio REAL NOT NULL,"
                + "stock INTEGER NOT NULL,"
                + "ruta_imagen TEXT"
                + ");";

        String sqlCierres = "CREATE TABLE IF NOT EXISTS cierres_caja ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "fecha TEXT UNIQUE,"
                + "efectivo REAL,"
                + "mercado_pago REAL,"
                + "total REAL,"
                + "auto_cerrado INTEGER DEFAULT 0"
                + ");";

        String sqlInstructores = "CREATE TABLE IF NOT EXISTS instructores ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "telefono TEXT,"
                + "edad TEXT,"
                + "email TEXT,"
                + "especialidad TEXT,"
                + "estado INTEGER DEFAULT 1"
                + ");";

        String sqlActividades = "CREATE TABLE IF NOT EXISTS actividades ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "instructor_id INTEGER,"
                + "cupo_maximo INTEGER DEFAULT 20,"
                + "horario TEXT,"
                + "dias TEXT,"
                + "FOREIGN KEY (instructor_id) REFERENCES instructores(id)"
                + ");";

        String sqlInscripciones = "CREATE TABLE IF NOT EXISTS inscripciones ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "socio_dni INTEGER,"
                + "actividad_id INTEGER,"
                + "fecha_inscripcion TEXT,"
                + "FOREIGN KEY (socio_dni) REFERENCES socios(dni),"
                + "FOREIGN KEY (actividad_id) REFERENCES actividades(id)"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            // Habilitar claves foráneas

            stmt.execute("PRAGMA foreign_keys = ON;");

            // Ejecutar creación de tablas

            stmt.execute(sqlSocios);

            // --- PARCHE DE MIGRACIÓN

            try {
                stmt.execute("ALTER TABLE socios ADD COLUMN telefono TEXT;");
                System.out.println("✅ Columna 'telefono' integrada a la tabla existente.");
            } catch (SQLException e) {
                // Si entra aquí, es porque la columna ya existe. No imprimimos error.
            }

            stmt.execute(sqlPagos);
            stmt.execute(sqlProductos);
            stmt.execute(sqlCierres);
            stmt.execute(sqlInstructores);
            stmt.execute(sqlActividades);
            stmt.execute(sqlInscripciones);

            System.out.println("✅ Base de datos verificada y actualizada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error crítico al crear tablas: " + e.getMessage());
        }
    }
}