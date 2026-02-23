package util;

import java.awt.Desktop;
import java.net.URI;

public class WhatsappUtil {

     // Abre el navegador con el chat de WhatsApp listo para enviar.

    public static void enviarMensaje(String telefono, String mensaje) {
        try {
            // 1. Limpia el teléfono de espacios o guiones por seguridad

            String numLimpio = telefono.replaceAll("[^0-9]", "");

            // 2. Prepara el link oficial (wa.me) reemplazando espacios por el formato URL

            String url = "https://wa.me/" + numLimpio + "?text=" + mensaje.replace(" ", "%20");

            // 3. Abre el navegador predeterminado

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            System.err.println("Error al intentar abrir WhatsApp: " + e.getMessage());
        }
    }
}