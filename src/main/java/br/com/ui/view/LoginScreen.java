package br.com.ui.view;

import br.com.auth.dto.LoginResponse;
import br.com.auth.service.AuthService;
import br.com.common.service.ApiServiceException;
import br.com.ui.component.RoundedButton;
import br.com.ui.util.ColorPalette;
import br.com.acesso.enums.TipoAcesso;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private RoundedButton loginButton;
    private final AuthService authService;

    public LoginScreen() {
        this.authService = new AuthService();

        setTitle("Login - PDV Posto de Combustível");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.TEXT);
        contentPane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Bem-vindo de volta!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ColorPalette.WHITE_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.1;
        contentPane.add(titleLabel, gbc);

        // Label do usuário
        JLabel userLabel = createLabel("Usuário");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(userLabel, gbc);

        // Campo de usuário
        usernameField = createTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPane.add(usernameField, gbc);

        // Label da senha
        JLabel passLabel = createLabel("Senha");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        contentPane.add(passLabel, gbc);

        // Campo de senha
        passwordField = createPasswordField();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPane.add(passwordField, gbc);

        // Botão de entrar
        loginButton = createButton("Entrar");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 5, 20);
        contentPane.add(loginButton, gbc);

        // Rodapé
        JLabel footerLabel = new JLabel("© 2024 PDV Posto de Combustível");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(ColorPalette.TEXT_MUTED);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        contentPane.add(footerLabel, gbc);


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
                JOptionPane.showMessageDialog(this, "Tipo de acesso não reconhecido.", "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ApiServiceException e) {
            JOptionPane.showMessageDialog(this, "Falha na autenticação: " + e.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorPalette.WHITE_TEXT);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBackground(ColorPalette.PANEL_BACKGROUND);
        passwordField.setForeground(ColorPalette.TEXT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return passwordField;
    }

    private RoundedButton createButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(ColorPalette.PRIMARY);
        button.setForeground(ColorPalette.WHITE_TEXT);
        button.setHoverBackgroundColor(ColorPalette.PRIMARY_DARK);
        button.setPressedBackgroundColor(ColorPalette.PRIMARY_DARK.darker());
        button.setCornerRadius(30);
        button.setPreferredSize(new Dimension(100, 40));

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new LoginScreen().setVisible(true);
        });
    }
}
