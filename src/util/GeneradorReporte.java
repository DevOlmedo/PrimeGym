package logic;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import ui.dialogos.DialogoExito;
import ui.dialogos.DialogoAviso;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneradorReporte {

    public void generarCierreCaja(Frame parent, double efectivo, double mercadoPago) {

        // FileDialog para elegir dónde guardar el PDF

        FileDialog fd = new FileDialog(parent, "Guardar Cierre de Caja", FileDialog.SAVE);
        String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        fd.setFile("Cierre_Caja_" + fechaActual + ".pdf");
        fd.setVisible(true);

        if (fd.getFile() == null) return; // El usuario canceló

        String ruta = fd.getDirectory() + fd.getFile();

        try {
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            // --- DISEÑO DEL PDF ---

            // Título Principal

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.RED);
            Paragraph titulo = new Paragraph("REPORTE DE CIERRE DE CAJA - PRIMEGYM", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            // Información de Fecha

            Paragraph info = new Paragraph("Fecha: " + fechaActual + "\n\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 12));
            documento.add(info);

            // Tabla de Totales

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);

            // Encabezados

            tabla.addCell(crearCeldaEncabezado("MÉTODO DE PAGO"));
            tabla.addCell(crearCeldaEncabezado("MONTO TOTAL"));

            // Datos

            tabla.addCell("Efectivo");
            tabla.addCell("$ " + String.format("%.2f", efectivo));

            tabla.addCell("Mercado Pago");
            tabla.addCell("$ " + String.format("%.2f", mercadoPago));

            // Total Final

            PdfPCell celdaTotalLbl = new PdfPCell(new Phrase("TOTAL GENERAL",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            celdaTotalLbl.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(celdaTotalLbl);

            PdfPCell celdaTotalMonto = new PdfPCell(new Phrase("$ " + String.format("%.2f", (efectivo + mercadoPago)),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED)));
            celdaTotalMonto.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(celdaTotalMonto);

            documento.add(tabla);
            documento.close();

            // DialogoExito

            new DialogoExito(parent, "REPORTE GENERADO", "El archivo PDF se guardó correctamente.").setVisible(true);

        } catch (Exception e) {
            new DialogoAviso(parent, "❌ Error al generar el PDF: " + e.getMessage()).setVisible(true);
        }
    }

    private PdfPCell crearCeldaEncabezado(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        celda.setBackgroundColor(new BaseColor(180, 0, 0)); // Rojo Prime
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(8);
        return celda;
    }
}