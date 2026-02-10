package ui;

import logic.ControlAcceso;
import ui.pestañas.*;
import dao.CierreDAO;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PantallaPrincipal extends JFrame {
    private String usuarioLogueado;
    private ControlAcceso control;
    private CardLayout cardLayout = new CardLayout();
    private JPanel panelCentral;

    public PantallaPrincipal(String usuario, ControlAcceso control) {
        this.usuarioLogueado = usuario;
        this.control = control;

        setTitle("PrimeGym - Panel de Control");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearPanelCentral(), BorderLayout.CENTER);

        iniciarAutoCierre();

        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }

    private void iniciarAutoCierre() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            LocalTime ahora = LocalTime.now();
            if (ahora.getHour() == 23 && ahora.getMinute() == 59) {
                new CierreDAO().ejecutarCierre(LocalDate.now(), true);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private JPanel crearBarraSuperior() {
        JPanel panelBarra = new JPanel(new BorderLayout());
        panelBarra.setBackground(new Color(20, 20, 20));
        panelBarra.setPreferredSize(new Dimension(0, 60));
        panelBarra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblCerrar.setForeground(new Color(231, 76, 60));
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
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));

        menu.add(Box.createRigidArea(new Dimension(0, 40)));

        String[] opciones = {"Acceso", "Socios", "Actividades", "Instructores", "Caja", "Estadísticas", "Market", "Whatsapp"};

        for (String texto : opciones) {
            String rutaIcono = "src/assets/" + texto.toLowerCase() + ".png";
            JButton btn = crearBotonMenu(" " + texto, rutaIcono);

            if (texto.equals("Estadísticas")) {
                JPopupMenu popup = new JPopupMenu();
                popup.setBackground(new Color(35, 35, 35));
                popup.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

                JMenuItem itemReportes = crearItemPopup("Reportes Diarios");
                JMenuItem itemMorosos = crearItemPopup("Lista de Morosos");

                // Eventos de navegación del submenú

                itemReportes.addActionListener(e -> cardLayout.show(panelCentral, "Reportes"));
                itemMorosos.addActionListener(e -> cardLayout.show(panelCentral, "Morosos"));

                popup.add(itemReportes);
                popup.add(itemMorosos);

                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        btn.setForeground(new Color(231, 76, 60));
                        popup.show(btn, btn.getWidth() - 1, 0);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        Point p = e.getLocationOnScreen();
                        SwingUtilities.convertPointFromScreen(p, btn);

                        if (p.x < btn.getWidth() - 5) {
                            Timer timer = new Timer(100, ex -> {
                                if (!popup.getVisibleRect().contains(MouseInfo.getPointerInfo().getLocation())) {
                                    btn.setForeground(Color.WHITE);
                                }
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                    }
                });

                popup.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        if (!popup.getBounds().contains(e.getPoint())) {
                            popup.setVisible(false);
                            btn.setForeground(Color.WHITE);
                        }
                    }
                });

                btn.addActionListener(e -> cardLayout.show(panelCentral, "Estadísticas"));
            } else {
                btn.addActionListener(e -> cardLayout.show(panelCentral, texto.trim()));
            }

            menu.add(btn);
            menu.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        menu.add(Box.createVerticalGlue());
        return menu;
    }

    private JPanel crearPanelCentral() {
        panelCentral = new JPanel(cardLayout);
        panelCentral.setBackground(new Color(30, 30, 30));

        // Registro de todos los paneles

        panelCentral.add(new PanelAcceso(), "Acceso");
        panelCentral.add(new PanelSocios(), "Socios");
        panelCentral.add(new PanelActividades(), "Actividades");
        panelCentral.add(new PanelInstructores(), "Instructores");
        panelCentral.add(new PanelCaja(), "Caja");
        panelCentral.add(new PanelEstadisticas(), "Estadísticas");
        panelCentral.add(new PanelReportes(), "Reportes");
        panelCentral.add(new PanelMorosos(), "Morosos"); // <--- Agregado correctamente
        panelCentral.add(new PanelMarket(), "Market");
        panelCentral.add(new PanelWhatsapp(), "Whatsapp");

        return panelCentral;
    }

    private JMenuItem crearItemPopup(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setBackground(new Color(35, 35, 35));
        item.setForeground(Color.WHITE);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setPreferredSize(new Dimension(170, 40));
        item.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                item.setBackground(new Color(65, 65, 65));
                item.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                item.setBackground(new Color(35, 35, 35));
                item.setForeground(Color.WHITE);
            }
        });

        return item;
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

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setForeground(new Color(231, 76, 60));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setForeground(Color.WHITE);
            }
        });

        try {
            ImageIcon icono = new ImageIcon(rutaIcono);
            Image img = icono.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(img));
        } catch (Exception e) {}

        return boton;
    }
}