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
                + "fecha TEXT UNIQUE,"
                + "efectivo REAL,"
                + "mercado_pago REAL,"
                + "total REAL,"
                + "auto_cerrado INTEGER DEFAULT 0"
                + ");";

        // 5. Tabla de Instructores

        String sqlInstructores = "CREATE TABLE IF NOT EXISTS instructores ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "telefono TEXT,"
                + "edad TEXT,"
                + "email TEXT,"
                + "especialidad TEXT,"
                + "estado INTEGER DEFAULT 1"
                + ");";

        // 6. Tabla de Actividades con Gestión de Cupos

        String sqlActividades = "CREATE TABLE IF NOT EXISTS actividades ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "instructor_id INTEGER,"
                + "cupo_maximo INTEGER DEFAULT 20,"
                + "horario TEXT,"
                + "dias TEXT,"
                + "FOREIGN KEY (instructor_id) REFERENCES instructores(id)"
                + ");";

        // 7. Tabla de Inscripciones

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

            stmt.execute(sqlSocios);
            stmt.execute(sqlPagos);
            stmt.execute(sqlProductos);
            stmt.execute(sqlCierres);
            stmt.execute(sqlInstructores);
            stmt.execute(sqlActividades);
            stmt.execute(sqlInscripciones);

            System.out.println("✅ Base de datos lista: tablas verificadas (Socios, Pagos, Market, Cierres, Staff, Actividades, Inscripciones).");
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }
}