package ui.pestañas;

import logic.AccesoDenegadoException;
import logic.ControlAcceso;
import model.Socio;
import javax.swing.*;
import java.awt.*;

public class PanelAcceso extends JPanel {
    private JTextField txtBusqueda;
    private JButton btnBuscar;
    private JPanel panelCarnet;
    private JLabel lblDatosSocio;
    private JLabel lblEstadoCuota;
    private ControlAcceso control;

    public PanelAcceso(ControlAcceso control) {
        this.control = control;
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridx = 0;

        // Título

        JLabel lblTitulo = new JLabel("CONTROL DE ACCESO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(lblTitulo, gbc);

        // Buscador

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBuscador.setOpaque(false);

        txtBusqueda = new JTextField(20);
        txtBusqueda.setPreferredSize(new Dimension(350, 45));
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtBusqueda.setBackground(new Color(30, 30, 30));
        txtBusqueda.setForeground(Color.WHITE);
        txtBusqueda.setCaretColor(Color.WHITE);
        txtBusqueda.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 0, 0)));

        btnBuscar = crearBotonEstilizado("VERIFICAR");
        panelBuscador.add(txtBusqueda);
        panelBuscador.add(btnBuscar);

        gbc.gridy = 1;
        add(panelBuscador, gbc);

        // 3. El Carnet
        panelCarnet = construirPanelCarnet();
        panelCarnet.setVisible(false); // Oculto al inicio
        gbc.gridy = 2;
        add(panelCarnet, gbc);

        // Lógica del botón
        btnBuscar.addActionListener(e -> ejecutarBusqueda());
    }

    // --- MÉTODOS MUDADOS ---

    private void ejecutarBusqueda() {
        try {
            String dni = txtBusqueda.getText();
            // Por ahora usamos el socio de prueba, luego usaremos el buscador real
            Socio socioPrueba = new Socio("RIQUELME ANA", dni, java.time.LocalDate.now().plusDays(10));

            control.verificarIngreso(socioPrueba);

            String info = "<html><body style='color:white; padding:20px; font-family:Segoe UI;'>"
                    + "<h1 style='margin:0; font-size:22px;'>" + socioPrueba.getNombre() + "</h1>"
                    + "<p style='margin:10px 0 5px 0; font-size:16px; color:#CCCCCC;'>DNI: " + socioPrueba.getDni() + "</p>"
                    + "<p style='margin:0; font-size:16px; color:#CCCCCC;'>Vencimiento: " + socioPrueba.getFechaVencimiento() + "</p>"
                    + "</body></html>";

            lblDatosSocio.setText(info);
            lblEstadoCuota.setText("CUOTA AL DÍA");
            lblEstadoCuota.setBackground(new Color(0, 150, 0));

        } catch (AccesoDenegadoException ex) {
            lblEstadoCuota.setText("CUOTA VENCIDA");
            lblEstadoCuota.setBackground(new Color(180, 0, 0));
        }

        panelCarnet.setVisible(true);
        revalidate();
        repaint();
    }

    private JPanel construirPanelCarnet() {
        JPanel carnet = new JPanel(new BorderLayout());
        carnet.setPreferredSize(new Dimension(500, 250));
        carnet.setBackground(new Color(45, 45, 45));
        carnet.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));

        // Parte superior (Foto y Datos)

        JPanel panelInfo = new JPanel(new GridLayout(1, 2));
        panelInfo.setOpaque(false);

        JLabel lblFoto = new JLabel("FOTO", SwingConstants.CENTER);
        lblFoto.setForeground(Color.GRAY);
        lblFoto.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(80, 80, 80)));

        lblDatosSocio = new JLabel();
        panelInfo.add(lblFoto);
        panelInfo.add(lblDatosSocio);

        // Barra Inferior de Estado

        lblEstadoCuota = new JLabel("ESTADO", SwingConstants.CENTER);
        lblEstadoCuota.setOpaque(true);
        lblEstadoCuota.setForeground(Color.WHITE);
        lblEstadoCuota.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEstadoCuota.setPreferredSize(new Dimension(0, 45));

        carnet.add(panelInfo, BorderLayout.CENTER);
        carnet.add(lblEstadoCuota, BorderLayout.SOUTH);

        return carnet;
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(220, 0, 0) : new Color(180, 0, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(140, 45));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}