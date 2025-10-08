package br.com.ui.view;

import br.com.mock.ProdutoMock;
import br.com.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaProdutos);

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.add(fieldsPanel, BorderLayout.NORTH);
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarProduto());
        excluirButton.addActionListener(e -> excluirProduto());
        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        // Carrega os dados iniciais
        carregarProdutos();
    }

    private void carregarProdutos() {
        tableModel.setRowCount(0);
        List<Produto> produtos = ProdutoMock.getProdutos();
        for (Produto p : produtos) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getNome(), p.getReferencia(), p.getFornecedor(), p.getMarca(), p.getCategoria()
            });
        }
    }

    private void salvarProduto() {
        Produto novoProduto = new Produto(
            null,
            nomeField.getText(),
            referenciaField.getText(),
            fornecedorField.getText(),
            marcaField.getText(),
            categoriaField.getText()
        );
        ProdutoMock.addProduto(novoProduto);
        carregarProdutos();
        limparCampos();
    }

    private void excluirProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            ProdutoMock.removeProduto(id);
            carregarProdutos();
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCamposComSelecao() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {
            nomeField.setText((String) tableModel.getValueAt(selectedRow, 1));
            referenciaField.setText((String) tableModel.getValueAt(selectedRow, 2));
            fornecedorField.setText((String) tableModel.getValueAt(selectedRow, 3));
            marcaField.setText((String) tableModel.getValueAt(selectedRow, 4));
            categoriaField.setText((String) tableModel.getValueAt(selectedRow, 5));
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        referenciaField.setText("");
        fornecedorField.setText("");
        marcaField.setText("");
        categoriaField.setText("");
        tabelaProdutos.clearSelection();
    }
}
