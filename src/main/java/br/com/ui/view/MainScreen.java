package br.com.ui.view;

import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainScreen extends JFrame {

    private String loggedInUsername;

    public MainScreen(String username) {
        this.loggedInUsername = username;
        setTitle("Painel Principal - PDV Posto de Combustível");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorPalette.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel userLabel = new JLabel("Usuário: " + loggedInUsername, SwingConstants.LEFT);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(ColorPalette.WHITE_TEXT);
        headerPanel.add(userLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Painel de Gerenciamento", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(ColorPalette.WHITE_TEXT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Sair");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setForeground(ColorPalette.PRIMARY);
        logoutButton.setBackground(ColorPalette.BACKGROUND);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        navPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        JButton productButton = createNavButton("Produtos");
        JButton customerButton = createNavButton("Clientes");
        JButton stockButton = createNavButton("Estoque");
        JButton contactButton = createNavButton("Contatos");
        JButton costButton = createNavButton("Custos");
        JButton priceButton = createNavButton("Preços");
        JButton accessButton = createNavButton("Acesso");

        navPanel.add(productButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(customerButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(stockButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(contactButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(costButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(priceButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(accessButton);
        navPanel.add(Box.createVerticalGlue());

        contentPane.add(navPanel, BorderLayout.WEST);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(ColorPalette.BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel welcomeLabel = new JLabel("Selecione uma opção no menu lateral", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        welcomeLabel.setForeground(ColorPalette.TEXT);
        mainContentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPane.add(mainContentPanel, BorderLayout.CENTER);

        productButton.addActionListener(e -> new ProdutoScreen().setVisible(true));
        customerButton.addActionListener(e -> new PessoaScreen().setVisible(true));
        stockButton.addActionListener(e -> new EstoqueScreen().setVisible(true));
        contactButton.addActionListener(e -> new ContatoScreen().setVisible(true));
        costButton.addActionListener(e -> new CustoScreen().setVisible(true));
        priceButton.addActionListener(e -> new PrecoScreen().setVisible(true));
        accessButton.addActionListener(e -> new GerenciamentoAcessoScreen().setVisible(true));
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.PANEL_BACKGROUND);
        button.setForeground(ColorPalette.TEXT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new MainScreen("Admin").setVisible(true);
        });
    }
}
