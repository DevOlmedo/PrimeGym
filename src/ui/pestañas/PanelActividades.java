package ui.pestañas;

import dao.ActividadDAO;
import model.Actividad;
import ui.dialogos.VentanaCargaActividad;
import ui.dialogos.VentanaDetalleActividad;
import ui.dialogos.DialogoExito;
import ui.dialogos.DialogoConfirmacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelActividades extends JPanel {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private ActividadDAO actividadDAO = new ActividadDAO();
    private List<Actividad> listaActividades;

    public PanelActividades() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        inicializarEncabezado();
        inicializarTabla();
        cargarActividades();
    }

    private void inicializarEncabezado() {
        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("GESTIÓN DE ACTIVIDADES Y CUPOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JButton btnNueva = crearBotonRojo("+ NUEVA ACTIVIDAD");
        btnNueva.addActionListener(e -> mostrarVentanaCarga(null));

        pnlNorte.add(titulo, BorderLayout.WEST);
        pnlNorte.add(btnNueva, BorderLayout.EAST);
        add(pnlNorte, BorderLayout.NORTH);
    }

    private void inicializarTabla() {
        String[] columnas = {"ID", "Actividad", "Instructor", "Días", "Horario", "Cupo Máx.", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        estilizarTabla();

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    Actividad seleccionada = listaActividades.get(fila);

                    if (e.getClickCount() == 2) {
                        new VentanaDetalleActividad((Frame) SwingUtilities.getWindowAncestor(PanelActividades.this), seleccionada).setVisible(true);
                    }

                    int columnaAcciones = 6;
                    if (tabla.columnAtPoint(e.getPoint()) == columnaAcciones) {
                        mostrarMenuAcciones(e, seleccionada, fila);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        add(scroll, BorderLayout.CENTER);
    }

    private void mostrarMenuAcciones(MouseEvent e, Actividad act, int fila) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        menu.setBackground(new Color(35, 35, 35));

        JMenuItem itemEditar = crearMenuItemModerno(" Editar Actividad");
        JMenuItem itemEliminar = crearMenuItemModerno(" Eliminar Actividad");

        itemEditar.addActionListener(al -> mostrarVentanaCarga(act));
        itemEliminar.addActionListener(al -> ejecutarEliminacion(fila));

        menu.add(itemEditar);
        menu.add(new JSeparator(JSeparator.HORIZONTAL));
        menu.add(itemEliminar);

        menu.show(tabla, e.getX(), e.getY());
    }

    private JMenuItem crearMenuItemModerno(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setOpaque(true);
        item.setBackground(new Color(35, 35, 35));
        item.setForeground(Color.WHITE);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setPreferredSize(new Dimension(160, 35));
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { item.setBackground(new Color(180, 0, 0)); }
            public void mouseExited(MouseEvent e) { item.setBackground(new Color(35, 35, 35)); }
        });
        return item;
    }

    private void ejecutarEliminacion(int fila) {
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = modeloTabla.getValueAt(fila, 1).toString();
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);

        DialogoConfirmacion diag = new DialogoConfirmacion(f, "¿Eliminar la clase de " + nombre + "?");
        diag.setVisible(true);

        if (diag.getRespuesta()) {
            if (actividadDAO.eliminarActividad(id)) {
                new DialogoExito(f, "ELIMINADO", "La actividad fue removida.").setVisible(true);
                cargarActividades();
            }
        }
    }

    public void cargarActividades() {
        modeloTabla.setRowCount(0);
        listaActividades = actividadDAO.obtenerTodas();

        for (Actividad a : listaActividades) {
            modeloTabla.addRow(new Object[]{
                    a.getId(),
                    a.getNombre().toUpperCase(),
                    a.getInstructorNombre(),
                    a.getDias(),
                    a.getHorario(),
                    a.getCupoMaximo(),
                    " GESTIONAR "
            });
        }
    }

    private void mostrarVentanaCarga(Actividad actExistente) {
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
        VentanaCargaActividad v = new VentanaCargaActividad(f, actExistente);
        v.setVisible(true);

        if (v.isConfirmado()) {
            boolean exito;
            if (actExistente == null) {

                // Registro de nueva actividad

                Actividad nueva = new Actividad(v.getNombre(), v.getInstructorId(), v.getCupo(), v.getHorario(), v.getDias());
                exito = actividadDAO.registrarActividad(nueva);
            } else {
                Actividad actualizada = new Actividad(actExistente.getId(), v.getNombre(), v.getInstructorId(), "", v.getCupo(), v.getHorario(), v.getDias());
                exito = actividadDAO.actualizarActividad(actualizada);
            }

            if (exito) {
                new DialogoExito(f, "ÉXITO", "Cambios guardados correctamente.").setVisible(true);
                cargarActividades();
            }
        }
    }

    private void estilizarTabla() {
        tabla.setBackground(new Color(35, 35, 35));
        tabla.setForeground(Color.WHITE);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.setRowHeight(45);
        tabla.setSelectionBackground(new Color(255, 140, 0, 100));
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(60, 60, 60));

        tabla.getTableHeader().setBackground(new Color(45, 45, 45));
        tabla.getTableHeader().setForeground(new Color(180, 0, 0));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(new Color(180, 0, 0));
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });
    }

    private JButton crearBotonRojo(String texto) {
        JButton btn = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = new Color(180, 0, 0);
                g2.setColor(getModel().isRollover() ? base.darker() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}