package logic;

import model.Socio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ControlAcceso {
    public void verificarIngreso(Socio socio) {
        long diasPasados = ChronoUnit.DAYS.between(socio.getFechaVencimiento(), LocalDate.now());

        if (diasPasados > 90) {
            throw new AccesoDenegadoException("Socio dado de baja (+" + diasPasados + " días).");
        } else if (diasPasados > 0) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fecha = socio.getFechaVencimiento().format(formato);
            throw new AccesoDenegadoException("Cuota vencida el " + fecha + " (hace " + diasPasados + " días).");
        } else {
            System.out.println("¡Bienvenido " + socio.getNombre() + "!");

            // Aquí detectamos si está en los últimos 3 días antes de vencer
            if (diasPasados >= -7 && diasPasados < 0) {
                System.out.println("⚠️ ¡Atención! Tu cuota vence en " + Math.abs(diasPasados) + " días.");
            } else if (diasPasados == 0) {
                System.out.println("🚨 ¡Atención! Tu cuota vence HOY.");
            }
        }
    }
}
