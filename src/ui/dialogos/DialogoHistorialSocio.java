package ui.dialogos;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DialogoHistorialSocio extends JDialog {

    public DialogoHistorialSocio(Frame parent, String nombreSocio, List<Object[]> datos) {
        super(parent, true);
        setUndecorated(true); // Estética más limpia sin bordes de Windows
        setSize(600, 450);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(20, 20, 20)); // Negro profundo
        panelPrincipal.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));

        // Título Naranja Prime

        JLabel lblTitulo = new JLabel("HISTORIAL: " + nombreSocio.toUpperCase(), SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(255, 140, 0));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Configuración de la Tabla

        String[] columnas = {"FECHA", "CONCEPTO", "MÉTODO", "MONTO"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Object[] fila : datos) modelo.addRow(fila);

        JTable tabla = new JTable(modelo);
        estilizarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Botón Cerrar

        JButton btnCerrar = crearBotonRojo("CERRAR");
        btnCerrar.addActionListener(e -> dispose());

        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        pnlSur.setOpaque(false);
        pnlSur.add(btnCerrar);

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        panelPrincipal.add(pnlSur, BorderLayout.SOUTH);
        add(panelPrincipal);
    }

    private void estilizarTabla(JTable t) {
        t.setBackground(new Color(20, 20, 20));
        t.setForeground(new Color(220, 220, 220));
        t.setRowHeight(35);
        t.setShowGrid(true);
        t.setGridColor(new Color(50, 50, 50));
        t.setSelectionBackground(new Color(255, 140, 0, 100));
        t.setSelectionForeground(Color.WHITE);

        // Renderizador para que el texto no se pierda y esté centrado

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setOpaque(true);
                return this;
            }
        };

        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton crearBotonRojo(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(200, 0, 0) : new Color(150, 0, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}