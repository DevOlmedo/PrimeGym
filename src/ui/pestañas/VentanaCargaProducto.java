package ui.pestañas;

import model.Producto;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class VentanaCargaProducto extends JDialog {
    private JTextField txtNombre, txtPrecio, txtDesc;
    private JButton btnFoto;
    private boolean confirmado = false;
    private String rutaImagen = "";
    private Producto productoParaEditar;

    // Constructor para NUEVO producto

    public VentanaCargaProducto(Frame parent) {
        super(parent, "Nuevo Producto", true);
        configurarVentana();
        inicializarComponentes();
    }

    // Constructor para EDITAR producto existente

    public VentanaCargaProducto(Frame parent, Producto producto) {
        super(parent, "Editar Producto", true);
        this.productoParaEditar = producto;
        configurarVentana();
        inicializarComponentes();
        rellenarCampos(); // Cargamos los datos actuales
    }

    private void configurarVentana() {
        setSize(350, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(45, 45, 45));
    }

    private void rellenarCampos() {
        if (productoParaEditar != null) {
            txtNombre.setText(productoParaEditar.getNombre());
            txtPrecio.setText(Double.toString(productoParaEditar.getPrecio())); // 💰 Conversión de número a texto
            txtDesc.setText(productoParaEditar.getDescripcion());
            this.rutaImagen = productoParaEditar.getRutaImagen();
            btnFoto.setText("✅ Imagen Cargada");
        }
    }

    private void inicializarComponentes() {
        JPanel panelCampos = new JPanel(new GridLayout(0, 1, 10, 10));
        panelCampos.setOpaque(false);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 14);
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

        // --- Campo Descripción ---

        panelCampos.add(crearLabel(" Descripción:", fuenteLabel, colorTexto));
        txtDesc = crearCampo();
        panelCampos.add(txtDesc);

        // --- Botón Final ---

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
        JButton btn = new JButton("GUARDAR CAMBIOS") {
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
            if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y Precio obligatorios");
            } else {
                // Lógica de GUARDAR: ¿Editamos o Creamos?
                if (productoParaEditar != null) {
                    productoParaEditar.setNombre(txtNombre.getText());
                    productoParaEditar.setPrecio(Double.parseDouble(txtPrecio.getText()));
                    productoParaEditar.setDescripcion(txtDesc.getText());
                    productoParaEditar.setRutaImagen(this.rutaImagen);
                }
                confirmado = true;
                dispose();
            }
        });
        return btn;
    }

    // Métodos auxiliares de UI (crearLabel, crearCampo...)

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

    public boolean isConfirmado() { return confirmado; }
    public String getNombre() { return txtNombre.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }
    public String getDescripcion() { return txtDesc.getText(); }
    public String getRutaImagen() { return rutaImagen; }
}