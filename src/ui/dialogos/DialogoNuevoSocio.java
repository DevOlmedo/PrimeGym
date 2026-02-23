package ui.dialogos;

import dao.SocioDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DialogoNuevoSocio extends JDialog {
    private JTextField txtDni, txtNombre, txtApellido, txtTelefono, txtVencimiento;
    private JComboBox<String> comboPlan;
    private JButton btnGuardar, btnCancelar;
    private SocioDAO socioDAO;
    private boolean modoEdicion = false;

    public DialogoNuevoSocio(Frame parent) {
        this(parent, null);
    }

    public DialogoNuevoSocio(Frame parent, Object[] datosSocio) {
        super(parent, true);
        this.socioDAO = new SocioDAO();
        setUndecorated(true);
        setSize(420, 520);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        inicializarInterfaz(parent, datosSocio);
    }

    private void inicializarInterfaz(Frame parent, Object[] datosSocio) {
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 35, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(180, 0, 0));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 30, 30);
                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);

        // --- TÍTULO ---

        JLabel lblTitulo = new JLabel(datosSocio == null ? "REGISTRAR SOCIO" : "EDITAR SOCIO", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        // --- FORMULARIO ---

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 20));
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);

        panelForm.add(crearLabel("DNI:", labelFont));
        txtDni = crearTextField();
        panelForm.add(txtDni);

        panelForm.add(crearLabel("Nombre:", labelFont));
        txtNombre = crearTextField();
        panelForm.add(txtNombre);

        panelForm.add(crearLabel("Apellido:", labelFont));
        txtApellido = crearTextField();
        panelForm.add(txtApellido);

        panelForm.add(crearLabel("Plan:", labelFont));
        comboPlan = new JComboBox<>(new String[]{"Musculación", "Crossfit", "Funcional", "Boxeo"});
        comboPlan.setBackground(new Color(50, 50, 50));
        comboPlan.setForeground(Color.WHITE);
        panelForm.add(comboPlan);

        panelForm.add(crearLabel("WhatsApp (Cód. Área):", labelFont));
        txtTelefono = crearTextField();
        txtTelefono.setToolTipText("Ej: 5491122334455");
        panelForm.add(txtTelefono);

        panelForm.add(crearLabel("Vencimiento:", labelFont));
        txtVencimiento = crearTextField();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtVencimiento.setText(LocalDate.now().plusMonths(1).format(formato));
        panelForm.add(txtVencimiento);

        // --- CARGAR DATOS SI ES EDICIÓN ---

        if (datosSocio != null) {
            modoEdicion = true;
            txtDni.setText(String.valueOf(datosSocio[0]));
            txtDni.setEditable(false);
            txtNombre.setText((String) datosSocio[1]);
            txtApellido.setText((String) datosSocio[2]);
            comboPlan.setSelectedItem(datosSocio[3]);
            txtTelefono.setText((String) datosSocio[4]); // Carga el teléfono
            txtVencimiento.setText((String) datosSocio[5]);
        }

        // --- BOTONES ---

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        panelBotones.setOpaque(false);

        btnGuardar = crearBotonEstilizado(modoEdicion ? "ACTUALIZAR" : "GUARDAR", new Color(180, 0, 0));
        btnCancelar = crearBotonEstilizado("CANCELAR", new Color(70, 70, 70));

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelForm, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        add(panelPrincipal);
    }

    private void guardar() {
        Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);

        if (txtDni.getText().isEmpty() || txtNombre.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
            new DialogoAviso(padre, "⚠ DNI, Nombre y WhatsApp son obligatorios.").setVisible(true);
            return;
        }

        try {
            int dni = Integer.parseInt(txtDni.getText().trim());
            String nom = txtNombre.getText().trim();
            String ape = txtApellido.getText().trim();
            String plan = (String) comboPlan.getSelectedItem();
            String tel = txtTelefono.getText().trim();
            String venc = txtVencimiento.getText().trim();

            boolean exito = modoEdicion ?
                    socioDAO.editarSocio(dni, nom, ape, plan, tel, venc) :
                    socioDAO.guardarSocio(dni, nom, ape, plan, tel, venc);

            if (exito) {
                new DialogoExito(padre, "OPERACIÓN EXITOSA", "Socio guardado correctamente.").setVisible(true);
                dispose();
            } else {
                new DialogoAviso(padre, "❌ Error al procesar datos. Verifica el DNI.").setVisible(true);
            }
        } catch (NumberFormatException ex) {
            new DialogoAviso(padre, "⚠ El DNI debe ser un número válido.").setVisible(true);
        }
    }

    private JTextField crearTextField() {
        JTextField t = new JTextField();
        t.setBackground(new Color(55, 55, 55));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return t;
    }

    private JLabel crearLabel(String t, Font f) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.GRAY);
        l.setFont(f);
        return l;
    }

    private JButton crearBotonEstilizado(String t, Color bg) {
        JButton b = new JButton(t) {
            @Override
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
}