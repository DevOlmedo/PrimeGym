package main;

import ui.VentanaLogin;

public class Main {
    static void main(String[] args) {

        VentanaLogin login = new VentanaLogin();
        login.setVisible(true);
        util.ConexionDB.crearTablas();

    }
}
