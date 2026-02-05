package ui.pestañas;

import ui.dialogos.DialogoNuevoSocio;
import ui.dialogos.DialogoHistorialSocio; // Nuevo import
import dao.SocioDAO;
import dao.PagoDAO; // Nuevo import
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelSocios extends JPanel {

    private JTable tablaSocios;
    private DefaultTableModel modeloTabla;
    private SocioDAO socioDAO = new SocioDAO();
    private PagoDAO pagoDAO = new PagoDAO(); // Instancia para manejar el historial

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

        // BOTÓN ACCIONES

        JButton btnAcciones = new JButton("ACCIONES ▼");
        btnAcciones.setBackground(new Color(255, 140, 0));
        btnAcciones.setForeground(Color.WHITE);
        btnAcciones.setFocusPainted(false);
        btnAcciones.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAcciones.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // MENÚ DESPLEGABLE

        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        JMenuItem itemNuevo = crearItemMenu("Cargar Nuevo Socio");
        JMenuItem itemVer = crearItemMenu("Ver Todos (Actualizar)");
        JMenuItem itemHistorial = crearItemMenu("Ver Historial de Pagos"); // Opción nueva
        JMenuItem itemEditar = crearItemMenu("Editar Socio");
        JMenuItem itemBaja = crearItemMenu("Dar de Baja a un Socio");

        menu.add(itemNuevo);
        menu.add(itemVer);
        menu.add(new JSeparator());
        menu.add(itemHistorial); // Agregado al menú
        menu.add(itemEditar);
        menu.add(itemBaja);

        // --- LÓGICA DE ACCIONES ---

        btnAcciones.addActionListener(e -> menu.show(btnAcciones, 0, btnAcciones.getHeight()));

        itemNuevo.addActionListener(e -> {
            Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
            new DialogoNuevoSocio(f).setVisible(true);
            actualizarTabla();
        });

        itemVer.addActionListener(e -> actualizarTabla());

        itemHistorial.addActionListener(e -> abrirHistorialSeleccionado()); // Lógica historial

        itemBaja.addActionListener(e -> eliminarSocioSeleccionado());

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

        // LÓGICA DE DOBLE CLIC PARA HISTORIAL

        tablaSocios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaSocios.getSelectedRow() != -1) {
                    abrirHistorialSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaSocios);
        scroll.getViewport().setBackground(new Color(30, 30, 30));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        panelCuerpo.add(scroll, BorderLayout.CENTER);
        add(panelCuerpo, BorderLayout.CENTER);

        actualizarTabla();
    }

    // Lógica para obtener el historial del socio seleccionado y mostrar el diálogo

    private void abrirHistorialSeleccionado() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un socio de la tabla.");
            return;
        }

        int dni = (int) tablaSocios.getValueAt(fila, 0);
        String nombreSocio = tablaSocios.getValueAt(fila, 1) + " " + tablaSocios.getValueAt(fila, 2);

        // Obtiene los datos desde el PagoDAO

        List<Object[]> historial = pagoDAO.obtenerHistorialPorSocio(dni);

        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoHistorialSocio diag = new DialogoHistorialSocio(f, nombreSocio, historial);
        diag.setVisible(true);
    }

    private void editarSocioSeleccionado() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un socio de la tabla.");
            return;
        }

        Object[] datosSocio = {
                tablaSocios.getValueAt(fila, 0),
                tablaSocios.getValueAt(fila, 1),
                tablaSocios.getValueAt(fila, 2),
                tablaSocios.getValueAt(fila, 3),
                tablaSocios.getValueAt(fila, 4)
        };

        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
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
                if (column == 5) {
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

    private JMenuItem crearItemMenu(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setBackground(new Color(45, 45, 45));
        item.setForeground(Color.WHITE);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return item;
    }
}