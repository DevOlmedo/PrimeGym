package ui.pestañas;

import model.Producto;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelMarket extends JPanel {
    private JPanel contenedorProductos;
    private List<Producto> listaProductos;

    public PanelMarket() {
        this.listaProductos = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // Título de la sección

        JLabel titulo = new JLabel("MARKET / STORE", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel donde irán las "cards"

        contenedorProductos = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        contenedorProductos.setBackground(new Color(30, 30, 30));

        // Scroll para cuando haya muchos productos

        JScrollPane scroll = new JScrollPane(contenedorProductos);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Dibujamos el estado inicial (solo el botón +)

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
                        diag.getNombre(),
                        diag.getDescripcion(),
                        Double.parseDouble(diag.getPrecio()),
                        diag.getRutaImagen()
                );

                listaProductos.add(nuevo);
                refrescarPantalla();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.");
            }
        }
    }

    private void agregarProductoVisual(Producto producto) {

        // Panel de la Card
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
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
        card.setPreferredSize(new Dimension(220, 420));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Imagen
        JLabel lblImagen = new JLabel("", SwingConstants.CENTER);
        if (producto.getRutaImagen() != null && !producto.getRutaImagen().isEmpty()) {
            ImageIcon icon = new ImageIcon(producto.getRutaImagen());
            // Cambiamos 180x180 por 180x220 para que sea más vertical
            Image img = icon.getImage().getScaledInstance(180, 220, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
        }
        card.add(lblImagen, BorderLayout.NORTH);

        // Panel de Información (Nombre, Descripción, Precio)

        JPanel infoCentral = new JPanel();
        infoCentral.setLayout(new BoxLayout(infoCentral, BoxLayout.Y_AXIS));
        infoCentral.setOpaque(false);

        // Nombre

        JLabel lblNombre = new JLabel(producto.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Descripción

        JLabel lblDesc = new JLabel("<html><center>" + producto.getDescripcion() + "</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDesc.setForeground(new Color(180, 180, 180));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setMaximumSize(new Dimension(180, 45));

        // Precio

        String precioFormateado = String.format("%.2f", producto.getPrecio());
        JLabel lblPrecio = new JLabel("$ " + precioFormateado);
        lblPrecio.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblPrecio.setForeground(new Color(255, 140, 0));
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Distribución del espacio interno

        infoCentral.add(Box.createVerticalStrut(8)); // Espacio corto después de la imagen
        infoCentral.add(lblNombre);
        infoCentral.add(Box.createVerticalStrut(4));
        infoCentral.add(lblDesc);
        infoCentral.add(Box.createVerticalStrut(4));
        infoCentral.add(lblPrecio);

        card.add(infoCentral, BorderLayout.CENTER);

        // 4. Botones

        JPanel panelAcciones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelAcciones.setOpaque(false);
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        JButton btnEdit = crearBotonAccion("Editar", new Color(80, 80, 80));
        JButton btnDelete = crearBotonAccion("Borrar", new Color(180, 0, 0));

        // Lógica de botones

        btnEdit.addActionListener(e -> {
            Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
            VentanaCargaProducto diag = new VentanaCargaProducto(padre, producto);
            diag.setVisible(true);
            if (diag.isConfirmado()) { refrescarPantalla(); }
        });

        btnDelete.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Borrar?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) { listaProductos.remove(producto); refrescarPantalla(); }
        });

        panelAcciones.add(btnEdit);
        panelAcciones.add(btnDelete);
        card.add(panelAcciones, BorderLayout.SOUTH);

        contenedorProductos.add(card);
    }

    private JButton crearBotonAccion(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(fondo);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void refrescarPantalla() {
        contenedorProductos.removeAll();
        for (Producto p : listaProductos) {
            agregarProductoVisual(p);
        }
        contenedorProductos.add(crearBotonAgregar());
        contenedorProductos.revalidate();
        contenedorProductos.repaint();
    }
}