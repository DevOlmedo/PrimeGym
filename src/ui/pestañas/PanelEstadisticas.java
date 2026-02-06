package ui.pestañas;

import dao.SocioDAO;
import dao.PagoDAO;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;


public class PanelEstadisticas extends JPanel {
    private PagoDAO pagoDAO = new PagoDAO();
    private SocioDAO socioDAO = new SocioDAO();
    private JComboBox<String> comboPeriodo;
    private JLabel lblIngresos, lblActivos, lblMorosos;
    private DefaultTableModel modeloMorosos;
    private JPanel pnlGrafico;

    public PanelEstadisticas() {
        setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());
        // pagoDAO.resetearPagos(); METODO DE PagoDAO para limpiar todos los cobros/pagos, etc.

        // --- ENCABEZADO CON FILTRO ---

        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.setOpaque(false);
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titulo = new JLabel("TABLERO DE CONTROL");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        String[] periodos = {"Hoy", "Esta Semana", "Este Mes"};
        comboPeriodo = new JComboBox<>(periodos);
        comboPeriodo.setPreferredSize(new Dimension(150, 30));
        comboPeriodo.setBackground(new Color(45, 45, 45));
        comboPeriodo.setForeground(Color.WHITE);
        comboPeriodo.addActionListener(e -> actualizarDatos());

        JPanel pnlFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFiltro.setOpaque(false);
        JLabel lblVer = new JLabel("Ver: ");
        lblVer.setForeground(Color.GRAY);
        pnlFiltro.add(lblVer);
        pnlFiltro.add(comboPeriodo);

        pnlNorte.add(titulo, BorderLayout.WEST);
        pnlNorte.add(pnlFiltro, BorderLayout.EAST);
        add(pnlNorte, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL ---

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        // Fila de Tarjetas

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setMaximumSize(new Dimension(1200, 150));

        lblIngresos = new JLabel("", SwingConstants.CENTER); // Se llena en actualizarDatos
        lblActivos = new JLabel("0", SwingConstants.CENTER);
        lblMorosos = new JLabel("0", SwingConstants.CENTER);

        pnlCards.add(crearCard("INGRESOS", lblIngresos, new Color(46, 204, 113)));
        pnlCards.add(crearCard("SOCIOS AL DÍA", lblActivos, new Color(52, 152, 219)));
        pnlCards.add(crearCard("MOROSIDAD TOTAL", lblMorosos, new Color(231, 76, 60)));

        pnlCentro.add(pnlCards);
        pnlCentro.add(Box.createRigidArea(new Dimension(0, 30)));


        pnlCentro.add(Box.createVerticalStrut(20)); // Espaciado

        pnlGrafico = new JPanel(new BorderLayout());
        pnlGrafico.setOpaque(false);
        pnlGrafico.setPreferredSize(new Dimension(800, 300));
        pnlGrafico.setMaximumSize(new Dimension(1400, 300));

        pnlCentro.add(pnlGrafico);
        pnlCentro.add(Box.createVerticalStrut(20)); // Espaciado


        // TABLA DE MOROSOS (ESTILO CAJA)

        modeloMorosos = new DefaultTableModel(new String[]{"NOMBRE Y APELLIDO", "DNI", "VENCIMIENTO", "ATRASO"}, 0);
        JTable tablaMorosos = new JTable(modeloMorosos);
        estilizarTablaPrime(tablaMorosos);

        JScrollPane scrollMorosos = new JScrollPane(tablaMorosos);
        scrollMorosos.setPreferredSize(new Dimension(750, 300));
        scrollMorosos.getViewport().setBackground(new Color(25, 25, 25));
        scrollMorosos.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
        scrollMorosos.getVerticalScrollBar().setUnitIncrement(12);

        JLabel lblHist = new JLabel("SOCIOS CON DEUDA PENDIENTE");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHist.setForeground(new Color(120, 120, 120));
        lblHist.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel pnlContenedorTabla = new JPanel(new BorderLayout());
        pnlContenedorTabla.setOpaque(false);
        pnlContenedorTabla.add(lblHist, BorderLayout.NORTH);
        pnlContenedorTabla.add(scrollMorosos, BorderLayout.CENTER);

        pnlCentro.add(pnlContenedorTabla);
        add(pnlCentro, BorderLayout.CENTER);

        actualizarDatos();
    }

