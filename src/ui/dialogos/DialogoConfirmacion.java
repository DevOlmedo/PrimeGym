package ui.dialogos;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DialogoConfirmacion extends JDialog {
    private boolean respuesta = false;

    public DialogoConfirmacion(Frame parent, String mensaje) {
        super(parent, true);
        setUndecorated(true);
        setSize(380, 200); // Un poco más amplio para que el texto respire
        setLocationRelativeTo(parent);

        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel panelPrincipal = new JPanel(new BorderLayout(20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Borde gris sutil
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);

        JLabel lblMensaje = new JLabel("<html><div style='text-align: center;'>" + mensaje + "</div></html>", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.WHITE);
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));
        panelPrincipal.add(lblMensaje, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        panelBotones.setOpaque(false);
        JButton btnSi = crearBotonDialogo("ACEPTAR", new Color(180, 0, 0));
        JButton btnNo = crearBotonDialogo("CANCELAR", new Color(70, 70, 70));

        btnSi.addActionListener(e -> { respuesta = true; dispose(); });
        btnNo.addActionListener(e -> { respuesta = false; dispose(); });

        panelBotones.add(btnNo);
        panelBotones.add(btnSi);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JButton crearBotonDialogo(String texto, Color fondo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? fondo.darker() : fondo);

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public boolean getRespuesta() { return respuesta; }
}