package ui.pestañas;

import javax.swing.*;
import java.awt.*;

public class PanelActividades extends JPanel {
    public PanelActividades() {
        setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());

        // Título de la pestaña

        JLabel titulo = new JLabel("Actividades", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        add(titulo, BorderLayout.NORTH);



    }
}