    private void generarGrafico() {
        pnlGrafico.removeAll(); // Limpiar antes de redibujar

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        java.util.Map<Integer, Double> ingresos = pagoDAO.obtenerIngresosMensuales();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        for (int i = 1; i <= 12; i++) {
            double monto = ingresos.getOrDefault(i, 0.0);
            dataset.addValue(monto, "Ingresos", meses[i - 1]);
        }

        org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createBarChart(
                "DESEMPEÑO ANUAL", null, null, dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL, false, true, false);

        // --- LLAMADA AL MÉTODO DE ESTILO ---

        estilizarGrafico(chart);

        // --- CONFIGURACIÓN EXTRA PARA ELIMINAR NÚMEROS CIENTÍFICOS ---

        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();

        // Evita el 4E-9 en el eje Y

        rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);

        org.jfree.chart.ChartPanel cp = new org.jfree.chart.ChartPanel(chart);
        cp.setOpaque(false);
        cp.setBackground(new Color(25, 25, 25));

        pnlGrafico.add(cp);
        pnlGrafico.revalidate();
        pnlGrafico.repaint();
    }

    private void actualizarDatos() {
        String seleccion = (String) comboPeriodo.getSelectedItem();
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now();

        // Lógica de filtrado de fechas

        if (seleccion.equals("Esta Semana")) inicio = LocalDate.now().minusDays(7);
        if (seleccion.equals("Este Mes")) inicio = LocalDate.now().withDayOfMonth(1);

        // LLAMADAS ACTUALIZADAS AL DAO

        double recaudado = pagoDAO.obtenerRecaudacionPorPeriodo(inicio, fin);

        // Ahora el desglose respeta el filtro del ComboBox (Hoy, Semana o Mes)

        double[] desglose = pagoDAO.obtenerTotalesPorMetodo(inicio, fin);

        int[] conteos = socioDAO.obtenerConteoEstados();

        lblIngresos.setText("<html><center>$ " + String.format("%.2f", recaudado) +
                "<br><font size='3' color='gray'>Efec: $" + String.format("%.0f", desglose[0]) +
                " | MP: $" + String.format("%.0f", desglose[1]) + "</font></center></html>");

        lblActivos.setText(String.valueOf(conteos[0]));
        lblMorosos.setText(String.valueOf(conteos[1]));

        modeloMorosos.setRowCount(0);
        List<Object[]> morosos = socioDAO.obtenerListaMorosos();
        for (Object[] m : morosos) {
            modeloMorosos.addRow(m);
        }
        generarGrafico();
    }

    private void estilizarTablaPrime(JTable t) {
        t.setBackground(new Color(25, 25, 25));
        t.setForeground(new Color(220, 220, 220));
        t.setRowHeight(35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setSelectionBackground(new Color(231, 76, 60, 100));
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(45, 45, 45));
        t.setShowGrid(true);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(35, 35, 35));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
    }

    private JPanel crearCard(String titulo, JLabel lblValor, Color colorAcento) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(33, 33, 33));
        card.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setForeground(Color.GRAY);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));

        lblValor.setForeground(colorAcento);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    private void estilizarGrafico(org.jfree.chart.JFreeChart chart) {

        // Fondo del gráfico completo

        chart.setBackgroundPaint(new Color(25, 25, 25));
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Configuración del área de dibujo (el Plot)

        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(35, 35, 35)); // Gris oscuro para el fondo de las barras
        plot.setOutlineVisible(false); // Quitamos el borde blanco molesto
        plot.setRangeGridlinePaint(new Color(70, 70, 70)); // Líneas de guía sutiles

        // Configuración de las barras

        org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 140, 0)); // Naranja Prime
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter()); // Quita el brillo glossy
        renderer.setShadowVisible(false); // Quita la sombra para un look más flat y moderno

        // 4. Corrección de los números científicos (Eje Y)

        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits()); // Solo números enteros
        rangeAxis.setAutoRangeIncludesZero(true); // Siempre empieza en $0
        rangeAxis.setTickLabelPaint(Color.WHITE); // Números en blanco
        rangeAxis.setLabelPaint(Color.WHITE);

        // 5. Configuración del Eje X (Meses)

        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
    }

}