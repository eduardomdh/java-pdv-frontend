package br.com.ui.view;

import br.com.ui.util.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {

    private String loggedInUsername;

    public MainScreen(String username) {
        this.loggedInUsername = username;
        setTitle("Painel Principal - PDV Posto de Combustível");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel do Cabeçalho ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorPalette.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // --- Painel de Botões com GridBagLayout ---
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Botões
        JButton productButton = createMenuButton("Gerenciamento de Produtos", ColorPalette.PANEL_BACKGROUND, ColorPalette.TEXT);
        JButton customerButton = createMenuButton("Gerenciamento de Clientes", ColorPalette.PANEL_BACKGROUND, ColorPalette.TEXT);
        JButton stockButton = createMenuButton("Gerenciamento de Estoque", ColorPalette.PANEL_BACKGROUND, ColorPalette.TEXT);
        JButton contactButton = createMenuButton("Gerenciamento de Contatos", ColorPalette.PANEL_BACKGROUND, ColorPalette.TEXT);
        JButton costButton = createMenuButton("Gerenciamento de Custos", ColorPalette.PANEL_BACKGROUND, ColorPalette.TEXT);
        JButton priceButton = createMenuButton("Gerenciamento de Preços", ColorPalette.PANEL_BACKGROUND, ColorPalette.TEXT);
        JButton accessButton = createMenuButton("Gerenciamento de Acesso", ColorPalette.PANEL_BACKGROUND, ColorPalette.TEXT);

        // Adicionando botões ao painel com GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(productButton, gbc);

        gbc.gridx = 1;
        buttonPanel.add(customerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(stockButton, gbc);

        gbc.gridx = 1;
        buttonPanel.add(contactButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonPanel.add(costButton, gbc);

        gbc.gridx = 1;
        buttonPanel.add(priceButton, gbc);

        // Botão de Acesso centralizado
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(accessButton, gbc);

        contentPane.add(buttonPanel, BorderLayout.CENTER);

        // --- Rodapé ---
        JLabel footerLabel = new JLabel("Versão 1.0.0", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPane.add(footerLabel, BorderLayout.SOUTH);

        // --- Ações dos Botões ---
        productButton.addActionListener(e -> new ProdutoScreen().setVisible(true));
        customerButton.addActionListener(e -> new PessoaScreen().setVisible(true));
        stockButton.addActionListener(e -> new EstoqueScreen().setVisible(true));
        contactButton.addActionListener(e -> new ContatoScreen().setVisible(true));
        costButton.addActionListener(e -> new CustoScreen().setVisible(true));
        priceButton.addActionListener(e -> new PrecoScreen().setVisible(true));
        accessButton.addActionListener(e -> new GerenciamentoAcessoScreen().setVisible(true));
    }

    private JButton createMenuButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 70));
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainScreen("Admin").setVisible(true);
        });
    }
}
