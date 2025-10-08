package br.com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreen extends JFrame {

    public MainScreen() {
        setTitle("Painel Principal - PDV Posto de Combustível");
        setSize(800, 600); // Aumentar o tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tenta aplicar o Look and Feel do sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Container Principal ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(20, 20));
        contentPane.setBackground(new Color(245, 245, 245)); // Um cinza bem claro

        // --- Cabeçalho ---
        JLabel headerLabel = new JLabel("PDV Posto de Combustível", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        contentPane.add(headerLabel, BorderLayout.NORTH);

        // --- Painel de Botões (Central) ---
        JPanel buttonWrapperPanel = new JPanel(new GridBagLayout()); // Painel para centralizar
        buttonWrapperPanel.setOpaque(false);
        contentPane.add(buttonWrapperPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 15, 15)); // 3 linhas, 1 coluna, com espaçamento
        buttonPanel.setOpaque(false);
        buttonWrapperPanel.add(buttonPanel); // Adiciona o painel de botões ao centralizador

        // --- Botões Estilizados ---
        JButton productButton = createMenuButton("Gerenciamento de Produtos");
        JButton customerButton = createMenuButton("Gerenciamento de Clientes");
        JButton stockButton = createMenuButton("Gerenciamento de Estoque");

        buttonPanel.add(productButton);
        buttonPanel.add(customerButton);
        buttonPanel.add(stockButton);

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
    }

    // Método auxiliar para criar botões estilizados e evitar repetição de código
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 70));
        button.setFocusPainted(false);
        button.setBackground(new Color(60, 120, 180)); // Azul aço
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Inicia a tela principal para fins de teste
            MainScreen main = new MainScreen();
            main.setVisible(true);
        });
    }
}
