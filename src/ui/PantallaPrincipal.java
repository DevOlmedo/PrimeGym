package ui;

import logic.ControlAcceso;
import ui.pestañas.*;
import javax.swing.*;
import java.awt.*;

public class PantallaPrincipal extends JFrame {
    private String usuarioLogueado;

    // Atributos para el buscador y carnet

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

        // onfiguraciones de la ventana

        setTitle("PrimeGym - Panel de Control");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null); // Esto lo centra en la pantalla

        // Inicialización y agregado de componentes

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearPanelCentral(), BorderLayout.CENTER);

        // REFRESCAR Y MOSTRAR

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

        // Panel principal con BorderLayout

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

        // Definición de las opciones

        String[] opciones = {"Acceso", "Socios", "Actividades", "Instructores", "Caja", "Estadísticas", "Market", "Whatsapp"};

        for (String texto : opciones) {

            // Usamos el nombre de la opción para buscar el archivo .png correspondiente

            String nombreArchivo = texto.toLowerCase() + ".png";
            String rutaIcono = "src/assets/" + nombreArchivo;

            JButton btn = crearBotonMenu(" " + texto, rutaIcono);

            // --- AJUSTE DE TAMAÑO ESTÁNDAR ---

            btn.setMinimumSize(new Dimension(240, 55));
            btn.setPreferredSize(new Dimension(240, 55));
            btn.setMaximumSize(new Dimension(240, 55));

            // --- LÓGICA DE NAVEGACIÓN (CardLayout) ---

            btn.addActionListener(e -> {
                // El texto del botón nos sirve como ID de la "carta" a mostrar
                // Usamos .trim() por si el String tiene espacios accidentales
                String nombreCarta = texto.trim();

                // Le pedimos al CardLayout que muestre la pestaña correspondiente
                // Nota: Asegúrate de que 'cardLayout' y 'panelCentral' sean variables de clase en tu PantallaPrincipal
                cardLayout.show(panelCentral, nombreCarta);
            });

            menu.add(btn);

            // Separación vertical entre botones

            menu.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // Empuje elástico hacia arriba

        menu.add(Box.createVerticalGlue());

        return menu;
    }

    // Variables de clase

    private CardLayout cardLayout = new CardLayout();
    private JPanel panelCentral;

    private JPanel crearPanelCentral() {
        panelCentral = new JPanel(cardLayout);
        panelCentral.setBackground(new Color(30, 30, 30));

        // Agregamos las clases reales que ya tienen código

        panelCentral.add(new PanelAcceso(this.control), "Acceso");
        panelCentral.add(new PanelSocios(), "Socios");
        panelCentral.add(new PanelActividades(), "Actividades");
        panelCentral.add(new PanelInstructores(), "Instructores");
        panelCentral.add(new PanelCaja(), "Caja");
        panelCentral.add(new PanelEstadisticas(), "Estadísticas");
        panelCentral.add(new PanelMarket(), "Market");
        panelCentral.add(new PanelWhatsapp(), "Whatsapp");


        return panelCentral;
    }

    private JPanel construirPanelCarnet() {
        JPanel carnet = new JPanel(new BorderLayout());
        carnet.setBackground(new Color(45, 45, 45));
        carnet.setPreferredSize(new Dimension(500, 250));
        carnet.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        carnet.setVisible(false); // Sigue empezando oculto

        // Foto

        lblFotoSocio = new JLabel("FOTO", SwingConstants.CENTER);
        lblFotoSocio.setPreferredSize(new Dimension(180, 0));
        lblFotoSocio.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        lblFotoSocio.setForeground(Color.LIGHT_GRAY);
        carnet.add(lblFotoSocio, BorderLayout.WEST);

        // Datos

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