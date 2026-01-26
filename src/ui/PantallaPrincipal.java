package ui;

import logic.AccesoDenegadoException;
import logic.ControlAcceso;
import model.Socio;

import javax.swing.*;
import java.awt.*;

public class PantallaPrincipal extends JFrame {
    private String usuarioLogueado;

    // Atributos para el buscador y carnet 🔍

    private JTextField txtBusqueda;
    private JButton btnBuscar;
    private JPanel panelCarnet;
    private JLabel lblFotoSocio;
    private JLabel lblDatosSocio;
    private JLabel lblEstadoCuota;

    private ControlAcceso control;

    public PantallaPrincipal(String usuario, ControlAcceso control) {
        this.usuarioLogueado = usuario;
        this.control = control;

        // 1. Configuraciones de la ventana

        setTitle("PrimeGym - Panel de Control");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null); // Esto lo centra en la pantalla

        // 2. Inicialización y agregado de componentes

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearPanelCentral(), BorderLayout.CENTER);

        // 3. REFRESCAR Y MOSTRAR ✨

        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }

    private void configurarVentana() {
        setTitle("PrimeGym - Panel de Control");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearPanelCentral(), BorderLayout.CENTER);
    }

    private JPanel crearBarraSuperior() {

        // 1. Panel principal con BorderLayout

        JPanel panelBarra = new JPanel(new BorderLayout());
        panelBarra.setBackground(new Color(20, 20, 20));
        panelBarra.setPreferredSize(new Dimension(0, 60));

        // --- LÍNEA DIVISORIA INFERIOR --- ⚪

        panelBarra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        // --- LADO IZQUIERDO: LOGO --- 🏛️

        JLabel lblLogoTop = new JLabel();
        try {
            ImageIcon iconoOriginal = new ImageIcon("src/assets/logorojo.png");
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(140, 40, Image.SCALE_SMOOTH);
            lblLogoTop.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo: " + e.getMessage());
            lblLogoTop.setText("PRIMEGYM");
            lblLogoTop.setForeground(new Color(200, 0, 0));
            lblLogoTop.setFont(new Font("Segoe UI", Font.BOLD, 20));
        }

        // Margen interno para el logo (separación del borde izquierdo)

        lblLogoTop.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        panelBarra.add(lblLogoTop, BorderLayout.WEST);

        // --- LADO DERECHO: INFO USUARIO Y SALIDA --- 👤

        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        panelUsuario.setOpaque(false);

        // Nombre del Administrador

        JLabel lblNombre = new JLabel(usuarioLogueado.toUpperCase());
        lblNombre.setForeground(new Color(50, 205, 50)); // Verde Lima
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Botón Cerrar Sesión

        JLabel lblCerrar = new JLabel("•  Cerrar sesión");
        lblCerrar.setForeground(Color.WHITE);
        lblCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Lógica de interactividad y navegación

        lblCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new VentanaLogin().setVisible(true);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblCerrar.setForeground(new Color(255, 69, 0)); // Rojo naranja al pasar el mouse
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblCerrar.setForeground(Color.WHITE);
            }
        });

        panelUsuario.add(lblNombre);
        panelUsuario.add(lblCerrar);
        panelBarra.add(panelUsuario, BorderLayout.EAST);

        return panelBarra;
    }

    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(new Color(25, 25, 25));
        menu.setPreferredSize(new Dimension(240, 0));

        // --- LÍNEA DIVISORIA DERECHA --- ⚪

        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));

        // Espaciado inicial arriba

        menu.add(Box.createRigidArea(new Dimension(0, 40)));

        // Navegacion

        String[] opciones = {"Acceso", "Socios", "Actividades", "Instructores", "Caja", "Estadísticas", "Whatsapp"};

        for (String texto : opciones) {

            // --- LÓGICA DE ICONO DINÁMICO --- 🖼️

            String nombreArchivo = texto.toLowerCase() + ".png";
            String rutaIcono = "src/assets/" + nombreArchivo;

            JButton btn = crearBotonMenu(" " + texto, rutaIcono);

            btn.setMinimumSize(new Dimension(240, 55));
            btn.setPreferredSize(new Dimension(240, 55));
            btn.setMaximumSize(new Dimension(240, 55));

            menu.add(btn);

            // Separación vertical entre botones

            menu.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // Empuja todo para arriba
        menu.add(Box.createVerticalGlue());

        return menu;
    }

    private JPanel crearPanelCentral() {
        JPanel panelPrincipalCentro = new JPanel(new GridBagLayout());
        panelPrincipalCentro.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10); // Más aire entre componentes
        gbc.gridx = 0;

        // 1. Título 🏛️

        JLabel lblTitulo = new JLabel("CONTROL DE ACCESO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Un poco más grande
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridy = 0;
        panelPrincipalCentro.add(lblTitulo, gbc);

        // 2. Buscador Estilizado 🔍

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBuscador.setOpaque(false);

        // Campo de texto: Estilo "Línea"

        txtBusqueda = new JTextField(20);
        txtBusqueda.setPreferredSize(new Dimension(350, 45));
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtBusqueda.setBackground(new Color(30, 30, 30));
        txtBusqueda.setForeground(Color.WHITE);
        txtBusqueda.setCaretColor(Color.WHITE);

        // Solo borde inferior

        txtBusqueda.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 0, 0)));
        txtBusqueda.setToolTipText("Ingrese DNI del socio");

        // Botón Verificar

        btnBuscar = new JButton("VERIFICAR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Si el mouse está encima, usamos un rojo más brillante
                if (getModel().isArmed()) {
                    g2.setColor(new Color(150, 0, 0));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(220, 0, 0));
                } else {
                    g2.setColor(new Color(180, 0, 0)); // Rojo base
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btnBuscar.setPreferredSize(new Dimension(140, 45));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Configuraciones necesarias para el botón personalizado

        btnBuscar.setContentAreaFilled(false);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setOpaque(false);

        panelBuscador.add(txtBusqueda);
        panelBuscador.add(btnBuscar);

        gbc.gridy = 1;
        panelPrincipalCentro.add(panelBuscador, gbc);

        // 3. El Carnet

        panelCarnet = construirPanelCarnet();
        gbc.gridy = 2;
        panelPrincipalCentro.add(panelCarnet, gbc);

        // --- LÓGICA DE INTEGRACIÓN DINÁMICA --- ⚡

        btnBuscar.addActionListener(e -> {
            try {
                String dni = txtBusqueda.getText();

                // Simulación de socio

                Socio socioPrueba = new Socio("RIQUELME ANA", dni, java.time.LocalDate.now().plusDays(10));

                this.control.verificarIngreso(socioPrueba);

                String infoSocio = "<html><body style='color:white; padding:20px; font-family:Segoe UI;'>"
                        + "<h1 style='margin:0; font-size:22px;'>" + socioPrueba.getNombre() + "</h1>"
                        + "<p style='margin:10px 0 5px 0; font-size:16px; color:#CCCCCC;'>"
                        + "<b style='color:#E1AD01;'>DNI:</b> " + socioPrueba.getDni() + "</p>"
                        + "<p style='margin:0; font-size:16px; color:#CCCCCC;'>"
                        + "<b style='color:#E1AD01;'>Vencimiento:</b> " + socioPrueba.getFechaVencimiento() + "</p>"
                        + "</body></html>";

                lblDatosSocio.setText(infoSocio);
                lblEstadoCuota.setText("CUOTA AL DÍA");
                lblEstadoCuota.setBackground(new Color(0, 150, 0)); // Verde éxito

            } catch (AccesoDenegadoException ex) {
                lblEstadoCuota.setText("CUOTA VENCIDA");
                lblEstadoCuota.setBackground(new Color(180, 0, 0)); // Rojo alerta
            }

            panelCarnet.setVisible(true);
            panelPrincipalCentro.revalidate();
            panelPrincipalCentro.repaint();
        });

        return panelPrincipalCentro;
    }

    private JPanel construirPanelCarnet() {
        JPanel carnet = new JPanel(new BorderLayout());
        carnet.setBackground(new Color(45, 45, 45));
        carnet.setPreferredSize(new Dimension(500, 250));
        carnet.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        carnet.setVisible(false); // Sigue empezando oculto 🕵️

        // Foto 📷

        lblFotoSocio = new JLabel("FOTO", SwingConstants.CENTER);
        lblFotoSocio.setPreferredSize(new Dimension(180, 0));
        lblFotoSocio.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        lblFotoSocio.setForeground(Color.LIGHT_GRAY);
        carnet.add(lblFotoSocio, BorderLayout.WEST);

        // Datos 📝

        lblDatosSocio = new JLabel();
        carnet.add(lblDatosSocio, BorderLayout.CENTER);

        // Franja de Estado 🟥/🟩

        lblEstadoCuota = new JLabel("", SwingConstants.CENTER); // Empezamos sin texto
        lblEstadoCuota.setOpaque(true);
        lblEstadoCuota.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblEstadoCuota.setPreferredSize(new Dimension(0, 50));
        carnet.add(lblEstadoCuota, BorderLayout.SOUTH);

        return carnet;
    }

    private JButton crearBotonMenu(String texto, String rutaIcono) {
        JButton boton = new JButton(texto);

        // Configuración de Estilo Base

        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(200, 60));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // Margen interno izquierdo
        boton.setIconTextGap(15);

        // Colores y Fuentes iniciales

        Color colorOriginal = Color.WHITE;
        Color colorHover = new Color(200, 0, 0); // Rojo PrimeGym
        Font fuenteOriginal = new Font("Segoe UI", Font.BOLD, 18);
        Font fuenteHover = new Font("Segoe UI", Font.BOLD, 20); // Un poco más grande al pasar el mouse

        boton.setForeground(colorOriginal);
        boton.setFont(fuenteOriginal);

        // Transparencia y Limpieza visual

        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto INTERACTIVO (Mouse Hover)

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setForeground(colorHover);
                boton.setFont(fuenteHover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setForeground(colorOriginal);
                boton.setFont(fuenteOriginal);
            }
        });

        // Gestión de Iconos

        try {
            ImageIcon icono = new ImageIcon(rutaIcono);
            Image img = icono.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("No se encontró el icono en: " + rutaIcono);
        }

        return boton;
    }
}