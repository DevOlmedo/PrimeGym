package ui;

import logic.ControlAcceso;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class VentanaLogin extends JFrame {

    // Atributos de clase: para que el botón pueda leer los datos después
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public VentanaLogin() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("PrimeGym - Inicio de Sesión");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Dividimos la ventana en dos columnas iguales
        setLayout(new GridLayout(1, 2));
    }

    private void inicializarComponentes() {
        // Añadimos los dos paneles que creamos por separado
        add(crearPanelIzquierdo());
        add(crearPanelDerecho());
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE); // Negro profundo 🌑
        panel.setLayout(new GridBagLayout()); // Centra el contenido automáticamente

        JLabel imagenLogo = new JLabel();
        // Verifica que la imagen esté en src/ui/assets/
        ImageIcon icon = new ImageIcon("src/assets/logorojo.png");
        imagenLogo.setIcon(icon);

        panel.add(imagenLogo);
        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 40)); // Gris oscuro 🌑 new Color(40, 40, 40)
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Configuración de márgenes y alineación
        gbc.insets = new Insets(10, 40, 10, 40); // Espaciado lateral
        gbc.fill = GridBagConstraints.HORIZONTAL; // Que los campos ocupen el ancho
        gbc.gridx = 0;
// --- SECCIÓN DE USUARIO ---

// 1. Etiqueta de texto (arriba)
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 1; // Columna 1 (derecha)
        gbc.gridy = 0; // Fila 0
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST; // Alineado a la izquierda
        gbc.insets = new Insets(20, 0, 5, 40);
        panel.add(lblUsuario, gbc);

// --- FILA DEL CAMPO (Icono + Línea) ---

// 2. El Icono 👤 (a la izquierda de la línea)
        JLabel icoUser = new JLabel();
        ImageIcon iconoUser = new ImageIcon("src/assets/usuario.png");
        Image imgUser = iconoUser.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        icoUser.setIcon(new ImageIcon(imgUser));

        gbc.gridx = 0; // Columna 0 (izquierda)
        gbc.gridy = 1; // Fila 1
        gbc.insets = new Insets(0, 40, 10, 5); // Margen derecho pequeño para separar de la línea
        panel.add(icoUser, gbc);

// 3. El Campo de Usuario (la línea blanca)
        txtUsuario = new JTextField(15);
        txtUsuario.setBackground(new Color(40, 40, 40));
        txtUsuario.setForeground(Color.WHITE);
        txtUsuario.setCaretColor(Color.WHITE);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));

        gbc.gridx = 1; // Columna 1 (derecha)
        gbc.gridy = 1; // Misma fila 1 que el icono
        gbc.insets = new Insets(0, 0, 10, 40);
        panel.add(txtUsuario, gbc);

// --- SECCIÓN DE CONTRASEÑA ---

// 3. Etiqueta de texto (arriba)
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 1; // Columna 1 (derecha)
        gbc.gridy = 2; // Fila 2
        gbc.insets = new Insets(20, 0, 5, 40); // Espacio superior para separar del bloque de arriba
        panel.add(lblPass, gbc);

// --- FILA DEL CAMPO (Icono + Línea) ---

// 4. El Icono 🔑 (a la izquierda de la línea)
        JLabel icoPass = new JLabel();
        ImageIcon iconoPass = new ImageIcon("src/assets/contraseña.png"); // Verifica que el nombre coincida
        Image imgPass = iconoPass.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        icoPass.setIcon(new ImageIcon(imgPass));

        gbc.gridx = 0; // Columna 0 (izquierda)
        gbc.gridy = 3; // Fila 3
        gbc.insets = new Insets(0, 40, 10, 5);
        panel.add(icoPass, gbc);

// 5. El Campo de Contraseña (la línea blanca)
        txtPassword = new JPasswordField(15);
        txtPassword.setBackground(new Color(40, 40, 40));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));

        gbc.gridx = 1; // Columna 1 (derecha)
        gbc.gridy = 3; // Misma fila 3 que el icono
        gbc.insets = new Insets(0, 0, 10, 40);
        panel.add(txtPassword, gbc);

// --- SECCIÓN DEL BOTÓN ESTILIZADO ---

        btnIngresar = new JButton("Ingresar");

// 1. Colores y Fuente 🖋️
        btnIngresar.setBackground(new Color(150, 0, 0)); // Un rojo un poco más sobrio
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Letra un poco más pequeña y fina

// 2. Quitar lo "feo" de Windows 🔨
        btnIngresar.setFocusPainted(false); // Quita el borde azul interno al hacer clic
        btnIngresar.setBorderPainted(false); // Quitamos el borde cuadrado por defecto
        btnIngresar.setContentAreaFilled(true); // Permite que veamos el color de fondo
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

// 3. Efecto "Hover" (Cambio de color al pasar el mouse) 🖱️
        btnIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnIngresar.setBackground(new Color(190, 0, 0)); // Rojo más brillante al entrar
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnIngresar.setBackground(new Color(150, 0, 0)); // Vuelve al original al salir
            }
        });

// 4. Ubicación en el panel 📏
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Que ocupe el ancho para que se vea más imponente
        gbc.insets = new Insets(40, 40, 10, 40);
        gbc.ipady = 15; // Esto le da "grosor" al botón (espacio interno arriba y abajo)
        panel.add(btnIngresar, gbc);

// ActionListener (Lógica de acceso)
        btnIngresar.addActionListener(e -> {
            String usuario = txtUsuario.getText();

            // 1. Capturamos como arreglo de caracteres (Seguro 🔒)
            char[] passwordIngresada = txtPassword.getPassword();
            char[] passwordCorrecta = "123".toCharArray();

            // 2. Comparamos usando la herramienta Arrays
            if (usuario.equals("admin") && Arrays.equals(passwordIngresada, passwordCorrecta)) {
                this.dispose();
                ControlAcceso control = new ControlAcceso();
                PantallaPrincipal principal = new PantallaPrincipal(usuario, control);
                principal.setVisible(true);
            } else {
                // Opcional: mostrar un error si falló
                System.out.println("Usuario o clave incorrectos");
            }

            // 3. LIMPIEZA FINAL: Borramos la clave de la memoria 🧹
            Arrays.fill(passwordIngresada, '0');
        });

        return panel;
    }
}