package ui.pestañas;

import dao.SocioDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PanelMorosos extends JPanel {
    private JTable tablaMorosos;
    private DefaultTableModel modeloTabla;
    private SocioDAO socioDAO = new SocioDAO();

    public PanelMorosos() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        // --- ENCABEZADO ---

        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("SOCIOS CON DEUDA PENDIENTE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JButton btnActualizar = crearBotonRojoPrime("ACTUALIZAR LISTA");

        pnlNorte.add(titulo, BorderLayout.WEST);
        pnlNorte.add(btnActualizar, BorderLayout.EAST);
        add(pnlNorte, BorderLayout.NORTH);

        // --- TABLA CON BLOQUEO DE EDICIÓN ---

        modeloTabla = new DefaultTableModel(new String[]{"NOMBRE Y APELLIDO", "DNI", "VENCIMIENTO", "ATRASO (DÍAS)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMorosos = new JTable(modeloTabla);
        estilizarTablaMorosos(tablaMorosos);

        JScrollPane scroll = new JScrollPane(tablaMorosos);
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        JPanel pnlCentro = new JPanel(new BorderLayout());
        pnlCentro.setOpaque(false);
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));
        pnlCentro.add(scroll, BorderLayout.CENTER);

        add(pnlCentro, BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> cargarMorosos());
        cargarMorosos();
    }

    private void cargarMorosos() {
        modeloTabla.setRowCount(0);
        List<Object[]> morosos = socioDAO.obtenerListaMorosos();
        for (Object[] m : morosos) {
            modeloTabla.addRow(m);
        }
    }

    private void estilizarTablaMorosos(JTable t) {
        t.setBackground(new Color(30, 30, 30));
        t.setForeground(Color.WHITE);
        t.setRowHeight(35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setGridColor(new Color(60, 60, 60));
        t.setShowGrid(true);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(new Color(180, 0, 0, 80));
        t.setSelectionForeground(Color.WHITE); // Mantenemos el texto blanco

        t.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(45, 45, 45));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private JButton crearBotonRojoPrime(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color rojoPrime = new Color(180, 0, 0);
                if (getModel().isRollover()) {
                    g2.setColor(rojoPrime.darker());
                } else {
                    g2.setColor(rojoPrime);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(180, 45));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        return btn;
    }
}