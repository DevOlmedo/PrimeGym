package model;

public class Actividad {
    private int id;
    private String nombre;
    private int instructorId;
    private String instructorNombre; // Para facilitar la visualización en la tabla
    private int cupoMaximo;
    private String horario;
    private String dias;

    // CONSTRUCTOR: Para crear una actividad nueva

    public Actividad(String nombre, int instructorId, int cupoMaximo, String horario, String dias) {
        this.nombre = nombre;
        this.instructorId = instructorId;
        this.cupoMaximo = cupoMaximo;
        this.horario = horario;
        this.dias = dias;
    }

    // CONSTRUCTOR: Para cargar desde la base de datos

    public Actividad(int id, String nombre, int instructorId, String instructorNombre, int cupoMaximo, String horario, String dias) {
        this.id = id;
        this.nombre = nombre;
        this.instructorId = instructorId;
        this.instructorNombre = instructorNombre;
        this.cupoMaximo = cupoMaximo;
        this.horario = horario;
        this.dias = dias;
    }

    // GETTERS

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getInstructorId() { return instructorId; }
    public String getInstructorNombre() { return instructorNombre; }
    public int getCupoMaximo() { return cupoMaximo; }
    public String getHorario() { return horario; }
    public String getDias() { return dias; }

    // SETTERS

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }
    public void setHorario(String horario) { this.horario = horario; }
    public void setDias(String dias) { this.dias = dias; }
}