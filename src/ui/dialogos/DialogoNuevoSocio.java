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
    private boolean modoEdicion = false; // Flag para distinguir la acción

    // Constructor para NUEVO SOCIO
    public DialogoNuevoSocio(Frame parent) {
        this(parent, null);
    }

    // Constructor para EDITAR SOCIO (recibe los datos de la fila seleccionada)
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

        // Seteamos fecha automática por defecto
        LocalDate proximoMes = LocalDate.now().plusMonths(1);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtVencimiento.setText(proximoMes.format(formato));
        panelForm.add(txtVencimiento);

        add(panelForm, BorderLayout.CENTER);

        // --- CONFIGURACIÓN SI ES EDICIÓN ---
        if (datosSocio != null) {
            modoEdicion = true;
            setTitle("Editar Socio");
            txtDni.setText(String.valueOf(datosSocio[0]));
            txtDni.setEditable(false); // No permitimos editar el DNI (es la clave única)
            txtNombre.setText((String) datosSocio[1]);
            txtApellido.setText((String) datosSocio[2]);
            comboPlan.setSelectedItem(datosSocio[3]);
            txtVencimiento.setText((String) datosSocio[4]);
        }

        // --- PANEL DE BOTONES ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.setOpaque(false);

        btnGuardar = new JButton(modoEdicion ? "ACTUALIZAR" : "GUARDAR SOCIO");
        btnGuardar.setPreferredSize(new Dimension(150, 35));
        btnGuardar.setBackground(new Color(255, 140, 0));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancelar = new JButton("CANCELAR");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setBackground(new Color(70, 70, 70));
        btnCancelar.setForeground(Color.WHITE);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void guardar() {
        if (txtDni.getText().isEmpty() || txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El DNI y el Nombre son obligatorios.");
            return;
        }

        try {
            int dni = Integer.parseInt(txtDni.getText().trim());
            String nom = txtNombre.getText().trim();
            String ape = txtApellido.getText().trim();
            String plan = (String) comboPlan.getSelectedItem();
            String venc = txtVencimiento.getText().trim();

            boolean exito;
            if (modoEdicion) {
                // Si estamos editando, usamos el nuevo método UPDATE
                exito = socioDAO.editarSocio(dni, nom, ape, plan, venc);
            } else {
                exito = socioDAO.guardarSocio(dni, nom, ape, plan, venc);
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, "✅ Operación realizada con éxito.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al procesar los datos.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: El DNI debe ser un número válido.");
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