package model;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String rutaImagen;

    // Constructor

    public Producto(int id, String nombre, String descripcion, double precio, int stock, String rutaImagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.rutaImagen = rutaImagen;
    }

    // --- Getters y Setters ---

    public int getId() {
        return id; }

    public void setId(int id) {
        this.id = id; }

    public int getStock() {
        return stock; }

    public void setStock(int stock) {
        this.stock = stock; }

    public String getNombre() {
        return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre; }

    public String getDescripcion() {
        return descripcion; }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion; }

    public double getPrecio() {
        return precio; }

    public void setPrecio(double precio) {
        this.precio = precio; }

    public String getRutaImagen() {
        return rutaImagen; }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen; }

    @Override
    public String toString() {
        return nombre + " - $" + precio;
    }
}