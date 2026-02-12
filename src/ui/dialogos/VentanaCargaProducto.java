package ui.dialogos;

import model.Producto;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaCargaProducto extends JDialog {
    private JTextField txtNombre, txtPrecio, txtDesc, txtStock;
    private JButton btnFoto;
    private boolean confirmado = false;
    private String rutaImagen = "";
    private Producto productoParaEditar;

    public VentanaCargaProducto(Frame parent) {
        this(parent, null);
    }

    public VentanaCargaProducto(Frame parent, Producto producto) {
        super(parent, true);
        this.productoParaEditar = producto;

        setUndecorated(true);
        setSize(400, 580);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        inicializarComponentes();
        if (productoParaEditar != null) rellenarCampos();
    }

    private void rellenarCampos() {
        txtNombre.setText(productoParaEditar.getNombre());
        txtPrecio.setText(Double.toString(productoParaEditar.getPrecio()));
        txtDesc.setText(productoParaEditar.getDescripcion());
        txtStock.setText(Integer.toString(productoParaEditar.getStock()));
        this.rutaImagen = productoParaEditar.getRutaImagen();
        btnFoto.setText("✅ Imagen Cargada");
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 35, 35)); // Fondo oscuro Prime
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(70, 70, 70)); // Borde gris sutil
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);

        // --- Título ---

        JLabel lblTitulo = new JLabel(productoParaEditar == null ? "NUEVO PRODUCTO" : "EDITAR PRODUCTO", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(180, 0, 0));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        JPanel panelCampos = new JPanel(new GridLayout(0, 1, 5, 8));
        panelCampos.setOpaque(false);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 13);
        Color colorTexto = Color.GRAY;

        // --- Campos Estilizados ---

        btnFoto = crearBotonEstilo("Seleccionar Imagen", new Color(60, 60, 60));
        btnFoto.addActionListener(e -> seleccionarImagen());

        panelCampos.add(crearLabel(" Imagen del producto:", fuenteLabel, colorTexto));
        panelCampos.add(btnFoto);
        panelCampos.add(crearLabel(" Nombre:", fuenteLabel, colorTexto));
        txtNombre = crearCampo();
        panelCampos.add(txtNombre);
        panelCampos.add(crearLabel(" Precio ($):", fuenteLabel, colorTexto));
        txtPrecio = crearCampo();
        panelCampos.add(txtPrecio);
        panelCampos.add(crearLabel(" Stock Inicial:", fuenteLabel, colorTexto));
        txtStock = crearCampo();
        txtStock.setText("0");
        panelCampos.add(txtStock);
        panelCampos.add(crearLabel(" Descripción:", fuenteLabel, colorTexto));
        txtDesc = crearCampo();
        panelCampos.add(txtDesc);

        // --- Botones de Acción Inferiores ---

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
        if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
            new DialogoAviso(f, "⚠ Nombre, Precio y Stock son obligatorios.").setVisible(true);
            return;
        }
        try {
            if (productoParaEditar != null) {
                productoParaEditar.setNombre(txtNombre.getText());
                productoParaEditar.setPrecio(Double.parseDouble(txtPrecio.getText()));
                productoParaEditar.setStock(Integer.parseInt(txtStock.getText()));
                productoParaEditar.setDescripcion(txtDesc.getText());
                productoParaEditar.setRutaImagen(this.rutaImagen);
            }
            confirmado = true;
            dispose();
        } catch (NumberFormatException ex) {
            new DialogoAviso(f, "⚠ Precio y Stock deben ser números válidos.").setVisible(true);
        }
    }

    private void seleccionarImagen() {
        FileDialog fd = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Seleccionar Imagen", FileDialog.LOAD);

        // Filtro para que solo se vean archivos de imagen

        fd.setFile("*.jpg;*.jpeg;*.png");
        fd.setVisible(true);

        if (fd.getFile() != null) {
            this.rutaImagen = fd.getDirectory() + fd.getFile();
            btnFoto.setText("✅ " + fd.getFile());
        }
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

    public boolean isConfirmado() { return confirmado; }
    public String getNombre() { return txtNombre.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }
    public String getStock() { return txtStock.getText(); }
    public String getDescripcion() { return txtDesc.getText(); }
    public String getRutaImagen() { return rutaImagen; }
}