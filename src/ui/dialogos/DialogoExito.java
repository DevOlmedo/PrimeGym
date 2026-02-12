package ui.dialogos;

import javax.swing.*;
import java.awt.*;

/*  **/

public class DialogoExito extends JDialog {

    // --- CONSTRUCTOR SIMPLE (Para Cierre de Caja, etc.) ---

    public DialogoExito(Frame parent, String titulo, String mensaje) {
        this(parent, "SISTEMA", "COMPLETADO", titulo, mensaje);
    }

    // --- CONSTRUCTOR PRIME ---

    public DialogoExito(Frame parent, String producto, String monto, String metodo, String socio) {
        super(parent, true);
        setUndecorated(true);
        setSize(420, 500);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 15, 15));
        panel.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 2));

        // --- ENCABEZADO ---

        JPanel pnlNorte = new JPanel();
        pnlNorte.setLayout(new BoxLayout(pnlNorte, BoxLayout.Y_AXIS));
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(35, 0, 10, 0));

        JLabel lblCheck = new JLabel("✓", SwingConstants.CENTER);
        lblCheck.setFont(new Font("Segoe UI Symbol", Font.BOLD, 85));
        lblCheck.setForeground(new Color(46, 204, 113));
        lblCheck.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Si el producto es "SISTEMA", muestra el título personalizado

        String textoTitulo = producto.equals("SISTEMA") ? monto : "VENTA REGISTRADA";
        JLabel lblTitulo = new JLabel(textoTitulo, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlNorte.add(lblCheck);
        pnlNorte.add(Box.createVerticalStrut(10));
        pnlNorte.add(lblTitulo);

        // --- CUERPO ---

        JPanel pnlCentro = new JPanel(new GridBagLayout());
        pnlCentro.setOpaque(false);
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        if (producto.equals("SISTEMA")) {

            // Versión simple para avisos

            gbc.gridy = 0;
            JLabel lblMsg = new JLabel("<html><center>" + socio + "</center></html>", SwingConstants.CENTER);
            lblMsg.setForeground(Color.WHITE);
            lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            pnlCentro.add(lblMsg, gbc);
        } else {

            // Versión detallada para ventas

            gbc.gridy = 0; pnlCentro.add(crearFilaInfo("PRODUCTO", producto), gbc);
            gbc.gridy = 1; gbc.insets = new Insets(15, 0, 0, 0); pnlCentro.add(crearFilaInfo("MONTO TOTAL", "$ " + monto), gbc);
            gbc.gridy = 2; pnlCentro.add(crearFilaInfo("MÉTODO DE PAGO", metodo), gbc);
            gbc.gridy = 3; pnlCentro.add(crearFilaInfo("IDENTIFICACIÓN", socio), gbc);
        }

        // --- BOTÓN ---

        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        pnlSur.setOpaque(false);

        JButton btnOk = crearBotonRojo("ENTENDIDO");
        btnOk.addActionListener(e -> dispose());

        pnlSur.add(btnOk);
        panel.add(pnlNorte, BorderLayout.NORTH);
        panel.add(pnlCentro, BorderLayout.CENTER);
        panel.add(pnlSur, BorderLayout.SOUTH);
        add(panel);
    }

    private JButton crearBotonRojo(String texto) {
        JButton btn = new JButton(texto) {
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
        btn.setPreferredSize(new Dimension(240, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel crearFilaInfo(String etiqueta, String valor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lblE = new JLabel(etiqueta);
        lblE.setForeground(new Color(130, 130, 130));
        lblE.setFont(new Font("Segoe UI", Font.BOLD, 10));
        JLabel lblV = new JLabel("<html><body style='width: 250px;'>" + valor + "</body></html>");
        lblV.setForeground(Color.WHITE);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(lblE, BorderLayout.NORTH);
        p.add(lblV, BorderLayout.CENTER);
        return p;
    }
}