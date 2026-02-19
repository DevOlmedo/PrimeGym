package ui.pestañas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelWhatsapp extends JPanel {

    public PanelWhatsapp() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        // TITULO

        JLabel titulo = new JLabel("CENTRO DE COMUNICACIÓN WHATSAPP");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        add(titulo, BorderLayout.NORTH);

        // Contenedor de Tarjetas

        JPanel gridTarjetas = new JPanel(new GridLayout(2, 3, 25, 25));
        gridTarjetas.setOpaque(false);
        gridTarjetas.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        gridTarjetas.add(crearTarjetaAccion("DIFUSIÓN MASIVA", "Envía un mensaje a todos los socios activos.", new Color(37, 211, 102)));
        gridTarjetas.add(crearTarjetaAccion("RECORDAR PAGOS", "Aviso automático para cuotas por vencer.", new Color(180, 0, 0)));
        gridTarjetas.add(crearTarjetaAccion("MENSAJE INDIVIDUAL", "Busca un socio y envíale un chat privado.", new Color(0, 120, 215)));
        gridTarjetas.add(crearTarjetaAccion("RECUPERAR MOROSOS", "Mensajes para socios que dejaron de venir.", new Color(255, 140, 0)));
        gridTarjetas.add(crearTarjetaAccion("CRONOGRAMA HOY", "Comparte las actividades del día.", new Color(155, 89, 182)));

        add(gridTarjetas, BorderLayout.CENTER);
    }

    private JPanel crearTarjetaAccion(String titulo, String desc, Color acento) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 35, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(acento);
                g2.fillRect(0, 0, 5, getHeight()); // Barra lateral de color
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Color.WHITE);
        lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea lblDesc = new JTextArea(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setEditable(false);
        lblDesc.setOpaque(false);
        lblDesc.setWrapStyleWord(true);
        lblDesc.setLineWrap(true);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(lblTit);
        card.add(Box.createVerticalStrut(10));
        card.add(lblDesc);
        card.add(Box.createVerticalGlue());

        // Efecto Hover

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(acento, 1)); }
            public void mouseExited(MouseEvent e) { card.setBorder(null); }
            public void mouseClicked(MouseEvent e) { ejecutarAccion(titulo); }
        });

        return card;
    }

    private void ejecutarAccion(String tipo) {
        System.out.println("Abriendo: " + tipo);
    }
}