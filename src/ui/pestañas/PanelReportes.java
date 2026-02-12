package ui.pestañas;

import dao.CierreDAO;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PanelReportes extends JPanel {
    private JTextField txtFechaBusqueda;
    private JLabel lblEfectivo, lblMP, lblTotal, lblTipoCierre;
    private CierreDAO cierreDAO = new CierreDAO();

    public PanelReportes() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        // --- ENCABEZADO ---

        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titulo = new JLabel("REPORTES HISTÓRICOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);
        pnlNorte.add(titulo);
        add(pnlNorte, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL ---

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);

        // Selector de Fecha

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        pnlBusqueda.setOpaque(false);

        JLabel lblInstruccion = new JLabel("CONSULTAR FECHA (AAAA-MM-DD):");
        lblInstruccion.setForeground(Color.GRAY);
        lblInstruccion.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtFechaBusqueda = new JTextField(LocalDate.now().toString(), 10);
        txtFechaBusqueda.setBackground(new Color(40, 40, 40));
        txtFechaBusqueda.setForeground(Color.WHITE);
        txtFechaBusqueda.setCaretColor(Color.WHITE);
        txtFechaBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtFechaBusqueda.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

        JButton btnConsultar = crearBotonRojo("BUSCAR CIERRE");

        pnlBusqueda.add(lblInstruccion);
        pnlBusqueda.add(txtFechaBusqueda);
        pnlBusqueda.add(btnConsultar);
        pnlCentro.add(pnlBusqueda);

        // --- TARJETAS DE RESULTADOS ---

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        lblEfectivo = crearTarjeta(pnlCards, "EFECTIVO", "$ 0.00", new Color(46, 204, 113));
        lblMP = crearTarjeta(pnlCards, "MERCADO PAGO", "$ 0.00", new Color(52, 152, 219));
        lblTotal = crearTarjeta(pnlCards, "TOTAL RECAUDADO", "$ 0.00", new Color(241, 196, 15));

        pnlCentro.add(pnlCards);

        // Info extra del cierre

        lblTipoCierre = new JLabel("Estado: Sin consulta");
        lblTipoCierre.setForeground(Color.GRAY);
        lblTipoCierre.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlCentro.add(lblTipoCierre);

        add(pnlCentro, BorderLayout.CENTER);

        // --- ACCIÓN DEL BOTÓN ---

        btnConsultar.addActionListener(e -> buscarCierre());
    }

    private void buscarCierre() {
        try {
            LocalDate fecha = LocalDate.parse(txtFechaBusqueda.getText().trim());
            Object[] datos = cierreDAO.obtenerCierrePorFecha(fecha);

            if (datos != null) {
                lblEfectivo.setText("$ " + String.format("%.2f", (Double) datos[0]));
                lblMP.setText("$ " + String.format("%.2f", (Double) datos[1]));
                lblTotal.setText("$ " + String.format("%.2f", (Double) datos[2]));
                lblTipoCierre.setText("Tipo de Cierre: " + datos[3]);
            } else {
                limpiarDatos("No hay cierre registrado para esta fecha.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use AAAA-MM-DD");
        }
    }

    private void limpiarDatos(String msg) {
        lblEfectivo.setText("$ 0.00");
        lblMP.setText("$ 0.00");
        lblTotal.setText("$ 0.00");
        lblTipoCierre.setText(msg);
    }

    private JLabel crearTarjeta(JPanel contenedor, String titulo, String valor, Color colorBorde) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(35, 35, 35));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblT = new JLabel(titulo);
        lblT.setForeground(Color.GRAY);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lblV = new JLabel(valor);
        lblV.setForeground(Color.WHITE);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 22));

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        contenedor.add(card);
        return lblV;
    }

    private JButton crearBotonRojo(String t) {
        JButton btn = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color rojoPrime = new Color(180, 0, 0);
                if (getModel().isRollover()) {
                    g2.setColor(rojoPrime.darker());
                } else {
                    g2.setColor(rojoPrime);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(150, 35));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }
}