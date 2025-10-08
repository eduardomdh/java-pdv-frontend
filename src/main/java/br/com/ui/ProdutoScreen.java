package br.com.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProdutoScreen extends JFrame {

    private JTextField nomeField, referenciaField, fornecedorField, marcaField, categoriaField;
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;

    public ProdutoScreen() {
        setTitle("Gerenciamento de Produtos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));

        fieldsPanel.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        fieldsPanel.add(nomeField);

        fieldsPanel.add(new JLabel("Referência:"));
        referenciaField = new JTextField();
        fieldsPanel.add(referenciaField);

        fieldsPanel.add(new JLabel("Fornecedor:"));
        fornecedorField = new JTextField();
        fieldsPanel.add(fornecedorField);

        fieldsPanel.add(new JLabel("Marca:"));
        marcaField = new JTextField();
        fieldsPanel.add(marcaField);

        fieldsPanel.add(new JLabel("Categoria:"));
        categoriaField = new JTextField();
        fieldsPanel.add(categoriaField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton novoButton = new JButton("Novo");
        JButton salvarButton = new JButton("Salvar");
        JButton excluirButton = new JButton("Excluir");
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "Referência", "Fornecedor", "Marca", "Categoria"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaProdutos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaProdutos);

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
                nomeField.getText(),
                referenciaField.getText(),
                fornecedorField.getText(),
                marcaField.getText(),
                categoriaField.getText()
            };
            tableModel.addRow(rowData);
            limparCampos();
        });

        excluirButton.addActionListener(e -> {
            int selectedRow = tabelaProdutos.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaProdutos.getSelectedRow();
                if (selectedRow != -1) {
                    nomeField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    referenciaField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    fornecedorField.setText((String) tableModel.getValueAt(selectedRow, 3));
                    marcaField.setText((String) tableModel.getValueAt(selectedRow, 4));
                    categoriaField.setText((String) tableModel.getValueAt(selectedRow, 5));
                }
            }
        });
    }

    private void limparCampos() {
        nomeField.setText("");
        referenciaField.setText("");
        fornecedorField.setText("");
        marcaField.setText("");
        categoriaField.setText("");
        tabelaProdutos.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProdutoScreen().setVisible(true);
        });
    }
}
