package ui.pestañas;

import dao.SocioDAO;
import dao.PagoDAO;
import dao.CierreDAO; // Importación necesaria
import ui.dialogos.DialogoConfirmacion;
import ui.dialogos.DialogoExito;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PanelCaja extends JPanel {
    private JTextField txtDni, txtMonto;
    private JComboBox<String> comboMetodo;
    private JTable tablaPagos;
    private DefaultTableModel modeloTabla;
    private SocioDAO socioDAO = new SocioDAO();
    private PagoDAO pagoDAO = new PagoDAO();
    private CierreDAO cierreDAO = new CierreDAO();

    public PanelCaja() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        // --- ENCABEZADO ---

        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titulo = new JLabel("GESTIÓN DE COBROS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);
        pnlNorte.add(titulo);
        add(pnlNorte, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL ---

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);

        // FORMULARIO DE COBRO

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        txtDni = crearTextField();
        txtMonto = crearTextField();

        String[] opciones = {"Efectivo", "Mercado Pago"};
        comboMetodo = new JComboBox<>(opciones);
        comboMetodo.setBackground(new Color(45, 45, 45));
        comboMetodo.setForeground(Color.WHITE);
        comboMetodo.setPreferredSize(new Dimension(220, 35));

        JButton btnCobrar = crearBotonRojoPrime("REGISTRAR PAGO Y RENOVAR");

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(crearLabel("DNI DEL SOCIO:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(txtDni, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(crearLabel("MONTO ($):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(txtMonto, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(crearLabel("MÉTODO:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(comboMetodo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        pnlForm.add(btnCobrar, gbc);

        pnlCentro.add(pnlForm);
        pnlCentro.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- HISTORIAL ---

        modeloTabla = new DefaultTableModel(new String[]{"DNI SOCIO", "MONTO", "FECHA", "MÉTODO"}, 0);
        tablaPagos = new JTable(modeloTabla);
        estilizarTablaPrime(tablaPagos);

        JScrollPane scroll = new JScrollPane(tablaPagos);
        scroll.setPreferredSize(new Dimension(750, 250));
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

        JPanel pnlMargenTabla = new JPanel(new BorderLayout());
        pnlMargenTabla.setOpaque(false);
        pnlMargenTabla.setBorder(BorderFactory.createEmptyBorder(0, 50, 10, 50));

        JLabel lblHist = new JLabel("HISTORIAL RECIENTE");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHist.setForeground(new Color(120, 120, 120));
        pnlMargenTabla.add(lblHist, BorderLayout.NORTH);
        pnlMargenTabla.add(scroll, BorderLayout.CENTER);

        pnlCentro.add(pnlMargenTabla);

        // --- BOTÓN CIERRE DE CAJA ---

        JPanel pnlSurCierre = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlSurCierre.setOpaque(false);
        pnlSurCierre.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JButton btnCerrarCaja = crearBotonNaranjaPrime("CERRAR CAJA DE HOY");
        pnlSurCierre.add(btnCerrarCaja);

        add(pnlCentro, BorderLayout.CENTER);
        add(pnlSurCierre, BorderLayout.SOUTH);

        // Eventos

        btnCobrar.addActionListener(e -> ejecutarCobro());
        btnCerrarCaja.addActionListener(e -> ejecutarCierreManual());
        cargarUltimosPagos();
    }

    private void ejecutarCierreManual() {
        LocalDate hoy = LocalDate.now();
        boolean yaExiste = cierreDAO.yaEstaCerrado(hoy);

        // Mensaje Dinamico

        String mensaje = yaExiste
                ? "¿Deseas ACTUALIZAR el cierre de hoy con los nuevos movimientos?"
                : "¿Deseas realizar el cierre de caja de la jornada actual?";

        Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoConfirmacion diag = new DialogoConfirmacion(padre, mensaje);

        diag.setVisible(true);

        if (diag.getRespuesta()) {
            cierreDAO.ejecutarCierre(hoy, false);

            String textoExito = yaExiste ? "CIERRE ACTUALIZADO" : "CAJA CERRADA";
            String cuerpoMsg = "Los registros de hoy han sido guardados correctamente.";

            new DialogoExito(padre, textoExito, cuerpoMsg).setVisible(true);
        }
    }

    private void ejecutarCobro() {
        try {
            int dni = Integer.parseInt(txtDni.getText().trim());
            double monto = Double.parseDouble(txtMonto.getText().trim());
            String metodo = (String) comboMetodo.getSelectedItem();

            if (pagoDAO.registrarPago(dni, monto, metodo)) {
                if (socioDAO.renovarSocio(dni)) {
                    txtDni.setText("");
                    txtMonto.setText("");
                    cargarUltimosPagos();
                    mostrarMensajeOscuro("✅ Pago registrado con " + metodo);
                }
            }
        } catch (Exception ex) {
            mostrarMensajeOscuro("❌ Error: Verifique los datos ingresados.");
        }
    }

    private void cargarUltimosPagos() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = pagoDAO.obtenerUltimosPagos(10);
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    // Estilo Naranja para el Cierre

    private JButton crearBotonNaranjaPrime(String t) {
        JButton btn = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(255, 100, 0) : new Color(255, 140, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(280, 45));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton crearBotonRojoPrime(String t) {
        JButton btn = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(220, 0, 0) : new Color(180, 0, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(350, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private void estilizarTablaPrime(JTable t) {
        t.setBackground(new Color(25, 25, 25));
        t.setForeground(new Color(220, 220, 220));
        t.setRowHeight(35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setSelectionBackground(new Color(255, 140, 0, 100));
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(45, 45, 45));
        t.setShowGrid(true);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(35, 35, 35));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
    }

    private void mostrarMensajeOscuro(String msg) {
        UIManager.put("OptionPane.background", new Color(45, 45, 45));
        UIManager.put("Panel.background", new Color(45, 45, 45));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        JOptionPane.showMessageDialog(this, msg, "PrimeGym", JOptionPane.PLAIN_MESSAGE);
    }

    private JLabel crearLabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(180, 180, 180));
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private JTextField crearTextField() {
        JTextField t = new JTextField(15);
        t.setBackground(new Color(40, 40, 40));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setPreferredSize(new Dimension(220, 35));
        t.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 65)));
        return t;
    }
}