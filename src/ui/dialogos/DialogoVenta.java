package ui.dialogos;

import model.Producto;
import dao.SocioDAO;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DialogoVenta extends JDialog {
    private boolean respuesta = false;
    private JComboBox<String> comboMetodo;
    private JTextField txtDniSocio;

    public DialogoVenta(Frame parent, Producto p) {
        super(parent, true);
        setUndecorated(true);
        setSize(400, 480);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30)); // Fondo Dark
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(60, 60, 60)); // Borde sutil
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        // --- Título ---

        JLabel lblTitulo = new JLabel("CONFIRMAR VENTA RÁPIDA", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(180, 0, 0)); // Rojo Prime
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        // --- Cuerpo Central ---

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JLabel lblMsg = new JLabel("<html><center>¿Deseas registrar la venta de:<br><b style='font-size:15px; color:white;'>"
                + p.getNombre() + "</b></center></html>", SwingConstants.CENTER);
        lblMsg.setForeground(Color.LIGHT_GRAY);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrecio = new JLabel("$ " + String.format("%.2f", p.getPrecio()), SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblPrecio.setForeground(new Color(46, 204, 113)); // Verde para dinero
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrecio.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        // Metodo de pago y DNI (Ajuste visual)

        comboMetodo = crearComboBoxPrime(new String[]{"Efectivo", "Mercado Pago"});
        txtDniSocio = crearTextFieldPrime();

        centro.add(lblMsg);
        centro.add(lblPrecio);
        centro.add(crearEtiquetaGuia("Método de pago:"));
        centro.add(Box.createVerticalStrut(5));
        centro.add(comboMetodo);
        centro.add(Box.createVerticalStrut(15));
        centro.add(crearEtiquetaGuia("DNI Socio (Opcional):"));
        centro.add(Box.createVerticalStrut(5));
        centro.add(txtDniSocio);

        // Filtro de DNI

        ((AbstractDocument) txtDniSocio.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });

        // --- Botones Inferiores Unificados ---

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        sur.setOpaque(false);

        JButton btnCance = crearBotonPrime("CANCELAR", new Color(70, 70, 70));
        JButton btnAcept = crearBotonPrime("ACEPTAR", new Color(180, 0, 0));

        btnCance.addActionListener(e -> { respuesta = false; dispose(); });
        btnAcept.addActionListener(e -> {
            int dni = getDniIngresado();
            if (dni != 0 && !new SocioDAO().existeSocio(dni)) {
                new DialogoAviso((Frame)parent, "El DNI " + dni + " no existe. La venta será anónima.").setVisible(true);
                txtDniSocio.setText("");
            }
            respuesta = true;
            dispose();
        });

        sur.add(btnCance);
        sur.add(btnAcept);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);
        add(panel);
    }

    // --- MÉTODOS DE ESTILO ---

    private JButton crearBotonPrime(String texto, Color bg) {
        JButton b = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setPreferredSize(new Dimension(130, 40));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField crearTextFieldPrime() {
        JTextField t = new JTextField();
        t.setMaximumSize(new Dimension(240, 35));
        t.setBackground(new Color(45, 45, 45));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        return t;
    }

    private JComboBox<String> crearComboBoxPrime(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setMaximumSize(new Dimension(240, 35));
        cb.setBackground(new Color(45, 45, 45));
        cb.setForeground(Color.WHITE);
        cb.setFocusable(false);
        return cb;
    }

    private JLabel crearEtiquetaGuia(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.GRAY);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    public int getDniIngresado() {
        try {
            String texto = txtDniSocio.getText().trim();
            return texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException e) { return 0; }
    }

    public String getMetodoSeleccionado() { return (String) comboMetodo.getSelectedItem(); }
    public boolean getRespuesta() { return respuesta; }
}