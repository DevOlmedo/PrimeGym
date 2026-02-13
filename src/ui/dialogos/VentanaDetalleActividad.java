package ui.dialogos;

import model.Actividad;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaDetalleActividad extends JDialog {

    public VentanaDetalleActividad(Frame parent, Actividad act) {
        super(parent, true);
        setUndecorated(true);
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 35, 35)); // Fondo oscuro Prime
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(180, 0, 0)); // Borde rojo
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };

        // --- Título ---

        JLabel lblTitulo = new JLabel("DETALLE DE LA CLASE", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));

        // --- Contenido ---

        JPanel pnlInfo = new JPanel(new GridLayout(0, 1, 0, 15));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        pnlInfo.add(crearItemInfo("ACTIVIDAD:", act.getNombre().toUpperCase()));
        pnlInfo.add(crearItemInfo("INSTRUCTOR:", act.getInstructorNombre()));
        pnlInfo.add(crearItemInfo("DÍAS:", act.getDias()));
        pnlInfo.add(crearItemInfo("HORARIO:", act.getHorario()));
        pnlInfo.add(crearItemInfo("CUPO MÁXIMO:", String.valueOf(act.getCupoMaximo())));

        // --- Botón Cerrar ---

        JPanel pnlInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        pnlInferior.setOpaque(false);
        JButton btnCerrar = crearBotonRojo("ENTENDIDO");
        btnCerrar.addActionListener(e -> dispose());
        pnlInferior.add(btnCerrar);

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(pnlInfo, BorderLayout.CENTER);
        panelPrincipal.add(pnlInferior, BorderLayout.SOUTH);
        add(panelPrincipal);
    }

    private JPanel crearItemInfo(String titulo, String valor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(new Color(180, 0, 0));
        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        v.setForeground(Color.WHITE);
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JButton crearBotonRojo(String texto) {
        JButton b = new JButton(texto);
        b.setPreferredSize(new Dimension(150, 40));
        b.setBackground(new Color(180, 0, 0));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}