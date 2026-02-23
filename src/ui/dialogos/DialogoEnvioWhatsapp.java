package ui.dialogos;

import util.WhatsappUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DialogoEnvioWhatsapp extends JDialog {
    private JTextArea txtMensaje;

    public DialogoEnvioWhatsapp(Frame parent, String titulo, String mensajePredeterminado, String telefonoSocio) {
        super(parent, true);
        setUndecorated(true);
        setSize(480, 400);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Borde verde WhatsApp

                g2.setColor(new Color(37, 211, 102));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };

        // --- ENCABEZADO ---

        JLabel lblTitulo = new JLabel(titulo.toUpperCase(), SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 15, 0));

        // --- CUERPO DE TEXTO ---

        txtMensaje = new JTextArea(mensajePredeterminado);
        txtMensaje.setBackground(new Color(45, 45, 45));
        txtMensaje.setForeground(Color.WHITE);
        txtMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setMargin(new Insets(15, 15, 15, 15));
        txtMensaje.setCaretColor(new Color(37, 211, 102));

        JScrollPane scroll = new JScrollPane(txtMensaje);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        // --- PANEL DE BOTONES ---

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 30));
        pnlBotones.setOpaque(false);

        JButton btnCancelar = crearBotonEstilizado("CANCELAR", new Color(70, 70, 70));
        JButton btnEnviar = crearBotonEstilizado("ENVIAR A WHATSAPP", new Color(37, 211, 102));

        // Acción de envío usando el Util

        btnEnviar.addActionListener(e -> {
            WhatsappUtil.enviarMensaje(telefonoSocio, txtMensaje.getText());
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());

        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnEnviar);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(pnlBotones, BorderLayout.SOUTH);
        add(panel);
    }

    private JButton crearBotonEstilizado(String t, Color bg) {
        JButton b = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setPreferredSize(new Dimension(170, 45));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }
}