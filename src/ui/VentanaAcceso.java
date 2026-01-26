package ui;

import logic.AccesoDenegadoException;
import logic.ControlAcceso;
import model.Socio;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class VentanaAcceso extends JFrame {

    private ControlAcceso control;

    private JLabel etiquetaDni;
    private JTextField campoDni;
    private JButton botonVerificar;

    public VentanaAcceso(ControlAcceso controlRecibido) {
        this.control = controlRecibido;
        configurarVentana();
        inicializarComponentes();
    }
    private void configurarVentana() {
        setTitle("PrimeGym - Control de Acceso");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());
        getContentPane().setBackground(new Color(45, 45, 45));
    }

    private void inicializarComponentes() {

        // Configuracion de la etiqueta

        etiquetaDni = new JLabel("DNI del Socio:");
        etiquetaDni.setForeground(Color.LIGHT_GRAY);
        etiquetaDni.setFont(new Font("Arial", Font.BOLD, 15));

        // Configuracion del campo de texto

        campoDni = new JTextField(15);

        // Configuracion del botón

        botonVerificar = new JButton("Verificar");
        botonVerificar.setBackground(Color.DARK_GRAY);
        botonVerificar.setForeground(Color.WHITE);

        add(etiquetaDni);
        add(campoDni);
        add(botonVerificar);

        botonVerificar.addActionListener(e -> {
            try {
                // 1. Tomamos el DNI del cuadro de texto
                String dni = campoDni.getText();
                // 2. Creamos un socio de prueba con ESE dni
                // Le ponemos una fecha de vencimiento pasada para que salte el error
                Socio socioPrueba = new Socio("Socio de Prueba", dni, LocalDate.now().plusDays(10));

                // 3. Usamos el "motor" (this.control) que recibimos del Main
                this.control.verificarIngreso(socioPrueba);

                // Si la línea de arriba no lanza error, mostramos éxito
                JOptionPane.showMessageDialog(this, "¡Acceso Permitido!");

            } catch (AccesoDenegadoException ex) {
                // Si hay error, mostramos el mensaje de tu excepción
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


}