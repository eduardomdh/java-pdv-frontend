package br.com.ui.view;

import br.com.auth.dto.LoginResponse;
import br.com.auth.service.AuthService;
import br.com.common.service.ApiServiceException;
import br.com.ui.util.ColorPalette;
import br.com.acesso.enums.TipoAcesso;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private final AuthService authService;

    public LoginScreen() {
        this.authService = new AuthService();

        setTitle("Login - PDV Posto de Combustível");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        formPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setOpaque(false);
        usernamePanel.add(createStyledLabel("Usuário:"), BorderLayout.NORTH);
        usernameField = createStyledTextField();
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        formPanel.add(usernamePanel);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.add(createStyledLabel("Senha:"), BorderLayout.NORTH);
        passwordField = createStyledPasswordField();
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        formPanel.add(passwordPanel);

        loginButton = createStyledButton("Entrar");
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(loginButton);
        formPanel.add(buttonWrapper);

        contentPane.add(formPanel, new GridBagConstraints());

        loginButton.addActionListener(e -> authenticateUser());
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            LoginResponse loginResponse = authService.login(username, password);

            this.dispose();

            if (loginResponse.tipoAcesso() == TipoAcesso.ADMINISTRADOR || loginResponse.tipoAcesso() == TipoAcesso.GERENCIA) {
                new MainScreen(username).setVisible(true);
            } else if (loginResponse.tipoAcesso() == TipoAcesso.FUNCIONARIO) {
                new AbastecimentoScreen(username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de acesso não reconhecido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ApiServiceException e) {
            JOptionPane.showMessageDialog(this, "Erro na API: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ColorPalette.TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setCaretColor(ColorPalette.TEXT);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(ColorPalette.PANEL_BACKGROUND);
        passwordField.setForeground(ColorPalette.TEXT);
        passwordField.setCaretColor(ColorPalette.TEXT);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.PRIMARY);
        button.setForeground(ColorPalette.WHITE_TEXT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new LoginScreen().setVisible(true);
        });
    }
}
