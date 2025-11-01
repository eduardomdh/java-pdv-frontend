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

        // Cores
        Color background = new Color(240, 240, 240);
        Color primary = new Color(163, 31, 52);
        Color secondary = new Color(0, 153, 102);
        Color text = new Color(51, 51, 51);
        Color textOnDark = Color.WHITE;
        Color accent = new Color(255, 204, 0);

        Container contentPane = getContentPane();
        contentPane.setBackground(background);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Estoque", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), primary),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Quantidade:", text));
        quantidadeField = createStyledTextField();
        fieldsPanel.add(quantidadeField);

        fieldsPanel.add(createStyledLabel("Local do Tanque:", text));
        localTanqueField = createStyledTextField();
        fieldsPanel.add(localTanqueField);

        fieldsPanel.add(createStyledLabel("Endereço do Local:", text));
        localEnderecoField = createStyledTextField();
        fieldsPanel.add(localEnderecoField);

        fieldsPanel.add(createStyledLabel("Lote de Fabricação:", text));
        loteFabricacaoField = createStyledTextField();
        fieldsPanel.add(loteFabricacaoField);

        fieldsPanel.add(createStyledLabel("Data de Validade (yyyy-mm-dd):", text));
        dataValidadeField = createStyledTextField();
        fieldsPanel.add(dataValidadeField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);
        JButton novoButton = createStyledButton("Novo", secondary, textOnDark);
        JButton salvarButton = createStyledButton("Salvar", primary, textOnDark);
        JButton excluirButton = createStyledButton("Excluir", accent, text);
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Quantidade", "Tanque", "Endereço", "Lote", "Validade"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaEstoque = new JTable(tableModel);

        // Estilo da Tabela
        tabelaEstoque.setBackground(Color.WHITE);
        tabelaEstoque.setForeground(text);
        tabelaEstoque.setGridColor(Color.LIGHT_GRAY);
        tabelaEstoque.setSelectionBackground(accent);
        tabelaEstoque.setSelectionForeground(text);
        tabelaEstoque.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaEstoque.setRowHeight(25);

        JTableHeader tableHeader = tabelaEstoque.getTableHeader();
        tableHeader.setBackground(primary);
        tableHeader.setForeground(textOnDark);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaEstoque);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Ações
        novoButton.addActionListener(e -> limparCampos());

        salvarButton.addActionListener(e -> {
            String[] rowData = {
                String.valueOf(tableModel.getRowCount() + 1),
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

    private JLabel createStyledLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
