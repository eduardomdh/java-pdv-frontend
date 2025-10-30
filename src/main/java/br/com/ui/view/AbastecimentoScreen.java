package br.com.ui.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AbastecimentoScreen extends JFrame {

    private String loggedInUsername;

    public AbastecimentoScreen(String username) {
        this.loggedInUsername = username;
        setTitle("Central de Abastecimento - PDV Posto de Combustível");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Cores ---
        Color background = new Color(43, 43, 43);
        Color panelBackground = new Color(60, 63, 65);
        Color textColor = Color.WHITE;
        Color buttonBackground = new Color(255, 204, 0);

        Container contentPane = getContentPane();
        contentPane.setBackground(background);
        contentPane.setLayout(new BorderLayout(10, 10));

        // --- Painel Superior (Usuário e Logout) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(background);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel userLabel = new JLabel("Bem-vindo, " + loggedInUsername + "!", SwingConstants.LEFT);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(textColor);
        topPanel.add(userLabel, BorderLayout.WEST);

        JButton logoutButton = createStyledButton("Sair");
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });
        JPanel logoutButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButtonPanel.setOpaque(false);
        logoutButtonPanel.add(logoutButton);
        topPanel.add(logoutButtonPanel, BorderLayout.EAST);

        // --- Título Principal ---
        JLabel mainTitleLabel = new JLabel("Central de Abastecimento", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        mainTitleLabel.setForeground(buttonBackground); // Amarelo
        mainTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // --- Container do Cabeçalho (Título e Painel Superior) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(mainTitleLabel, BorderLayout.CENTER);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // --- Painel de Bombas (com GridLayout) ---
        JPanel pumpsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        pumpsPanel.setOpaque(false);
        pumpsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adiciona margem

        // Seção B1 (Concluída)
        pumpsPanel.add(createPumpSection("B1", "Etanol", "40 litros / R$ 4,50 por litro", "R$ 180,00", new String[]{"PIX", "Dinheiro (D)", "Cartão de Crédito (CC)", "Cartão de Débito (CD)"}, "Concluída", panelBackground, textColor, Color.GREEN));

        // Seção B2 (Ativa)
        pumpsPanel.add(createPumpSection("B2", "Gasolina", "30 litros / R$ 6,50 por litro", "R$ 195,00", new String[]{"PIX", "Dinheiro (D)", "Cartão de Crédito (CC)", "Cartão de Débito (CD)"}, "Ativa", panelBackground, textColor, Color.ORANGE));

        // Seção B3 (Inativa)
        pumpsPanel.add(createPumpSection("B3", "", "", "", new String[]{}, "INATIVA", panelBackground, textColor, Color.RED));

        contentPane.add(pumpsPanel, BorderLayout.CENTER);
    }

    private JPanel createPumpSection(String pumpTitle, String fuel, String details, String totalValue, String[] paymentOptions, String status, Color panelBg, Color textC, Color statusColor) {
        JPanel sectionPanel = new JPanel();
        // Usar BoxLayout com alinhamento CENTER_ALIGNMENT para centralizar os componentes
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(panelBg);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(pumpTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(textC);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o título da bomba
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        if (!fuel.isEmpty()) {
            sectionPanel.add(createStyledLabel("Combustível: " + fuel, textC));
            sectionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            sectionPanel.add(createStyledLabel("<html>Detalhes: " + details + "</html>", textC));
            sectionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            sectionPanel.add(createStyledLabel("Valor Total: " + totalValue, textC));
            sectionPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // --- Radio Buttons para Opções de Pagamento ---
            JPanel paymentPanel = new JPanel();
            paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
            paymentPanel.setBackground(panelBg);
            TitledBorder paymentBorder = BorderFactory.createTitledBorder("Opções de Pagamento");
            paymentBorder.setTitleColor(textC);
            paymentPanel.setBorder(paymentBorder);
            paymentPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o painel de pagamento

            ButtonGroup paymentGroup = new ButtonGroup();
            for (String option : paymentOptions) {
                JRadioButton radioButton = new JRadioButton(option);
                radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
                radioButton.setBackground(panelBg);
                radioButton.setForeground(textC);
                radioButton.setFocusPainted(false);
                radioButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza cada Radio Button
                paymentGroup.add(radioButton);
                paymentPanel.add(radioButton);
            }
            sectionPanel.add(paymentPanel);
        }

        sectionPanel.add(Box.createVerticalGlue()); // Empurra o status para baixo

        JLabel statusLabel = new JLabel("Status: " + status, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setForeground(statusColor);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o status
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        sectionPanel.add(statusLabel);

        return sectionPanel;
    }

    private JLabel createStyledLabel(String text, Color textColor) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o texto dos detalhes
        return label;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AbastecimentoScreen("TesteUser").setVisible(true);
        });
    }
}
