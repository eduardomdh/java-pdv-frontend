package br.com.ui.view;

import br.com.model.User;
import br.com.model.UserRepository;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private final UserRepository userRepository;

    public LoginScreen() {
        this.userRepository = UserRepository.getInstance();
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

        // Username
        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setOpaque(false);
        usernamePanel.add(createStyledLabel("Usuário:"), BorderLayout.NORTH);
        usernameField = createStyledTextField();
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        formPanel.add(usernamePanel);

        // Password
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.add(createStyledLabel("Senha:"), BorderLayout.NORTH);
        passwordField = createStyledPasswordField();
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        formPanel.add(passwordPanel);

        // Login Button
        loginButton = createStyledButton("Entrar");
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(loginButton);
        formPanel.add(buttonWrapper);

        contentPane.add(formPanel, new GridBagConstraints());

        // Adicionar listener ao botão de login
        loginButton.addActionListener(e -> authenticateUser());
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

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        Optional<User> userOpt = userRepository.findByLogin(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            String role = user.getTipoAcesso();

            this.dispose();

            if ("funcionario".equals(role)) {
                new AbastecimentoScreen(user.getLogin()).setVisible(true);
            } else if ("gerente".equals(role) || "administrador".equals(role)) {
                new MainScreen(user.getLogin()).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
