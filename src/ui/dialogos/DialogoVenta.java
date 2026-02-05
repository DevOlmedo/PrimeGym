package ui.dialogos;

import model.Producto;
import javax.swing.*;
import java.awt.*;

public class DialogoVenta extends JDialog {
    private boolean respuesta = false;
    private JComboBox<String> comboMetodo; // Selector para evitar inconsistencias en estadísticas

    public DialogoVenta(Frame parent, Producto p) {
        super(parent, true);
        setUndecorated(true);
        setSize(400, 360);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));

        // --- Título ---

        JLabel lblTitulo = new JLabel("CONFIRMAR VENTA RÁPIDA", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(255, 140, 0));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // --- Cuerpo Central ---

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JLabel lblMsg = new JLabel("<html><center>¿Deseas registrar la venta de:<br><b style='font-size:16px; color:white;'>"
                + p.getNombre() + "</b></center></html>", SwingConstants.CENTER);
        lblMsg.setForeground(Color.LIGHT_GRAY);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrecio = new JLabel("$ " + String.format("%.2f", p.getPrecio()), SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblPrecio.setForeground(new Color(46, 204, 113));
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrecio.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        // --- Selector de Método de Pago ---

        JLabel lblMetodo = new JLabel("Método de pago:");
        lblMetodo.setForeground(Color.GRAY);
        lblMetodo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMetodo.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] opciones = {"Efectivo", "Mercado Pago"};
        comboMetodo = new JComboBox<>(opciones);
        comboMetodo.setMaximumSize(new Dimension(220, 35));
        comboMetodo.setBackground(new Color(45, 45, 45));
        comboMetodo.setForeground(Color.WHITE);
        comboMetodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboMetodo.setAlignmentX(Component.CENTER_ALIGNMENT);

        comboMetodo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        centro.add(lblMsg);
        centro.add(lblPrecio);
        centro.add(lblMetodo);
        centro.add(Box.createVerticalStrut(5));
        centro.add(comboMetodo);

        // --- Botones Inferiores ---

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        sur.setOpaque(false);

        JButton btnCance = crearBotonEstilo("CANCELAR", new Color(70, 70, 70));
        JButton btnAcept = crearBotonEstilo("ACEPTAR", new Color(180, 0, 0));

        btnCance.addActionListener(e -> { respuesta = false; dispose(); });
        btnAcept.addActionListener(e -> { respuesta = true; dispose(); });

        sur.add(btnCance);
        sur.add(btnAcept);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);
        add(panel);
    }

    private JButton crearBotonEstilo(String texto, Color bg) {
        JButton b = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setPreferredSize(new Dimension(140, 45));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return b;
    }

    // Getter para que el PanelMarket obtenga el método de pago seleccionado

    public String getMetodoSeleccionado() {
        return (String) comboMetodo.getSelectedItem();
    }

    public boolean getRespuesta() { return respuesta; }
}