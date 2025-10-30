package br.com.ui.view;

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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(20, 20));
        contentPane.setBackground(new Color(43, 43, 43)); // Fundo cinza escuro

        // Painel superior para o nome do usuário e botão de logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(43, 43, 43));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel userLabel = new JLabel("Bem-vindo, " + loggedInUsername + "!", SwingConstants.LEFT);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        topPanel.add(userLabel, BorderLayout.WEST);

        JButton logoutButton = createMenuButton("Sair");
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });
        JPanel logoutButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButtonPanel.setOpaque(false);
        logoutButtonPanel.add(logoutButton);
        topPanel.add(logoutButtonPanel, BorderLayout.EAST);

        JLabel headerLabel = new JLabel("PDV Posto de Combustível", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setForeground(new Color(255, 204, 0)); // Texto amarelo

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonWrapperPanel = new JPanel(new GridBagLayout());
        buttonWrapperPanel.setOpaque(false);
        contentPane.add(buttonWrapperPanel, BorderLayout.CENTER);

        // Layout atualizado para 7 botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2, 15, 15)); // 4 linhas, 2 colunas
        buttonPanel.setOpaque(false);
        buttonWrapperPanel.add(buttonPanel);

        // Botões
        JButton productButton = createMenuButton("Gerenciamento de Produtos");
        JButton customerButton = createMenuButton("Gerenciamento de Clientes");
        JButton stockButton = createMenuButton("Gerenciamento de Estoque");
        JButton contactButton = createMenuButton("Gerenciamento de Contatos");
        JButton costButton = createMenuButton("Gerenciamento de Custos");
        JButton priceButton = createMenuButton("Gerenciamento de Preços");
        JButton accessButton = createMenuButton("Gerenciamento de Acesso");

        buttonPanel.add(productButton);
        buttonPanel.add(customerButton);
        buttonPanel.add(stockButton);
        buttonPanel.add(contactButton);
        buttonPanel.add(costButton);
        buttonPanel.add(priceButton);
        buttonPanel.add(accessButton);

        JLabel footerLabel = new JLabel("Versão 1.0.0", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.LIGHT_GRAY);
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

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 70));
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 204, 0)); // Fundo amarelo
        button.setForeground(Color.BLACK); // Texto preto
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0), 2)); // Borda laranja
        return button;
    }

    // O método main é apenas para teste isolado
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainScreen("Admin").setVisible(true);
        });
    }
}
