package br.com.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Dados do Estoque"));

        fieldsPanel.add(new JLabel("Quantidade:"));
        quantidadeField = new JTextField();
        fieldsPanel.add(quantidadeField);

        fieldsPanel.add(new JLabel("Local do Tanque:"));
        localTanqueField = new JTextField();
        fieldsPanel.add(localTanqueField);

        fieldsPanel.add(new JLabel("Endereço do Local:"));
        localEnderecoField = new JTextField();
        fieldsPanel.add(localEnderecoField);

        fieldsPanel.add(new JLabel("Lote de Fabricação:"));
        loteFabricacaoField = new JTextField();
        fieldsPanel.add(loteFabricacaoField);

        fieldsPanel.add(new JLabel("Data de Validade (yyyy-mm-dd):"));
        dataValidadeField = new JTextField();
        fieldsPanel.add(dataValidadeField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton novoButton = new JButton("Novo");
        JButton salvarButton = new JButton("Salvar");
        JButton excluirButton = new JButton("Excluir");
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Quantidade", "Tanque", "Endereço", "Lote", "Validade"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaEstoque = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaEstoque);

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.add(fieldsPanel, BorderLayout.NORTH);
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

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

    private void limparCampos() {
        quantidadeField.setText("");
        localTanqueField.setText("");
        localEnderecoField.setText("");
        loteFabricacaoField.setText("");
        dataValidadeField.setText("");
        tabelaEstoque.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EstoqueScreen().setVisible(true);
        });
    }
}
