package ui.dialogos;

import dao.SocioDAO;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DialogoNuevoSocio extends JDialog {
    private JTextField txtDni, txtNombre, txtApellido, txtVencimiento;
    private JComboBox<String> comboPlan;
    private JButton btnGuardar, btnCancelar;
    private SocioDAO socioDAO;
    private boolean modoEdicion = false;

    public DialogoNuevoSocio(Frame parent) {
        this(parent, null);
    }

    public DialogoNuevoSocio(Frame parent, Object[] datosSocio) {
        super(parent, "Registrar Nuevo Socio", true);
        this.socioDAO = new SocioDAO();

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(35, 35, 35));

        // --- PANEL DE FORMULARIO ---

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 15));
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

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
        comboPlan.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        panelForm.add(comboPlan);

        panelForm.add(crearLabel("Vencimiento:", labelFont));
        txtVencimiento = crearTextField();

        LocalDate proximoMes = LocalDate.now().plusMonths(1);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtVencimiento.setText(proximoMes.format(formato));
        panelForm.add(txtVencimiento);

        add(panelForm, BorderLayout.CENTER);

        if (datosSocio != null) {
            modoEdicion = true;
            setTitle("Editar Socio");
            txtDni.setText(String.valueOf(datosSocio[0]));
            txtDni.setEditable(false);
            txtNombre.setText((String) datosSocio[1]);
            txtApellido.setText((String) datosSocio[2]);
            comboPlan.setSelectedItem(datosSocio[3]);
            txtVencimiento.setText((String) datosSocio[4]);
        }

        // --- PANEL DE BOTONES ---

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.setOpaque(false);

        // BOTÓN GUARDAR/ACTUALIZAR

        btnGuardar = new JButton(modoEdicion ? "ACTUALIZAR" : "GUARDAR SOCIO") {
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
        estilizarBotonEmergente(btnGuardar);

        // BOTÓN CANCELAR

        btnCancelar = new JButton("CANCELAR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color grisOscuro = new Color(70, 70, 70);
                g2.setColor(getModel().isRollover() ? grisOscuro.darker() : grisOscuro);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        estilizarBotonEmergente(btnCancelar);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void estilizarBotonEmergente(JButton btn) {
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
    }

    private void guardar() {

        Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);

        if (txtDni.getText().isEmpty() || txtNombre.getText().isEmpty()) {
            new DialogoAviso(padre, "⚠ El DNI y el Nombre son obligatorios.").setVisible(true);
            return;
        }

        try {
            int dni = Integer.parseInt(txtDni.getText().trim());
            String nom = txtNombre.getText().trim();
            String ape = txtApellido.getText().trim();
            String plan = (String) comboPlan.getSelectedItem();
            String venc = txtVencimiento.getText().trim();

            boolean exito = modoEdicion ?
                    socioDAO.editarSocio(dni, nom, ape, plan, venc) :
                    socioDAO.guardarSocio(dni, nom, ape, plan, venc);

            if (exito) {
                String titulo = modoEdicion ? "SOCIO ACTUALIZADO" : "SOCIO REGISTRADO";
                new DialogoExito(padre, titulo, "Los datos de " + nom + " se guardaron correctamente.").setVisible(true);
                dispose();
            } else {
                // Error de base de datos o DNI duplicado
                new DialogoAviso(padre, "❌ Error al procesar los datos. Verifica si el DNI ya existe.").setVisible(true);
            }
        } catch (NumberFormatException ex) {
            // Error de entrada de texto
            new DialogoAviso(padre, "⚠ Error: El DNI debe ser un número válido.").setVisible(true);
        }
    }

    private JLabel crearLabel(String texto, Font font) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(font);
        return l;
    }

    private JTextField crearTextField() {
        JTextField t = new JTextField(15);
        t.setBackground(new Color(50, 50, 50));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.ORANGE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return t;
    }
}