package br.com;

import br.com.ui.view.MainScreen;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Inicia a aplicação diretamente na tela principal (agora no pacote view)
            MainScreen mainScreen = new MainScreen();
            mainScreen.setVisible(true);
        });
    }
}
