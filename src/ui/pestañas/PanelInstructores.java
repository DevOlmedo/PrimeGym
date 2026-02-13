package ui.pestañas;

import dao.InstructorDAO;
import model.Instructor;
import ui.dialogos.VentanaCargaInstructor;
import ui.dialogos.DialogoExito;
import ui.dialogos.DialogoConfirmacion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelInstructores extends JPanel {
    private JPanel contenedorTarjetas;
    private InstructorDAO instructorDAO = new InstructorDAO();

    public PanelInstructores() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        inicializarEncabezado();
        inicializarCuerpo();
        cargarInstructores();
    }

    private void inicializarEncabezado() {
        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("STAFF DE INSTRUCTORES");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        // Botón principal

        JButton btnNuevo = crearBotonRojo(" + AGREGAR INSTRUCTOR ");
        btnNuevo.addActionListener(e -> mostrarVentanaCarga(null));

        pnlNorte.add(titulo, BorderLayout.WEST);
        pnlNorte.add(btnNuevo, BorderLayout.EAST);
        add(pnlNorte, BorderLayout.NORTH);
    }

    private void inicializarCuerpo() {

        JPanel panelCentrado = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        panelCentrado.setOpaque(false);

        // El '0' en filas significa "tantas como sean necesarias"

        contenedorTarjetas = new JPanel(new GridLayout(0, 3, 25, 25));
        contenedorTarjetas.setOpaque(false);

        panelCentrado.add(contenedorTarjetas);

        JScrollPane scroll = new JScrollPane(panelCentrado);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        // Forzamos a que solo haga scroll vertical

        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    public void cargarInstructores() {
        contenedorTarjetas.removeAll();
        List<Instructor> lista = instructorDAO.obtenerTodos();

        for (Instructor ins : lista) {
            contenedorTarjetas.add(crearTarjetaInstructor(ins));
        }

        contenedorTarjetas.revalidate();
        contenedorTarjetas.repaint();
    }

    private JPanel crearTarjetaInstructor(Instructor ins) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 35, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(180, 0, 0, 100));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };

        card.setMaximumSize(new Dimension(250, 250));
        card.setPreferredSize(new Dimension(250, 250));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);

        JLabel lblAvatar = new JLabel(ins.getNombre().substring(0, 1).toUpperCase());
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 50));
        lblAvatar.setForeground(new Color(180, 0, 0));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nombre

        JLabel lblNombre = new JLabel(ins.getNombre().toUpperCase());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Especialidad

        JLabel lblEsp = new JLabel(ins.getEspecialidad());
        lblEsp.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblEsp.setForeground(new Color(180, 0, 0));
        lblEsp.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Info (Edad y Email)

        String infoTexto = ins.getEdad() + " años  |  " + ins.getEmail();
        JLabel lblInfo = new JLabel(infoTexto);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Teléfono

        JLabel lblTel = new JLabel("TEL: " + ins.getTelefono());
        lblTel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTel.setForeground(Color.LIGHT_GRAY);
        lblTel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- PANEL DE BOTONES ---

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlAcciones.setOpaque(false);
        pnlAcciones.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JButton btnEditar = crearBotonAccion("EDITAR", new Color(60, 60, 60));
        JButton btnEliminar = crearBotonAccion("ELIMINAR", new Color(130, 0, 0));

        btnEditar.addActionListener(e -> mostrarVentanaCarga(ins));
        btnEliminar.addActionListener(e -> ejecutarEliminacion(ins));

        pnlAcciones.add(btnEditar);
        pnlAcciones.add(btnEliminar);

        // ENSAMBLADO SIN ESPACIOS MUERTOS

        card.add(Box.createVerticalStrut(10));
        card.add(lblAvatar);
        card.add(Box.createVerticalStrut(2));
        card.add(lblNombre);
        card.add(lblEsp);
        card.add(Box.createVerticalStrut(5));
        card.add(lblInfo);
        card.add(Box.createVerticalStrut(2));
        card.add(lblTel);
        card.add(Box.createVerticalStrut(10));
        card.add(pnlAcciones);

        return card;
    }

    private void mostrarVentanaCarga(Instructor instructorExistente) {
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
        VentanaCargaInstructor v = new VentanaCargaInstructor(f, instructorExistente);
        v.setVisible(true);

        if (v.isConfirmado()) {
            Instructor data = new Instructor(
                    v.getNombre(), v.getTelefono(), v.getEdad(), v.getEmail(), v.getEspecialidad()
            );

            boolean exito;
            if (instructorExistente == null) {
                exito = instructorDAO.registrarInstructor(data);
            } else {
                Instructor editado = new Instructor(
                        instructorExistente.getId(), data.getNombre(), data.getTelefono(),
                        data.getEdad(), data.getEmail(), data.getEspecialidad()
                );
                exito = instructorDAO.actualizarInstructor(editado);
            }

            if (exito) {
                new DialogoExito(f, "EXITO", "Staff actualizado correctamente.").setVisible(true);
                cargarInstructores();
            }
        }
    }

    private void ejecutarEliminacion(Instructor ins) {
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoConfirmacion diag = new DialogoConfirmacion(f, "¿Dar de baja a " + ins.getNombre() + "?");
        diag.setVisible(true);

        if (diag.getRespuesta()) {
            if (instructorDAO.eliminarInstructor(ins.getId())) {
                new DialogoExito(f, "ELIMINADO", "Instructor dado de baja.").setVisible(true);
                cargarInstructores();
            }
        }
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
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Botones pequeños para la tarjeta

    private JButton crearBotonAccion(String texto, Color bg) {
        JButton btn = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(90, 30));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}