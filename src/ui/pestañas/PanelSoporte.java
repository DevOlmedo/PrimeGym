package ui.pestañas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class PanelSoporte extends JPanel {

    public PanelSoporte() {
        setBackground(new Color(25, 25, 25)); // Fondo Prime
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // --- CONTENEDOR CENTRAL ---

        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setOpaque(false);

        // Icono o Logo de Soporte

        JLabel lblIcono = new JLabel();
        try {
            ImageIcon iconoOriginal = new ImageIcon("src/assets/soporte_grande.png"); // Usa una imagen real
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lblIcono.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            lblIcono.setText("?"); // Fallback seguro
        }
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título

        JLabel lblTitulo = new JLabel("CENTRO DE ASISTENCIA TÉCNICA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Descripción

        JTextArea txtDesc = new JTextArea("¿Necesitas ayuda con PrimeGym?\nSi el sistema presenta errores o necesitas una nueva función, contacta directamente con tu desarrollador.");
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtDesc.setForeground(Color.GRAY);
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setHighlighter(null);
        txtDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtDesc.setMargin(new Insets(20, 0, 20, 0));

        // Botón de WhatsApp

        JButton btnWhatsapp = crearBotonSoporte("CONTACTAR POR WHATSAPP", new Color(37, 211, 102));
        btnWhatsapp.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnWhatsapp.addActionListener(e -> abrirEnlaceSoporte());

        // Información adicional

        JLabel lblVersion = new JLabel("Versión del Sistema: 2.0.1 - PrimeGym Gold Edition");
        lblVersion.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblVersion.setForeground(new Color(80, 80, 80));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ensamblado

        contenedor.add(lblIcono);
        contenedor.add(Box.createRigidArea(new Dimension(0, 20)));
        contenedor.add(lblTitulo);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));
        contenedor.add(txtDesc);
        contenedor.add(Box.createRigidArea(new Dimension(0, 30)));
        contenedor.add(btnWhatsapp);
        contenedor.add(Box.createRigidArea(new Dimension(0, 40)));
        contenedor.add(lblVersion);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(contenedor, gbc);
    }

    private JButton crearBotonSoporte(String texto, Color bg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setMaximumSize(new Dimension(300, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void abrirEnlaceSoporte() {
        try {

            String telefono = "2617039848";
            String mensaje = "Hola! Necesito soporte técnico para el sistema PrimeGym del local.";
            String url = "https://wa.me/" + telefono + "?text=" + mensaje.replace(" ", "%20");

            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir el navegador.");
        }
    }
}