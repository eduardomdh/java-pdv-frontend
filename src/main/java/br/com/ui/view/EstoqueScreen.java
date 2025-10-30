package br.com.ui.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class EstoqueScreen extends JFrame {

    private JTextField quantidadeField, localTanqueField, localEnderecoField, loteFabricacaoField, dataValidadeField;
    private JTable tabelaEstoque;
    private DefaultTableModel tableModel;

    public EstoqueScreen() {
        setTitle("Gerenciamento de Estoque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Cores ---
        Color background = new Color(43, 43, 43);
        Color panelBackground = new Color(60, 63, 65);
        Color textColor = Color.WHITE;
        Color buttonBackground = new Color(255, 204, 0);
        Color buttonForeground = Color.BLACK;

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setBackground(background);
        contentPane.setLayout(new BorderLayout(10, 10));

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBackground(background);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Dados do Estoque");
        titledBorder.setTitleColor(textColor);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titledBorder
        ));

        fieldsPanel.add(createStyledLabel("Quantidade:"));
        quantidadeField = createStyledTextField();
        fieldsPanel.add(quantidadeField);

        fieldsPanel.add(createStyledLabel("Local do Tanque:"));
        localTanqueField = createStyledTextField();
        fieldsPanel.add(localTanqueField);

        fieldsPanel.add(createStyledLabel("Endereço do Local:"));
        localEnderecoField = createStyledTextField();
        fieldsPanel.add(localEnderecoField);

        fieldsPanel.add(createStyledLabel("Lote de Fabricação:"));
        loteFabricacaoField = createStyledTextField();
        fieldsPanel.add(loteFabricacaoField);

        fieldsPanel.add(createStyledLabel("Data de Validade (yyyy-mm-dd):"));
        dataValidadeField = createStyledTextField();
        fieldsPanel.add(dataValidadeField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(background);
        JButton novoButton = createStyledButton("Novo");
        JButton salvarButton = createStyledButton("Salvar");
        JButton excluirButton = createStyledButton("Excluir");
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Quantidade", "Tanque", "Endereço", "Lote", "Validade"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaEstoque = new JTable(tableModel);

        // Estilo da Tabela
        tabelaEstoque.setBackground(panelBackground);
        tabelaEstoque.setForeground(textColor);
        tabelaEstoque.setGridColor(new Color(80, 80, 80));
        tabelaEstoque.setSelectionBackground(buttonBackground);
        tabelaEstoque.setSelectionForeground(buttonForeground);
        tabelaEstoque.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaEstoque.setRowHeight(25);

        // Estilo do Header da Tabela
        JTableHeader tableHeader = tabelaEstoque.getTableHeader();
        tableHeader.setBackground(new Color(80, 80, 80));
        tableHeader.setForeground(buttonBackground); // Yellow text
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaEstoque);
        tableScrollPane.getViewport().setBackground(background);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(panelBackground, 2));

        // Adiciona um espaçamento geral
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(background);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Ações
        novoButton.addActionListener(e -> limparCampos());

        salvarButton.addActionListener(e -> {
            String[] rowData = {
                String.valueOf(tableModel.getRowCount() + 1), // ID temporário
                quantidadeField.getText(),
                localTanqueField.getText(),
                localEnderecoField.getText(),
                loteFabricacaoField.getText(),
                dataValidadeField.getText()
            };
            tableModel.addRow(rowData);
            limparCampos();
        });

        excluirButton.addActionListener(e -> {
            int selectedRow = tabelaEstoque.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        tabelaEstoque.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaEstoque.getSelectedRow();
                if (selectedRow != -1) {
                    quantidadeField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    localTanqueField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    localEnderecoField.setText((String) tableModel.getValueAt(selectedRow, 3));
                    loteFabricacaoField.setText((String) tableModel.getValueAt(selectedRow, 4));
                    dataValidadeField.setText((String) tableModel.getValueAt(selectedRow, 5));
                }
            }
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

    private void limparCampos() {
        quantidadeField.setText("");
        localTanqueField.setText("");
        localEnderecoField.setText("");
        loteFabricacaoField.setText("");
        dataValidadeField.setText("");
        tabelaEstoque.clearSelection();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new EstoqueScreen().setVisible(true);
        });
    }
}
