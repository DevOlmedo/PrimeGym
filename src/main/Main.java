package main;

import ui.VentanaLogin;

public class Main {
    static void main(String[] args) {

        VentanaLogin login = new VentanaLogin();
        login.setVisible(true);

    }

    /*static void main(String[] args) {

        Socio socio1 = new Socio("Joaquin", "51712775", LocalDate.now().plusDays(7));
        ControlAcceso  control = new ControlAcceso();

        try {
            control.verificarIngreso(socio1);


        } catch (AccesoDenegadoException e) {
            System.out.println("ERROR " + e.getLocalizedMessage());

        }

        VentanaAcceso ventana = new VentanaAcceso(control);
        ventana.setVisible(true);

    }*/
}
