package ui.pestañas;

import ui.dialogos.*;
import dao.SocioDAO;
import dao.PagoDAO;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelSocios extends JPanel {

    private JTable tablaSocios;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscador;
    private SocioDAO socioDAO = new SocioDAO();
    private PagoDAO pagoDAO = new PagoDAO();

    public PanelSocios() {
        setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());

        // --- ENCABEZADO ---

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);
        panelNorte.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel titulo = new JLabel("GESTIÓN DE SOCIOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        // --- BUSCADOR Y BOTÓN (LADO DERECHO) ---

        JPanel panelDerechoTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelDerechoTop.setOpaque(false);

        txtBuscador = new JTextField();
        txtBuscador.setPreferredSize(new Dimension(250, 35));
        txtBuscador.setBackground(new Color(45, 45, 45));
        txtBuscador.setForeground(Color.WHITE);
        txtBuscador.setCaretColor(Color.WHITE);
        txtBuscador.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        txtBuscador.setText("Buscar por Nombre o DNI...");
        txtBuscador.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        // Placeholder dinámico

        txtBuscador.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtBuscador.getText().equals("Buscar por Nombre o DNI...")) {
                    txtBuscador.setText("");
                    txtBuscador.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtBuscador.getText().isEmpty()) {
                    txtBuscador.setText("Buscar por Nombre o DNI...");
                    txtBuscador.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                }
            }
        });

        JButton btnAcciones = new JButton("ACCIONES ▼") {
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

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Configuraciones de estilo adicionales

        btnAcciones.setPreferredSize(new Dimension(140, 35));
        btnAcciones.setForeground(Color.WHITE);
        btnAcciones.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAcciones.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcciones.setContentAreaFilled(false); // Necesario para que se vea nuestro diseño redondeado
        btnAcciones.setBorderPainted(false);
        btnAcciones.setFocusPainted(false);

        panelDerechoTop.add(txtBuscador);
        panelDerechoTop.add(btnAcciones);

        // Lógica de filtrado en tiempo real

        txtBuscador.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
            private void filtrar() {
                String texto = txtBuscador.getText();
                if (texto.equals("Buscar por Nombre o DNI...") || texto.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        // MENÚ DESPLEGABLE

        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemNuevo = crearItemMenu("Cargar Nuevo Socio");
        JMenuItem itemVer = crearItemMenu("Ver Todos (Actualizar)");
        JMenuItem itemHistorial = crearItemMenu("Ver Historial de Pagos");
        JMenuItem itemEditar = crearItemMenu("Editar Socio");
        JMenuItem itemBaja = crearItemMenu("Dar de Baja a un Socio");

        menu.add(itemNuevo); menu.add(itemVer); menu.add(new JSeparator());
        menu.add(itemHistorial); menu.add(itemEditar); menu.add(itemBaja);

        btnAcciones.addActionListener(e -> menu.show(btnAcciones, 0, btnAcciones.getHeight()));
        itemNuevo.addActionListener(e -> {
            Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
            new DialogoNuevoSocio(f).setVisible(true);
            actualizarTabla();
        });
        itemVer.addActionListener(e -> actualizarTabla());
        itemHistorial.addActionListener(e -> abrirHistorialSeleccionado());
        itemEditar.addActionListener(e -> editarSocioSeleccionado()); // Corregido: Llamada al método
        itemBaja.addActionListener(e -> eliminarSocioSeleccionado()); // Corregido: Llamada al método

        panelNorte.add(titulo, BorderLayout.WEST);
        panelNorte.add(panelDerechoTop, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        // --- CUERPO TABLA ---

        JPanel panelCuerpo = new JPanel(new BorderLayout());
        panelCuerpo.setOpaque(false);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        modeloTabla = new DefaultTableModel(new String[]{"DNI", "Nombre", "Apellido", "Plan", "Vencimiento", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaSocios = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaSocios.setRowSorter(sorter);
        configurarEstiloTabla();

        tablaSocios.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaSocios.getSelectedRow() != -1) { abrirHistorialSeleccionado(); }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaSocios);
        scroll.getViewport().setBackground(new Color(30, 30, 30));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        panelCuerpo.add(scroll, BorderLayout.CENTER);
        add(panelCuerpo, BorderLayout.CENTER);

        actualizarTabla();
    }

    // --- MÉTODOS DE ACCIÓN ---

    private void abrirHistorialSeleccionado() {
        int filaVista = tablaSocios.getSelectedRow();
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);

        if (filaVista == -1) {
            new DialogoAviso(f, "⚠ Selecciona un socio de la tabla para ver su historial de pagos.").setVisible(true);
            return;
        }
        int filaModelo = tablaSocios.convertRowIndexToModel(filaVista);
        int dni = (int) modeloTabla.getValueAt(filaModelo, 0);
        String nombreSocio = modeloTabla.getValueAt(filaModelo, 1) + " " + modeloTabla.getValueAt(filaModelo, 2);

        List<Object[]> historial = pagoDAO.obtenerHistorialPorSocio(dni);
        new DialogoHistorialSocio(f, nombreSocio, historial).setVisible(true);
    }

    private void editarSocioSeleccionado() {
        int filaVista = tablaSocios.getSelectedRow();

        if (filaVista == -1) {
            Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
            new DialogoAviso(f, "⚠ Selecciona un socio de la tabla para poder editar sus datos.").setVisible(true);
            return;
        }
        int filaModelo = tablaSocios.convertRowIndexToModel(filaVista);

        Object[] datosSocio = {
                modeloTabla.getValueAt(filaModelo, 0),
                modeloTabla.getValueAt(filaModelo, 1),
                modeloTabla.getValueAt(filaModelo, 2),
                modeloTabla.getValueAt(filaModelo, 3),
                modeloTabla.getValueAt(filaModelo, 4)
        };

        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoNuevoSocio(f, datosSocio).setVisible(true);

        actualizarTabla();
    }

    private void eliminarSocioSeleccionado() {
        int filaVista = tablaSocios.getSelectedRow();
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);

        if (filaVista == -1) {
            new DialogoAviso(f, "⚠ Selecciona un socio de la tabla para darlo de baja.").setVisible(true);
            return;
        }

        int filaModelo = tablaSocios.convertRowIndexToModel(filaVista);
        String nombre = modeloTabla.getValueAt(filaModelo, 1) + " " + modeloTabla.getValueAt(filaModelo, 2);
        int dni = (int) modeloTabla.getValueAt(filaModelo, 0);

        DialogoConfirmacion diag = new DialogoConfirmacion(f,
                "¿Estás seguro de que deseas eliminar permanentemente a:<br><b>" + nombre + "</b> (DNI: " + dni + ")?");
        diag.setVisible(true);

        if (diag.getRespuesta()) {
            if (socioDAO.eliminarSocio(dni)) {
                actualizarTabla(); // Refrescamos la lista de socios

                // 5. Éxito: Mostramos el cartel grande con el check verde
                new DialogoExito(f, "BAJA COMPLETADA", "El socio ha sido removido del sistema correctamente.").setVisible(true);
            } else {
                // Error técnico
                new DialogoAviso(f, "❌ No se pudo eliminar al socio. Verifica si tiene pagos pendientes vinculados.").setVisible(true);
            }
        }
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = socioDAO.obtenerTodos();
        for (Object[] fila : datos) { modeloTabla.addRow(fila); }
    }

    private void configurarEstiloTabla() {
        tablaSocios.setBackground(new Color(45, 45, 45));
        tablaSocios.setForeground(Color.WHITE);
        tablaSocios.setRowHeight(35);
        tablaSocios.setSelectionBackground(new Color(255, 140, 0, 100));
        tablaSocios.getTableHeader().setBackground(new Color(60, 60, 60));
        tablaSocios.getTableHeader().setForeground(Color.WHITE);
        tablaSocios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (column == 5) {
                    if ("DEUDA".equals(value)) { c.setForeground(Color.RED); c.setFont(c.getFont().deriveFont(Font.BOLD)); }
                    else { c.setForeground(new Color(50, 205, 50)); }
                } else { c.setForeground(Color.WHITE); }
                return c;
            }
        };
        for (int i = 0; i < tablaSocios.getColumnCount(); i++) {
            tablaSocios.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JMenuItem crearItemMenu(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setBackground(new Color(45, 45, 45));
        item.setForeground(Color.WHITE);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return item;
    }
}