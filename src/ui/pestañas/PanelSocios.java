package ui.pestañas;

import ui.dialogos.DialogoNuevoSocio;
import dao.SocioDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PanelSocios extends JPanel {

    private JTable tablaSocios;
    private DefaultTableModel modeloTabla;
    private SocioDAO socioDAO = new SocioDAO();

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

        // BOTÓN QUE ABRE EL MINI MENÚ

        JButton btnAcciones = new JButton("ACCIONES ▼");
        btnAcciones.setBackground(new Color(255, 140, 0));
        btnAcciones.setForeground(Color.WHITE);
        btnAcciones.setFocusPainted(false);
        btnAcciones.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAcciones.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // CREACIÓN DEL MINI MENÚ

        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        JMenuItem itemNuevo = crearItemMenu("Cargar Nuevo Socio");
        JMenuItem itemVer = crearItemMenu("Ver Todos (Actualizar)");
        JMenuItem itemBaja = crearItemMenu("Dar de Baja a un Socio");
        JMenuItem itemEditar = crearItemMenu("Editar Socio");

        menu.add(itemNuevo);
        menu.add(itemVer);
        menu.add(new JSeparator()); // Separador visual
        menu.add(itemBaja);
        menu.add(itemEditar);

        // --- LÓGICA DEL MENÚ ---

        btnAcciones.addActionListener(e -> menu.show(btnAcciones, 0, btnAcciones.getHeight()));

        itemNuevo.addActionListener(e -> {
            Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
            new DialogoNuevoSocio(f).setVisible(true);
            actualizarTabla();
        });

        itemVer.addActionListener(e -> actualizarTabla());

        itemBaja.addActionListener(e -> eliminarSocioSeleccionado());

        // Lógica para Editar Socio

        itemEditar.addActionListener(e -> editarSocioSeleccionado());

        panelNorte.add(titulo, BorderLayout.WEST);
        panelNorte.add(btnAcciones, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        // --- CUERPO DE LA TABLA ---

        JPanel panelCuerpo = new JPanel(new BorderLayout());
        panelCuerpo.setOpaque(false);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        modeloTabla = new DefaultTableModel(new String[]{"DNI", "Nombre", "Apellido", "Plan", "Vencimiento", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaSocios = new JTable(modeloTabla);
        configurarEstiloTabla();

        JScrollPane scroll = new JScrollPane(tablaSocios);
        scroll.getViewport().setBackground(new Color(30, 30, 30));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        panelCuerpo.add(scroll, BorderLayout.CENTER);
        add(panelCuerpo, BorderLayout.CENTER);

        actualizarTabla();
    }

    private void editarSocioSeleccionado() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un socio de la tabla.");
            return;
        }

        // Extrae los datos de la fila para pasárselos al diálogo

        Object[] datosSocio = {
                tablaSocios.getValueAt(fila, 0), // DNI (Integer)
                tablaSocios.getValueAt(fila, 1), // Nombre
                tablaSocios.getValueAt(fila, 2), // Apellido
                tablaSocios.getValueAt(fila, 3), // Plan
                tablaSocios.getValueAt(fila, 4)  // Vencimiento
        };

        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);

        // Llama al constructor especial de edición

        DialogoNuevoSocio diag = new DialogoNuevoSocio(f, datosSocio);
        diag.setVisible(true);
        actualizarTabla();
    }

    private void eliminarSocioSeleccionado() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un socio de la lista.");
            return;
        }
        int dni = (int) tablaSocios.getValueAt(fila, 0);
        int rta = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar al socio con DNI " + dni + "?",
                "Confirmar baja", JOptionPane.YES_NO_OPTION);
        if (rta == JOptionPane.YES_OPTION) {
            if (socioDAO.eliminarSocio(dni)) {
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Socio eliminado correctamente.");
            }
        }
    }

    private void configurarEstiloTabla() {
        tablaSocios.setBackground(new Color(45, 45, 45));
        tablaSocios.setForeground(Color.WHITE);
        tablaSocios.setRowHeight(35);
        tablaSocios.setSelectionBackground(new Color(255, 140, 0, 100));
        tablaSocios.getTableHeader().setBackground(new Color(60, 60, 60));
        tablaSocios.getTableHeader().setForeground(Color.WHITE);
        tablaSocios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (column == 5) { // Lógica de colores según estado
                    if ("DEUDA".equals(value)) {
                        c.setForeground(Color.RED);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(50, 205, 50));
                    }
                } else {
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };

        for (int i = 0; i < tablaSocios.getColumnCount(); i++) {
            tablaSocios.getColumnModel().getColumn(i).setCellRenderer(colorRenderer);
        }
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = socioDAO.obtenerTodos();
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    // Método auxiliar para crear items de menú con estilo oscuro

    private JMenuItem crearItemMenu(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setBackground(new Color(45, 45, 45));
        item.setForeground(Color.WHITE);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return item;
    }
}