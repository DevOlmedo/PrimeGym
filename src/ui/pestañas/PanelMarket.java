package ui.pestañas;

import model.Producto;
import ui.dialogos.DialogoConfirmacion;
import ui.dialogos.VentanaCargaProducto;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelMarket extends JPanel {
    private JPanel contenedorProductos;
    private List<Producto> listaProductos;

    public PanelMarket() {
        // CARGA DE DATOS
        this.listaProductos = util.GestorArchivos.cargar();

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

        // Tamaño: ancho de 500 y una altura de 40

        txtBuscador.setPreferredSize(new Dimension(500, 40));
        txtBuscador.setMinimumSize(new Dimension(500, 40));
        txtBuscador.setMaximumSize(new Dimension(500, 40));

        txtBuscador.setBackground(new Color(45, 45, 45));
        txtBuscador.setForeground(Color.WHITE); // Texto en blanco
        txtBuscador.setCaretColor(new Color(255, 140, 0)); // Cursor naranja

        // Fuente: El tamaño de la fuente no puede ser mayor a la altura del campo

        txtBuscador.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        // Padding: Margen interno para que el texto no toque los bordes

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

        // Timer de 150ms.
        // Solo ejecutará la búsqueda cuando el usuario deje de escribir por ese tiempo.

        Timer timerBusqueda = new Timer(150, e -> {
            String texto = txtBuscador.getText().toLowerCase().trim();

            contenedorProductos.removeAll();

            for (model.Producto p : listaProductos) {
                // Filtramos por nombre

                if (p.getNombre().toLowerCase().contains(texto)) {
                    agregarProductoVisual(p);
                }
            }

            // Solo mostramos el botón "+" si no estamos buscando nada

            if (texto.isEmpty()) {
                contenedorProductos.add(crearBotonAgregar());
            }

            contenedorProductos.revalidate();
            contenedorProductos.repaint();
        });

        // Importante: que el timer no se repita solo

        timerBusqueda.setRepeats(false);

        txtBuscador.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { resetearTimer(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { resetearTimer(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { resetearTimer(); }

            private void resetearTimer() {
                // Cada vez que el usuario toca una tecla, reiniciamos la cuenta regresiva
                if (timerBusqueda.isRunning()) {
                    timerBusqueda.restart();
                } else {
                    timerBusqueda.start();
                }
            }
        });

        // 2. DIBUJAMOS EL ESTADO INICIAL

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
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado de componente

        // Descripción

        JLabel lblDesc = new JLabel("<html><div style='text-align: center; width: 160px;'>" + producto.getDescripcion() + "</div></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDesc.setForeground(new Color(180, 180, 180));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setMaximumSize(new Dimension(190, 50));

        // Precio

        String precioFormateado = String.format("%.2f", producto.getPrecio());
        JLabel lblPrecio = new JLabel("$ " + precioFormateado);
        lblPrecio.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblPrecio.setForeground(new Color(255, 140, 0));
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Distribución

        infoCentral.add(Box.createVerticalStrut(8));
        infoCentral.add(lblNombre);
        infoCentral.add(Box.createVerticalStrut(4));
        infoCentral.add(lblDesc);
        infoCentral.add(Box.createVerticalStrut(4));
        infoCentral.add(lblPrecio);

        card.add(infoCentral, BorderLayout.CENTER);

        // Botones

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
            Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);

            // Invocamos nuestro nuevo diálogo

            DialogoConfirmacion dc = new DialogoConfirmacion(padre, "¿Estás seguro de que quieres eliminar este producto?");
            dc.setVisible(true);

            // Si el usuario marcó "ACEPTAR"

            if (dc.getRespuesta()) {
                listaProductos.remove(producto);
                refrescarPantalla();
            }
        });

        panelAcciones.add(btnEdit);
        panelAcciones.add(btnDelete);
        card.add(panelAcciones, BorderLayout.SOUTH);

        contenedorProductos.add(card);
    }

    private JButton crearBotonAccion(String texto, Color fondoBase) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Lógica de colores para Hover y Click

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

        // PERSISTENCIA: Antes de limpiar y redibujar la pantalla,
        // listaProductos en el archivo JSON.

        util.GestorArchivos.guardar(listaProductos);

        // Limpia el contenedor para evitar duplicados al redibujar
        contenedorProductos.removeAll();

        // Recorre la lista que cargamos (o modificamos) y creamos las cards visuales

        for (Producto p : listaProductos) {
            agregarProductoVisual(p);
        }

        // Agregam siempre al final el botón "+" para cargar nuevos productos

        contenedorProductos.add(crearBotonAgregar());

        // Avisamos a Swing que la interfaz cambió y debe refrescarse visualmente

        contenedorProductos.revalidate();
        contenedorProductos.repaint();
    }
}