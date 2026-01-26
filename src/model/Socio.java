package model;

import java.time.LocalDate;

public class Socio {

    private String nombre;
    private String dni;
    private LocalDate fechaVencimiento;

    //CONSTRUCTOR
    public Socio(String nombre, String dni, LocalDate fechaVencimiento) {
        this.nombre = nombre;
        this.dni = dni;
        this.fechaVencimiento = fechaVencimiento;
    }

    //GETTERS
    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    //METODOS
    public boolean isCuotaAlDia() {
        // Si la fecha de vencimiento es DESPUÉS de ahora, está al día
        return fechaVencimiento.isAfter(LocalDate.now());
    }




}
