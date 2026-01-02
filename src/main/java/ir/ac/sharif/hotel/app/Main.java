package ir.ac.sharif.hotel.app;

import ir.ac.sharif.hotel.presentation.cli.CliApp;

public class Main {
    public static void main(String[] args) {
        AppContext.get();
        new CliApp().run();
    }
}
