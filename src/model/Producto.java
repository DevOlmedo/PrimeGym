package model;

public class Producto {
    private String nombre;
    private String descripcion;
    private double precio;
    private String rutaImagen;

    // Constructor

    public Producto(String nombre, String descripcion, double precio, String rutaImagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.rutaImagen = rutaImagen;
    }

    // --- Getters y Setters ---

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