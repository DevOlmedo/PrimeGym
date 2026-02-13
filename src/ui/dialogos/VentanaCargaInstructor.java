package ui.dialogos;

import model.Instructor;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaCargaInstructor extends JDialog {
    private JTextField txtNombre, txtTelefono, txtEdad, txtEmail, txtEspecialidad;
    private boolean confirmado = false;
    private Instructor instructorParaEditar;

    public VentanaCargaInstructor(Frame parent) {
        this(parent, null);
    }

    public VentanaCargaInstructor(Frame parent, Instructor instructor) {
        super(parent, true);
        this.instructorParaEditar = instructor;

        setUndecorated(true);
        setSize(420, 580); // Aumentamos la altura para los nuevos campos
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        inicializarComponentes();
        if (instructorParaEditar != null) rellenarCampos();
    }

    private void rellenarCampos() {
        txtNombre.setText(instructorParaEditar.getNombre());
        txtTelefono.setText(instructorParaEditar.getTelefono());
        txtEdad.setText(instructorParaEditar.getEdad());
        txtEmail.setText(instructorParaEditar.getEmail());
        txtEspecialidad.setText(instructorParaEditar.getEspecialidad());
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 35, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(70, 70, 70));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);

        JLabel lblTitulo = new JLabel(instructorParaEditar == null ? "NUEVO INSTRUCTOR" : "EDITAR INSTRUCTOR", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(180, 0, 0));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        // --- Formulario ---

        JPanel panelCampos = new JPanel(new GridLayout(0, 1, 2, 5));
        panelCampos.setOpaque(false);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(5, 35, 5, 35));

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 12);
        Color colorTexto = Color.GRAY;

        panelCampos.add(crearLabel(" Nombre y Apellido:", fuenteLabel, colorTexto));
        txtNombre = crearCampo();
        panelCampos.add(txtNombre);

        panelCampos.add(crearLabel(" Teléfono / WhatsApp:", fuenteLabel, colorTexto));
        txtTelefono = crearCampo();
        panelCampos.add(txtTelefono);

        panelCampos.add(crearLabel(" Edad:", fuenteLabel, colorTexto));
        txtEdad = crearCampo();
        panelCampos.add(txtEdad);

        panelCampos.add(crearLabel(" Correo Electrónico:", fuenteLabel, colorTexto));
        txtEmail = crearCampo();
        panelCampos.add(txtEmail);

        panelCampos.add(crearLabel(" Especialidad:", fuenteLabel, colorTexto));
        txtEspecialidad = crearCampo();
        panelCampos.add(txtEspecialidad);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelInferior.setOpaque(false);

        JButton btnCerrar = crearBotonEstilo("CANCELAR", new Color(70, 70, 70));
        JButton btnGuardar = crearBotonEstilo("GUARDAR", new Color(180, 0, 0));

        btnCerrar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> validarYGuardar());

        panelInferior.add(btnCerrar);
        panelInferior.add(btnGuardar);

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        add(panelPrincipal);
    }

    private void validarYGuardar() {
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);

        // Validación

        if (txtNombre.getText().trim().isEmpty() || txtEspecialidad.getText().trim().isEmpty()) {
            new DialogoAviso(f, "⚠ El Nombre y la Especialidad son obligatorios.").setVisible(true);
            return;
        }

        confirmado = true;
        dispose();
    }

    private JButton crearBotonEstilo(String texto, Color bg) {
        JButton b = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setPreferredSize(new Dimension(140, 40));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField crearCampo() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(55, 55, 55));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JLabel crearLabel(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    // Getters para recuperar toda la info

    public boolean isConfirmado() { return confirmado; }
    public String getNombre() { return txtNombre.getText().trim(); }
    public String getTelefono() { return txtTelefono.getText().trim(); }
    public String getEdad() { return txtEdad.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public String getEspecialidad() { return txtEspecialidad.getText().trim(); }
}