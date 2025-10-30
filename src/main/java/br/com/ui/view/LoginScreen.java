package br.com.ui.view;

import br.com.model.User;
import br.com.model.UserRepository;

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

        // --- Cores ---
        Color background = new Color(43, 43, 43);
        Color panelBackground = new Color(60, 63, 65);
        Color textColor = Color.WHITE;
        Color buttonBackground = new Color(255, 204, 0);
        Color buttonForeground = Color.BLACK;

        Container contentPane = getContentPane();
        contentPane.setBackground(background);
        contentPane.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        formPanel.setBackground(panelBackground);
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

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Adicionar listener ao botão de login
        loginButton.addActionListener(e -> {
            authenticateUser();
        });
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(new Color(60, 63, 65));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(new Color(60, 63, 65));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 204, 0));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0), 2));
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

            JOptionPane.showMessageDialog(this, "Login bem-sucedido! Perfil: " + role, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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
