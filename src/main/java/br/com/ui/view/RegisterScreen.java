package br.com.ui.view;

import br.com.acesso.enums.TipoAcesso;
import br.com.auth.service.AuthService;
import br.com.ui.component.RoundedButton;
import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<TipoAcesso> roleComboBox;
    private RoundedButton registerButton;
    private final AuthService authService;

    public RegisterScreen() {
        this.authService = new AuthService();

        setTitle("Cadastro - PDV Posto de Combustível");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout());

        // Header com o título
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(ColorPalette.BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(40, 20, 20, 20));
        JLabel titleLabel = new JLabel("Crie sua conta");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ColorPalette.TEXT);
        headerPanel.add(titleLabel);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Wrapper panel to center the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ColorPalette.BACKGROUND);

        // Painel do formulário
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(ColorPalette.BACKGROUND);
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel userLabel = createLabel("Usuário");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(userLabel);

        usernameField = createTextField();
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel passLabel = createLabel("Senha");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passLabel);

        passwordField = createPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel confirmPassLabel = createLabel("Confirmar Senha");
        confirmPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(confirmPassLabel);

        confirmPasswordField = createPasswordField();
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel roleLabel = createLabel("Cargo");
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(roleLabel);

        roleComboBox = createRoleComboBox();
        roleComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(roleComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        registerButton = createButton("Cadastrar");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(registerButton);

        centerPanel.add(formPanel, new GridBagConstraints());
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Rodapé
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(ColorPalette.BACKGROUND);
        footerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel footerLabel = new JLabel("© 2024 PDV Posto de Combustível");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(ColorPalette.TEXT_MUTED);
        footerPanel.add(footerLabel);
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerUser());
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        TipoAcesso selectedRole = (TipoAcesso) roleComboBox.getSelectedItem();

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "As senhas não conferem.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: Adicionar chamada ao serviço de registro
        JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso para o cargo: " + selectedRole, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
        new LoginScreen().setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorPalette.TEXT);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        textField.setMaximumSize(new Dimension(300, 45));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBackground(ColorPalette.PANEL_BACKGROUND);
        passwordField.setForeground(ColorPalette.TEXT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        passwordField.setMaximumSize(new Dimension(300, 45));
        return passwordField;
    }

    private JComboBox<TipoAcesso> createRoleComboBox() {
        JComboBox<TipoAcesso> comboBox = new JComboBox<>(TipoAcesso.values());
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboBox.setBackground(ColorPalette.PANEL_BACKGROUND);
        comboBox.setForeground(ColorPalette.TEXT);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        comboBox.setMaximumSize(new Dimension(300, 45));
        return comboBox;
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
        button.setMaximumSize(new Dimension(300, 50));

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new RegisterScreen().setVisible(true);
        });
    }
}
