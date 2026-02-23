package ui.pestañas;

import dao.SocioDAO;
import ui.dialogos.DialogoEnvioWhatsapp;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelMorosos extends JPanel {
    private JTable tablaMorosos;
    private DefaultTableModel modeloTabla;
    private SocioDAO socioDAO = new SocioDAO();

    public PanelMorosos() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        inicializarEncabezado();
        inicializarTabla();
        cargarMorosos();
    }

    private void inicializarEncabezado() {
        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("SOCIOS CON DEUDA PENDIENTE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        JButton btnActualizar = crearBotonRojoPrime("ACTUALIZAR LISTA");
        btnActualizar.addActionListener(e -> cargarMorosos());

        pnlNorte.add(titulo, BorderLayout.WEST);
        pnlNorte.add(btnActualizar, BorderLayout.EAST);
        add(pnlNorte, BorderLayout.NORTH);
    }

    private void inicializarTabla() {
        String[] columnas = {"NOMBRE Y APELLIDO", "DNI", "VENCIMIENTO", "ATRASO (DÍAS)", "NOTIFICAR"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaMorosos = new JTable(modeloTabla);
        estilizarTablaMorosos(tablaMorosos);

        // Detecta el clic en la columna de WhatsApp

        tablaMorosos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tablaMorosos.getSelectedRow();
                int columna = tablaMorosos.columnAtPoint(e.getPoint());

                if (filaVista != -1 && columna == 4) {
                    int filaModelo = tablaMorosos.convertRowIndexToModel(filaVista);
                    prepararMensajeIndividual(filaModelo);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaMorosos);
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        JPanel pnlCentro = new JPanel(new BorderLayout());
        pnlCentro.setOpaque(false);
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));
        pnlCentro.add(scroll, BorderLayout.CENTER);

        add(pnlCentro, BorderLayout.CENTER);
    }

    private void prepararMensajeIndividual(int fila) {
        String nombre = modeloTabla.getValueAt(fila, 0).toString();
        String dias = modeloTabla.getValueAt(fila, 3).toString();

        List<Object[]> morosos = socioDAO.obtenerListaMorosos();
        String telefono = morosos.get(fila)[4].toString();

        String mensaje = "Hola " + nombre + ", te escribimos de PrimeGym. Notamos un atraso de " + dias +
                " en tu cuota. ¡Te esperamos para regularizar!";

        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoEnvioWhatsapp(f, "RECORDATORIO DE PAGO", mensaje, telefono).setVisible(true);
    }

    private void cargarMorosos() {
        modeloTabla.setRowCount(0);
        List<Object[]> morosos = socioDAO.obtenerListaMorosos();
        for (Object[] m : morosos) {
            // El emoji se reemplaza visualmente por el renderizador con imagen
            Object[] filaConBoton = new Object[]{m[0], m[1], m[2], m[3], " ENVIAR"};
            modeloTabla.addRow(filaConBoton);
        }
    }

    private void estilizarTablaMorosos(JTable t) {
        t.setBackground(new Color(30, 30, 30));
        t.setForeground(Color.WHITE);
        t.setRowHeight(40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setGridColor(new Color(60, 60, 60));
        t.setShowGrid(true);
        t.setSelectionBackground(new Color(180, 0, 0, 80));

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer renderWhatsapp = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(table.getBackground());
                    label.setForeground(new Color(37, 211, 102));
                }

                try {
                    ImageIcon iconoOriginal = new ImageIcon("src/assets/whatsapp.png");
                    Image imgEscalada = iconoOriginal.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(imgEscalada));
                    label.setText(value.toString());
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } catch (Exception e) {
                    label.setText("ENVIAR");
                }
                return label;
            }
        };

        for (int i = 0; i < t.getColumnCount() - 1; i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(centro);
        }
        t.getColumnModel().getColumn(4).setCellRenderer(renderWhatsapp);

        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(new Color(45, 45, 45));
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 40));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private JButton crearBotonRojoPrime(String texto) {
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
        btn.setPreferredSize(new Dimension(190, 45));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }
}