package br.com.ui.view;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {

    public MainScreen() {
        setTitle("Painel Principal - PDV Posto de Combustível");
        setSize(800, 700); // Aumentar um pouco a altura para os botões
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(20, 20));
        contentPane.setBackground(new Color(245, 245, 245));

        JLabel headerLabel = new JLabel("PDV Posto de Combustível", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        contentPane.add(headerLabel, BorderLayout.NORTH);

        JPanel buttonWrapperPanel = new JPanel(new GridBagLayout());
        buttonWrapperPanel.setOpaque(false);
        contentPane.add(buttonWrapperPanel, BorderLayout.CENTER);

        // Layout atualizado para 6 botões (2 colunas)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 15, 15)); // 3 linhas, 2 colunas
        buttonPanel.setOpaque(false);
        buttonWrapperPanel.add(buttonPanel);

        // Botões
        JButton productButton = createMenuButton("Gerenciamento de Produtos");
        JButton customerButton = createMenuButton("Gerenciamento de Clientes");
        JButton stockButton = createMenuButton("Gerenciamento de Estoque");
        JButton contactButton = createMenuButton("Gerenciamento de Contatos");
        JButton costButton = createMenuButton("Gerenciamento de Custos");
        JButton priceButton = createMenuButton("Gerenciamento de Preços"); // Novo botão

        buttonPanel.add(productButton);
        buttonPanel.add(customerButton);
        buttonPanel.add(stockButton);
        buttonPanel.add(contactButton);
        buttonPanel.add(costButton);
        buttonPanel.add(priceButton); // Adicionado ao painel

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
        priceButton.addActionListener(e -> new PrecoScreen().setVisible(true)); // Ação do novo botão
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 70));
        button.setFocusPainted(false);
        button.setBackground(new Color(220, 220, 220));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return button;
    }
}
