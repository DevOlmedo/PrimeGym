package ui.dialogos;

import dao.InstructorDAO;
import model.Instructor;
import model.Actividad;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class VentanaCargaActividad extends JDialog {
    private JTextField txtNombre, txtCupo, txtDias;
    private JSpinner spnHoraIni, spnMinIni, spnHoraFin, spnMinFin;
    private JComboBox<Instructor> cbInstructores;
    private boolean confirmado = false;
    private InstructorDAO instructorDAO = new InstructorDAO();
    private Actividad actividadParaEditar; // Para saber si estamos editando

    public VentanaCargaActividad(Frame parent) {
        this(parent, null);
    }

    public VentanaCargaActividad(Frame parent, Actividad actividad) {
        super(parent, true);
        this.actividadParaEditar = actividad;

        setUndecorated(true);
        setSize(420, 580);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        inicializarComponentes();
        cargarListaInstructores();
        if (actividadParaEditar != null) {
            rellenarDatosEdicion();
        }
    }

    private void rellenarDatosEdicion() {
        txtNombre.setText(actividadParaEditar.getNombre());
        txtCupo.setText(String.valueOf(actividadParaEditar.getCupoMaximo()));
        txtDias.setText(actividadParaEditar.getDias());
        for (int i = 0; i < cbInstructores.getItemCount(); i++) {
            if (cbInstructores.getItemAt(i).getId() == actividadParaEditar.getInstructorId()) {
                cbInstructores.setSelectedIndex(i);
                break;
            }
        }

        // Lógica para desarmar el horario "HH:mm a HH:mm"

        try {
            String horario = actividadParaEditar.getHorario();
            String[] partes = horario.split(" a ");
            String[] ini = partes[0].split(":");
            String[] fin = partes[1].split(":");

            spnHoraIni.setValue(Integer.parseInt(ini[0]));
            spnMinIni.setValue(Integer.parseInt(ini[1]));
            spnHoraFin.setValue(Integer.parseInt(fin[0]));
            spnMinFin.setValue(Integer.parseInt(fin[1]));
        } catch (Exception e) {
            System.err.println("Error procesando horario para edición");
        }
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 35, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(180, 0, 0));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);

        String tituloTexto = (actividadParaEditar == null) ? "NUEVA ACTIVIDAD" : "EDITAR ACTIVIDAD";
        JLabel lblTitulo = new JLabel(tituloTexto, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        JPanel panelCampos = new JPanel(new GridLayout(0, 1, 2, 8));
        panelCampos.setOpaque(false);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(5, 35, 10, 35));

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 12);
        Color colorTexto = Color.GRAY;

        panelCampos.add(crearLabel(" Nombre de la Actividad:", fuenteLabel, colorTexto));
        txtNombre = crearCampo();
        panelCampos.add(txtNombre);

        panelCampos.add(crearLabel(" Seleccionar Instructor:", fuenteLabel, colorTexto));
        cbInstructores = crearComboEstilizado();
        panelCampos.add(cbInstructores);

        panelCampos.add(crearLabel(" Cupo Máximo:", fuenteLabel, colorTexto));
        txtCupo = crearCampo();
        txtCupo.setText("20");
        panelCampos.add(txtCupo);

        panelCampos.add(crearLabel(" Horario de la Clase:", fuenteLabel, colorTexto));
        panelCampos.add(crearPanelHorario());

        panelCampos.add(crearLabel(" Días (Ej: Lunes, Miércoles):", fuenteLabel, colorTexto));
        txtDias = crearCampo();
        panelCampos.add(txtDias);

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

    private JPanel crearPanelHorario() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnl.setOpaque(false);

        spnHoraIni = crearSpinner(23, 18);
        spnMinIni = crearSpinner(59, 0);
        spnHoraFin = crearSpinner(23, 19);
        spnMinFin = crearSpinner(59, 30);

        pnl.add(spnHoraIni); pnl.add(crearLabelTexto("h", Color.MAGENTA));
        pnl.add(spnMinIni);  pnl.add(crearLabelTexto("m", Color.MAGENTA));
        pnl.add(crearLabelTexto(" a ", Color.WHITE));
        pnl.add(spnHoraFin); pnl.add(crearLabelTexto("h", Color.MAGENTA));
        pnl.add(spnMinFin);  pnl.add(crearLabelTexto("m", Color.MAGENTA));

        return pnl;
    }

    private JSpinner crearSpinner(int max, int actual) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(actual, 0, max, 1));
        s.setPreferredSize(new Dimension(50, 35));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) s.getEditor();
        JTextField tf = editor.getTextField();
        tf.setBackground(new Color(55, 55, 55));
        tf.setForeground(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tf.setBorder(null);
        s.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        return s;
    }

    private JLabel crearLabelTexto(String t, Color c) {
        JLabel l = new JLabel(t);
        l.setForeground(c);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    private void cargarListaInstructores() {
        List<Instructor> lista = instructorDAO.obtenerTodos();
        for (Instructor i : lista) {
            cbInstructores.addItem(i);
        }
    }

    private void validarYGuardar() {
        if (txtNombre.getText().trim().isEmpty() || cbInstructores.getSelectedItem() == null) {
            Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
            new DialogoAviso(f, "⚠ Debe completar el nombre y el instructor.").setVisible(true);
            return;
        }
        confirmado = true;
        dispose();
    }

    private JTextField crearCampo() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(55, 55, 55));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JComboBox<Instructor> crearComboEstilizado() {
        JComboBox<Instructor> cb = new JComboBox<>();
        cb.setBackground(new Color(55, 55, 55));
        cb.setForeground(Color.WHITE);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Instructor) setText(((Instructor) value).getNombre());
                return this;
            }
        });
        return cb;
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
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel crearLabel(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    public boolean isConfirmado() { return confirmado; }
    public String getNombre() { return txtNombre.getText().trim(); }
    public int getInstructorId() { return ((Instructor) cbInstructores.getSelectedItem()).getId(); }
    public int getCupo() { return Integer.parseInt(txtCupo.getText().trim()); }
    public String getDias() { return txtDias.getText().trim(); }

    public String getHorario() {
        return String.format("%02d:%02d a %02d:%02d",
                spnHoraIni.getValue(), spnMinIni.getValue(),
                spnHoraFin.getValue(), spnMinFin.getValue());
    }
}