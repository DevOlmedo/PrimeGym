package dao;

import model.Producto;
import util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // Recupera todos los productos de la base de datos
    // Mapea cada fila al modelo Producto incluyendo su ID y Stock

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("ruta_imagen")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener productos: " + e.getMessage());
        }
        return lista;
    }

    // Registra una venta reduciendo el stock disponible

    public boolean reducirStock(int id, int cantidad) {
        // Solo actualiza si el stock resultante es mayor o igual a 0
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, id);
            pstmt.setInt(3, cantidad);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al reducir stock: " + e.getMessage());
            return false;
        }
    }

    // Guarda un nuevo producto en la tabla SQL

    public boolean guardar(Producto p) {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, stock, ruta_imagen) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getDescripcion());
            pstmt.setDouble(3, p.getPrecio());
            pstmt.setInt(4, p.getStock());
            pstmt.setString(5, p.getRutaImagen());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    // Actualiza la información de un producto existente usando su ID como referencia

    public boolean editar(Producto p) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ?, ruta_imagen = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getDescripcion());
            pstmt.setDouble(3, p.getPrecio());
            pstmt.setInt(4, p.getStock());
            pstmt.setString(5, p.getRutaImagen());
            pstmt.setInt(6, p.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al editar producto: " + e.getMessage());
            return false;
        }
    }

    // Elimina permanentemente un producto de la base de datos por su ID

    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}