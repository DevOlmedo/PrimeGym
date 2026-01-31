package ui.pestañas;

import dao.SocioDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanelAcceso extends JPanel {
    private JTextField txtBusqueda;
    private JButton btnBuscar;
    private JPanel panelCarnet;
    private JLabel lblDatosSocio;
    private JLabel lblEstadoCuota;
    private SocioDAO socioDAO = new SocioDAO();

    public PanelAcceso() {
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
        txtBusqueda.setBackground(new Color(45, 45, 45));
        txtBusqueda.setForeground(Color.WHITE);
        txtBusqueda.setCaretColor(Color.WHITE);
        txtBusqueda.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 0, 0)));
        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) ejecutarBusqueda();
            }
        });

        btnBuscar = crearBotonEstilizado("VERIFICAR");
        panelBuscador.add(txtBusqueda);
        panelBuscador.add(btnBuscar);

        gbc.gridy = 1;
        add(panelBuscador, gbc);

        // El Carnet

        panelCarnet = construirPanelCarnet();
        panelCarnet.setVisible(false);
        gbc.gridy = 2;
        add(panelCarnet, gbc);

        btnBuscar.addActionListener(e -> ejecutarBusqueda());
    }

    private void ejecutarBusqueda() {
        String entrada = txtBusqueda.getText().trim();
        if (entrada.isEmpty()) return;

        try {
            int dni = Integer.parseInt(entrada);

            // Busca los datos reales en SQLite

            Object[] socio = socioDAO.buscarPorDni(dni);

            if (socio != null) {
                String nombreCompleto = ((String) socio[1] + " " + (String) socio[2]).toUpperCase();
                String plan = (String) socio[3];
                String venc = (String) socio[4]; // Fecha de vencimiento
                String estado = (String) socio[5];

                String info = "<html><body style='color:white; font-family:Segoe UI;'>"
                        + "<div style='margin-left:10px;'>"
                        + "<span style='font-size:18px; font-weight:bold; color:#FF8C00;'>" + nombreCompleto + "</span><br>"
                        + "<span style='font-size:11px; color:#AAAAAA;'>FICHA DE SOCIO</span>"
                        + "<div style='border-top: 1px solid #555; margin-top:5px; margin-bottom:10px; width:250px;'></div>"
                        + "<table style='font-size:14px; color:#DDDDDD;'>"
                        + "<tr><td><b>DNI:</b></td><td>&nbsp;&nbsp;" + dni + "</td></tr>"
                        + "<tr><td style='padding-top:4px;'><b>Plan:</b></td><td style='padding-top:4px;'>&nbsp;&nbsp;" + plan + "</td></tr>"
                        + "<tr><td style='padding-top:4px;'><b>Vence:</b></td><td style='padding-top:4px;'>&nbsp;&nbsp;" + venc + "</td></tr>"
                        + "</table>"
                        + "</div>"
                        + "</body></html>";

                lblDatosSocio.setText(info);

                // Lógica de colores según el estado calculado en el DAO

                if ("AL DÍA".equals(estado)) {
                    lblEstadoCuota.setText("CUOTA AL DÍA - ACCESO PERMITIDO");
                    lblEstadoCuota.setBackground(new Color(0, 150, 0)); // Verde
                } else {
                    lblEstadoCuota.setText("CUOTA VENCIDA - ACCESO DENEGADO");
                    lblEstadoCuota.setBackground(new Color(180, 0, 0)); // Rojo
                }
                panelCarnet.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Socio no encontrado.");
                panelCarnet.setVisible(false);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI válido (solo números).");
        }

        txtBusqueda.selectAll();
        revalidate();
        repaint();
    }

    private JPanel construirPanelCarnet() {
        JPanel carnet = new JPanel(new BorderLayout());

        carnet.setPreferredSize(new Dimension(550, 300));
        carnet.setBackground(new Color(45, 45, 45));

        carnet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel panelInfo = new JPanel(new BorderLayout(20, 0));
        panelInfo.setOpaque(false);

        JLabel lblFoto = new JLabel("PHOTO", SwingConstants.CENTER);
        lblFoto.setPreferredSize(new Dimension(160, 0));
        lblFoto.setForeground(new Color(90, 90, 90));
        lblFoto.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblFoto.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));

        lblDatosSocio = new JLabel();
        lblDatosSocio.setVerticalAlignment(SwingConstants.TOP);

        panelInfo.add(lblFoto, BorderLayout.WEST);
        panelInfo.add(lblDatosSocio, BorderLayout.CENTER);

        lblEstadoCuota = new JLabel("ESTADO", SwingConstants.CENTER);
        lblEstadoCuota.setOpaque(true);
        lblEstadoCuota.setForeground(Color.WHITE);
        lblEstadoCuota.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblEstadoCuota.setPreferredSize(new Dimension(0, 60));

        JPanel panelContenedorEstado = new JPanel(new BorderLayout());
        panelContenedorEstado.setOpaque(false);
        panelContenedorEstado.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelContenedorEstado.add(lblEstadoCuota, BorderLayout.CENTER);

        carnet.add(panelInfo, BorderLayout.CENTER);
        carnet.add(panelContenedorEstado, BorderLayout.SOUTH);

        return carnet;
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(220, 0, 0) : new Color(180, 0, 0));                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
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