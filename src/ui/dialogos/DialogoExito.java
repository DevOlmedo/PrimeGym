package ui.dialogos;

import javax.swing.*;
import java.awt.*;

public class DialogoExito extends JDialog {

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

        JLabel lblTitulo = new JLabel("VENTA REGISTRADA", SwingConstants.CENTER);
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

        gbc.gridy = 0; pnlCentro.add(crearFilaInfo("PRODUCTO", producto), gbc);
        gbc.gridy = 1; gbc.insets = new Insets(15, 0, 0, 0); pnlCentro.add(crearFilaInfo("MONTO TOTAL", "$ " + monto), gbc);
        gbc.gridy = 2; pnlCentro.add(crearFilaInfo("MÉTODO DE PAGO", metodo), gbc);
        gbc.gridy = 3; pnlCentro.add(crearFilaInfo("IDENTIFICACIÓN", socio), gbc);

        // --- BOTÓN ---

        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        pnlSur.setOpaque(false);

        JButton btnOk = new JButton("ENTENDIDO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color colorBase = new Color(255, 80, 80);

                // Lógica de Hover: si el mouse está encima, brilla

                if (getModel().isRollover()) {
                    g2.setColor(colorBase.brighter());
                } else {
                    g2.setColor(colorBase);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btnOk.setPreferredSize(new Dimension(240, 50));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnOk.setContentAreaFilled(false);
        btnOk.setBorderPainted(false);
        btnOk.setFocusPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Manito al pasar el mouse
        btnOk.addActionListener(e -> dispose());

        pnlSur.add(btnOk);

        panel.add(pnlNorte, BorderLayout.NORTH);
        panel.add(pnlCentro, BorderLayout.CENTER);
        panel.add(pnlSur, BorderLayout.SOUTH);
        add(panel);
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