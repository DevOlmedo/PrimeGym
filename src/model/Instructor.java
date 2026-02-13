package model;

public class Instructor {
    private int id;
    private String nombre;
    private String telefono;
    private String edad;
    private String email;
    private String especialidad;

    // CONSTRUCTORES

    // Para crear un instructor nuevo (sin ID todavía)

    public Instructor(String nombre, String telefono, String edad, String email, String especialidad) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.edad = edad;
        this.email = email;
        this.especialidad = especialidad;
    }

    // Para cargar un instructor existente desde la Base de Datos

    public Instructor(int id, String nombre, String telefono, String edad, String email, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.edad = edad;
        this.email = email;
        this.especialidad = especialidad;
    }

    // GETTERS

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEdad() { return edad; }
    public String getEmail() { return email; }
    public String getEspecialidad() { return especialidad; }

    // SETTERS

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEdad(String edad) { this.edad = edad; }
    public void setEmail(String email) { this.email = email; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}