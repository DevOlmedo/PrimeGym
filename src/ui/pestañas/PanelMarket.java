package ui.pestañas;

import dao.ProductoDAO;
import model.Producto;
import ui.dialogos.DialogoConfirmacion;
import ui.dialogos.DialogoVenta;
import ui.dialogos.VentanaCargaProducto;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelMarket extends JPanel {
    private JPanel contenedorProductos;
    private ProductoDAO productoDAO = new ProductoDAO(); // Nuevo objeto
    private List<Producto> listaProductos;

    public PanelMarket() {

        // CARGA DE DATOS

        this.listaProductos = productoDAO.obtenerTodos();

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // --- BLOQUE DEL BUSCADOR ---

        // Panel superior que contendrá Título + Buscador

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);

        // Título

        JLabel titulo = new JLabel("MARKET / STORE", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Campo de texto del buscador

        JTextField txtBuscador = new JTextField();

        // Tamaño

        txtBuscador.setPreferredSize(new Dimension(500, 40));
        txtBuscador.setMinimumSize(new Dimension(500, 40));
        txtBuscador.setMaximumSize(new Dimension(500, 40));

        txtBuscador.setBackground(new Color(45, 45, 45));
        txtBuscador.setForeground(Color.WHITE);
        txtBuscador.setCaretColor(new Color(255, 140, 0)); // Cursor naranja

        // Fuente: El tamaño de la fuente no puede ser mayor a la altura del campo

        txtBuscador.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        txtBuscador.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15) // 15px de espacio a los lados
        ));

        // Panel para centrar el buscador horizontalmente

        JPanel panelBarra = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBarra.setOpaque(false);
        JLabel lblIcono = new JLabel("🔍 "); // Un emoji simple o icono
        lblIcono.setForeground(Color.GRAY);
        panelBarra.add(lblIcono);
        panelBarra.add(txtBuscador);

        // Panel norte

        panelNorte.add(titulo);
        panelNorte.add(panelBarra);
        panelNorte.add(Box.createVerticalStrut(15)); // Espacio antes de los productos

        add(panelNorte, BorderLayout.NORTH);

        // --- PANEL DE PRODUCTOS ---

        contenedorProductos = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        contenedorProductos.setBackground(new Color(30, 30, 30));

        JScrollPane scroll = new JScrollPane(contenedorProductos);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // --- LÓGICA DEL BUSCADOR (CON DEBOUNCING PARA EVITAR DELAY) ---


        Timer timerBusqueda = new Timer(150, e -> {
            String texto = txtBuscador.getText().toLowerCase().trim();

            contenedorProductos.removeAll();

            for (model.Producto p : listaProductos) {

                // Filtra por nombre

                if (p.getNombre().toLowerCase().contains(texto)) {
                    agregarProductoVisual(p);
                }
            }

            if (texto.isEmpty()) {
                contenedorProductos.add(crearBotonAgregar());
            }

            contenedorProductos.revalidate();
            contenedorProductos.repaint();
        });

        timerBusqueda.setRepeats(false);

        txtBuscador.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { resetearTimer(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { resetearTimer(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { resetearTimer(); }

            private void resetearTimer() {

                // Cada vez que el usuario toca una tecla, reinicia la cuenta regresiva

                if (timerBusqueda.isRunning()) {
                    timerBusqueda.restart();
                } else {
                    timerBusqueda.start();
                }
            }
        });

        // DIBUJA EL ESTADO INICIAL

        refrescarPantalla();
    }

    private JButton crearBotonAgregar() {
        JButton btn = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(80, 80, 80) : new Color(60, 60, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(220, 380)); // Mismo tamaño que las cards
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        btn.setForeground(new Color(180, 180, 180));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> abrirVentanaCarga());
        return btn;
    }

    private void abrirVentanaCarga() {
        Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
        VentanaCargaProducto diag = new VentanaCargaProducto(padre);
        diag.setVisible(true);

        if (diag.isConfirmado()) {
            try {
                Producto nuevo = new Producto(
                        0,
                        diag.getNombre(),
                        diag.getDescripcion(),
                        Double.parseDouble(diag.getPrecio()),
                        Integer.parseInt(diag.getStock()),
                        diag.getRutaImagen()
                );

                if (productoDAO.guardar(nuevo)) { // Guardar en DB
                    refrescarPantalla();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio y Stock deben ser números.");
            }
        }
    }

    private void agregarProductoVisual(Producto producto) {

        // Panel principal de la tarjeta

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 45, 45));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(240, 480)); // Reducimos la altura total
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- IMAGEN ---

        JLabel lblImagen = new JLabel("", SwingConstants.CENTER);
        if (producto.getRutaImagen() != null && !producto.getRutaImagen().isEmpty()) {
            ImageIcon icon = new ImageIcon(producto.getRutaImagen());
            Image img = icon.getImage().getScaledInstance(170, 180, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
        }
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(lblImagen, gbc);

        // --- TEXTOS (Nombre, Desc, Precio, Stock) ---

        JLabel lblNombre = new JLabel("<html><body style='width: 160px; text-align: center;'>"
                + producto.getNombre() + "</body></html>", SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombre.setForeground(Color.WHITE);

        // Configuración para el nombre

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0); // Un pequeño margen inferior
        card.add(lblNombre, gbc);

        JLabel lblDesc = new JLabel("<html><body style='width: 150px; text-align: center;'>"
                + producto.getDescripcion() + "</body></html>", SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(170, 170, 170));
        gbc.gridy = 2;
        card.add(lblDesc, gbc);

        JLabel lblPrecio = new JLabel("$ " + String.format("%.2f", producto.getPrecio()), SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblPrecio.setForeground(new Color(255, 140, 0));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        card.add(lblPrecio, gbc);

        JLabel lblStock = new JLabel("Stock: " + producto.getStock(), SwingConstants.CENTER);
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStock.setForeground(producto.getStock() > 0 ? Color.GRAY : new Color(255, 80, 80));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        card.add(lblStock, gbc);

        // --- BOTÓN VENDER ---

        JButton btnVender = crearBotonAccion("VENDER", new Color(46, 204, 113));
        btnVender.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVender.setPreferredSize(new Dimension(0, 45));

        // Validación visual de stock antes de intentar la venta

        if (producto.getStock() <= 0) {
            btnVender.setEnabled(false);
            btnVender.setText("SIN STOCK");
        }

        btnVender.addActionListener(e -> {
            Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
            DialogoVenta dv = new DialogoVenta(padre, producto);
            dv.setVisible(true);

            // Si el usuario confirmó la venta y hay stock disponible en la DB

            if (dv.getRespuesta()) {

                // Capta el método de pago elegido (Efectivo o Mercado Pago)

                String metodoElegido = dv.getMetodoSeleccionado();

                if (productoDAO.reducirStock(producto.getId(), 1)) {

                    // Registra el pago detallando el origen para las estadísticas

                    new dao.PagoDAO().registrarPago(0, producto.getPrecio(), "Market (" + metodoElegido + ")");

                    // Actualiza la interfaz para mostrar el nuevo stock y los botones correctamente

                    refrescarPantalla();
                }
            }
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(btnVender, gbc);

        // --- BOTONES ADMIN (Editar / Borrar) ---

        JPanel pnlAdmin = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlAdmin.setOpaque(false);

        JButton btnEdit = crearBotonAccion("Editar", new Color(70, 70, 70));
        JButton btnDelete = crearBotonAccion("Borrar", new Color(150, 0, 0));

        // Listeners corregidos para asegurar ejecución

        btnEdit.addActionListener(e -> {
            Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
            VentanaCargaProducto diag = new VentanaCargaProducto(padre, producto);
            diag.setVisible(true);
            if (diag.isConfirmado()) {
                productoDAO.editar(producto);
                refrescarPantalla();
            }
        });

        btnDelete.addActionListener(e -> {
            Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
            DialogoConfirmacion dc = new DialogoConfirmacion(padre, "¿Eliminar este producto?");
            dc.setVisible(true);
            if (dc.getRespuesta() && productoDAO.eliminar(producto.getId())) {
                refrescarPantalla();
            }
        });

        pnlAdmin.add(btnEdit);
        pnlAdmin.add(btnDelete);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(pnlAdmin, gbc);

        contenedorProductos.add(card);
    }

    private JButton crearBotonAccion(String texto, Color fondoBase) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(fondoBase.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(fondoBase.brighter()); // Se ilumina al pasar el mouse
                } else {
                    g2.setColor(fondoBase);
                }

                // fondo redondeado

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Estilos para quitar el diseño por defecto de Java

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); // Importante para que no se vea el fondo cuadrado original
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void refrescarPantalla() {

        // Traemos la lista actualizada desde SQL

        this.listaProductos = productoDAO.obtenerTodos();

        contenedorProductos.removeAll();

        for (Producto p : listaProductos) {
            agregarProductoVisual(p);
        }

        contenedorProductos.add(crearBotonAgregar());
        contenedorProductos.revalidate();
        contenedorProductos.repaint();
    }
}