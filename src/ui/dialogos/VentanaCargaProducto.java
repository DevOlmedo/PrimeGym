package ui.dialogos;

import model.Producto;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class VentanaCargaProducto extends JDialog {
    private JTextField txtNombre, txtPrecio, txtDesc, txtStock; // Nuevo: txtStock
    private JButton btnFoto;
    private boolean confirmado = false;
    private String rutaImagen = "";
    private Producto productoParaEditar;

    public VentanaCargaProducto(Frame parent) {
        super(parent, "Nuevo Producto", true);
        configurarVentana();
        inicializarComponentes();
    }

    public VentanaCargaProducto(Frame parent, Producto producto) {
        super(parent, "Editar Producto", true);
        this.productoParaEditar = producto;
        configurarVentana();
        inicializarComponentes();
        rellenarCampos();
    }

    private void configurarVentana() {
        setSize(380, 550); // Ajustado para el nuevo campo
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(45, 45, 45));
    }

    private void rellenarCampos() {
        if (productoParaEditar != null) {
            txtNombre.setText(productoParaEditar.getNombre());
            txtPrecio.setText(Double.toString(productoParaEditar.getPrecio()));
            txtDesc.setText(productoParaEditar.getDescripcion());
            txtStock.setText(Integer.toString(productoParaEditar.getStock())); // Carga el stock actual
            this.rutaImagen = productoParaEditar.getRutaImagen();
            btnFoto.setText("✅ Imagen Cargada");
        }
    }

    private void inicializarComponentes() {
        JPanel panelCampos = new JPanel(new GridLayout(0, 1, 5, 5));
        panelCampos.setOpaque(false);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 13);
        Color colorTexto = Color.WHITE;

        // --- Campo Imagen ---
        btnFoto = new JButton("Seleccionar Imagen");
        btnFoto.addActionListener(e -> seleccionarImagen());
        panelCampos.add(crearLabel(" Imagen del producto:", fuenteLabel, colorTexto));
        panelCampos.add(btnFoto);

        // --- Campo Nombre ---
        panelCampos.add(crearLabel(" Nombre del producto:", fuenteLabel, colorTexto));
        txtNombre = crearCampo();
        panelCampos.add(txtNombre);

        // --- Campo Precio ---
        panelCampos.add(crearLabel(" Precio ($):", fuenteLabel, colorTexto));
        txtPrecio = crearCampo();
        panelCampos.add(txtPrecio);

        // --- Campo Stock ---

        panelCampos.add(crearLabel(" Stock Inicial:", fuenteLabel, colorTexto));
        txtStock = crearCampo();
        txtStock.setText("0"); // Valor por defecto
        panelCampos.add(txtStock);

        // --- Campo Descripción ---

        panelCampos.add(crearLabel(" Descripción:", fuenteLabel, colorTexto));
        txtDesc = crearCampo();
        panelCampos.add(txtDesc);

        JButton btnFinal = crearBotonConfirmar();

        add(panelCampos, BorderLayout.CENTER);
        add(btnFinal, BorderLayout.SOUTH);
    }

    private void seleccionarImagen() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.rutaImagen = selector.getSelectedFile().getAbsolutePath();
            btnFoto.setText("✅ " + selector.getSelectedFile().getName());
        }
    }

    private JButton crearBotonConfirmar() {
        JButton btn = new JButton("GUARDAR PRODUCTO") {
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);

        btn.addActionListener(e -> {

            // Validación de campos obligatorios

            if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre, Precio y Stock son obligatorios.");
            } else {
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
                    JOptionPane.showMessageDialog(this, "Precio y Stock deben ser números válidos.");
                }
            }
        });
        return btn;
    }

    private JLabel crearLabel(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private JTextField crearCampo() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(60, 60, 60));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return tf;
    }

    // Getters actualizados para recuperar la info desde PanelMarket

    public boolean isConfirmado() { return confirmado; }
    public String getNombre() { return txtNombre.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }
    public String getStock() { return txtStock.getText(); } // Nuevo Getter
    public String getDescripcion() { return txtDesc.getText(); }
    public String getRutaImagen() { return rutaImagen; }
}