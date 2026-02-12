package ui.dialogos;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DialogoAviso extends JDialog {

    public DialogoAviso(Frame parent, String mensaje) {
        super(parent, true);
        setUndecorated(true);
        setSize(380, 180);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(70, 70, 70));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);
        JLabel lblMensaje = new JLabel("<html><div style='text-align: center;'>" + mensaje + "</div></html>", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.WHITE);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        panelPrincipal.add(lblMensaje, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panelInferior.setOpaque(false);

        JButton btnOk = new JButton("ENTENDIDO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color rojoPrime = new Color(180, 0, 0);
                g2.setColor(getModel().isRollover() ? rojoPrime.darker() : rojoPrime);

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Estilo del botón

        btnOk.setPreferredSize(new Dimension(140, 40));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.setContentAreaFilled(false);
        btnOk.setBorderPainted(false);
        btnOk.setFocusPainted(false);

        btnOk.addActionListener(e -> dispose());

        panelInferior.add(btnOk);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
    }
}