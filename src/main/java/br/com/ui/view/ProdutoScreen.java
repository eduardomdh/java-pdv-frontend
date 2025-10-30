package br.com.ui.view;

import br.com.ui.model.Produto;
import br.com.ui.util.TipoProduto;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoScreen extends JFrame {

    private JTextField nomeField, referenciaField, fornecedorField, marcaField, categoriaField;
    private JComboBox<TipoProduto> tipoProdutoComboBox;
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private Long selectedProductId = null;
    private final List<Produto> produtos = new ArrayList<>();
    private long nextId = 1;

    public ProdutoScreen() {
        setTitle("Gerenciamento de Produtos");
        setSize(800, 650);
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
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBackground(background);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Dados do Produto");
        titledBorder.setTitleColor(textColor);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titledBorder
        ));

        fieldsPanel.add(createStyledLabel("Nome:"));
        nomeField = createStyledTextField();
        fieldsPanel.add(nomeField);

        fieldsPanel.add(createStyledLabel("Referência:"));
        referenciaField = createStyledTextField();
        fieldsPanel.add(referenciaField);

        fieldsPanel.add(createStyledLabel("Fornecedor:"));
        fornecedorField = createStyledTextField();
        fieldsPanel.add(fornecedorField);

        fieldsPanel.add(createStyledLabel("Marca:"));
        marcaField = createStyledTextField();
        fieldsPanel.add(marcaField);

        fieldsPanel.add(createStyledLabel("Categoria:"));
        categoriaField = createStyledTextField();
        fieldsPanel.add(categoriaField);

        fieldsPanel.add(createStyledLabel("Tipo de Produto:"));
        tipoProdutoComboBox = new JComboBox<>(TipoProduto.values());
        fieldsPanel.add(tipoProdutoComboBox);

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
        String[] colunas = {"ID", "Nome", "Referência", "Fornecedor", "Marca", "Categoria", "Tipo Produto"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        tabelaProdutos = new JTable(tableModel);

        // Estilo da Tabela
        tabelaProdutos.setBackground(panelBackground);
        tabelaProdutos.setForeground(textColor);
        tabelaProdutos.setGridColor(new Color(80, 80, 80));
        tabelaProdutos.setSelectionBackground(buttonBackground);
        tabelaProdutos.setSelectionForeground(buttonForeground);
        tabelaProdutos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaProdutos.setRowHeight(25);

        JTableHeader tableHeader = tabelaProdutos.getTableHeader();
        tableHeader.setBackground(new Color(80, 80, 80));
        tableHeader.setForeground(buttonBackground);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaProdutos);
        tableScrollPane.getViewport().setBackground(background);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(panelBackground, 2));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(background);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarProduto());
        excluirButton.addActionListener(e -> excluirProduto());

        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaProdutos.getSelectedRow();
                if (selectedRow != -1) {
                    selectedProductId = (Long) tableModel.getValueAt(selectedRow, 0);
                    nomeField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    referenciaField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    fornecedorField.setText((String) tableModel.getValueAt(selectedRow, 3));
                    marcaField.setText((String) tableModel.getValueAt(selectedRow, 4));
                    categoriaField.setText((String) tableModel.getValueAt(selectedRow, 5));
                    Object tipoProdutoObj = tableModel.getValueAt(selectedRow, 6);
                    if (tipoProdutoObj != null && !tipoProdutoObj.toString().isEmpty()) {
                        try {
                            tipoProdutoComboBox.setSelectedItem(TipoProduto.valueOf(tipoProdutoObj.toString()));
                        } catch (IllegalArgumentException ex) {
                            tipoProdutoComboBox.setSelectedIndex(0); // Reset to default if value is invalid
                        }
                    } else {
                        tipoProdutoComboBox.setSelectedIndex(0); // Reset to default for empty/null
                    }
                }
            }
        });

        carregarProdutos();
    }

    private void carregarProdutos() {
        tableModel.setRowCount(0); // Limpa a tabela
        for (Produto produto : produtos) {
            tableModel.addRow(new Object[]{
                    produto.getId(),
                    produto.getNome(),
                    produto.getReferencia(),
                    produto.getFornecedor(),
                    produto.getMarca(),
                    produto.getCategoria(),
                    produto.getTipoProduto() != null ? produto.getTipoProduto().name() : ""
            });
        }
    }

    private void salvarProduto() {
        Produto produto = new Produto();
        produto.setNome(nomeField.getText());
        produto.setReferencia(referenciaField.getText());
        produto.setFornecedor(fornecedorField.getText());
        produto.setMarca(marcaField.getText());
        produto.setCategoria(categoriaField.getText());
        produto.setTipoProduto((TipoProduto) tipoProdutoComboBox.getSelectedItem());

        if (selectedProductId == null) { // Criar novo
            produto.setId(nextId++);
            produtos.add(produto);
        } else { // Atualizar existente
            for (int i = 0; i < produtos.size(); i++) {
                if (produtos.get(i).getId().equals(selectedProductId)) {
                    produto.setId(selectedProductId);
                    produtos.set(i, produto);
                    break;
                }
            }
        }
        carregarProdutos();
        limparCampos();
    }

    private void excluirProduto() {
        if (selectedProductId == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o produto selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            produtos.removeIf(p -> p.getId().equals(selectedProductId));
            carregarProdutos();
            limparCampos();
        }
    }

    private void limparCampos() {
        selectedProductId = null;
        nomeField.setText("");
        referenciaField.setText("");
        fornecedorField.setText("");
        marcaField.setText("");
        categoriaField.setText("");
        tipoProdutoComboBox.setSelectedIndex(0);
        tabelaProdutos.clearSelection();
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ProdutoScreen().setVisible(true);
        });
    }
}
