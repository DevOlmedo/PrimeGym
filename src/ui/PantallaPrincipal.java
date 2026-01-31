package ui;

import logic.ControlAcceso;
import ui.pestañas.*;
import javax.swing.*;
import java.awt.*;

public class PantallaPrincipal extends JFrame {
    private String usuarioLogueado;
    private ControlAcceso control;

    // Variables de clase para la navegación

    private CardLayout cardLayout = new CardLayout();
    private JPanel panelCentral;

    public PantallaPrincipal(String usuario, ControlAcceso control) {
        this.usuarioLogueado = usuario;
        this.control = control;

        // Configuraciones de la ventana

        setTitle("PrimeGym - Panel de Control");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);

        // Inicialización y agregado de componentes

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearPanelCentral(), BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }

    // --- MÉTODOS DE CONSTRUCCIÓN DE INTERFAZ ---

    private JPanel crearBarraSuperior() {
        JPanel panelBarra = new JPanel(new BorderLayout());
        panelBarra.setBackground(new Color(20, 20, 20));
        panelBarra.setPreferredSize(new Dimension(0, 60));
        panelBarra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        // Logo

        JLabel lblLogoTop = new JLabel();
        try {
            ImageIcon iconoOriginal = new ImageIcon("src/assets/logorojo.png");
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(140, 40, Image.SCALE_SMOOTH);
            lblLogoTop.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            lblLogoTop.setText("PRIMEGYM");
            lblLogoTop.setForeground(new Color(200, 0, 0));
            lblLogoTop.setFont(new Font("Segoe UI", Font.BOLD, 20));
        }
        lblLogoTop.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        panelBarra.add(lblLogoTop, BorderLayout.WEST);

        // Info Usuario

        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        panelUsuario.setOpaque(false);

        JLabel lblNombre = new JLabel(usuarioLogueado.toUpperCase());
        lblNombre.setForeground(new Color(50, 205, 50));
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblCerrar = new JLabel("•  Cerrar sesión");
        lblCerrar.setForeground(Color.WHITE);
        lblCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new VentanaLogin().setVisible(true);
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
        menu.setBackground(new Color(255, 25, 25, 0)); // Ajuste de opacidad
        menu.setBackground(new Color(25, 25, 25));
        menu.setPreferredSize(new Dimension(240, 0));
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));

        menu.add(Box.createRigidArea(new Dimension(0, 40)));

        String[] opciones = {"Acceso", "Socios", "Actividades", "Instructores", "Caja", "Estadísticas", "Market", "Whatsapp"};

        for (String texto : opciones) {
            String rutaIcono = "src/assets/" + texto.toLowerCase() + ".png";
            JButton btn = crearBotonMenu(" " + texto, rutaIcono);

            btn.addActionListener(e -> cardLayout.show(panelCentral, texto.trim()));

            menu.add(btn);
            menu.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        menu.add(Box.createVerticalGlue());
        return menu;
    }

    private JPanel crearPanelCentral() {
        panelCentral = new JPanel(cardLayout);
        panelCentral.setBackground(new Color(30, 30, 30));

        panelCentral.add(new PanelAcceso(), "Acceso");

        panelCentral.add(new PanelSocios(), "Socios");
        panelCentral.add(new PanelActividades(), "Actividades");
        panelCentral.add(new PanelInstructores(), "Instructores");
        panelCentral.add(new PanelCaja(), "Caja");
        panelCentral.add(new PanelEstadisticas(), "Estadísticas");
        panelCentral.add(new PanelMarket(), "Market");
        panelCentral.add(new PanelWhatsapp(), "Whatsapp");

        return panelCentral;
    }

    private JButton crearBotonMenu(String texto, String rutaIcono) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(240, 55));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        boton.setIconTextGap(15);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon icono = new ImageIcon(rutaIcono);
            Image img = icono.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(img));
        } catch (Exception e) {}

        return boton;
    }
}