package br.com.ui.view;

import br.com.ui.model.Produto;
import br.com.ui.util.ColorPalette;
import br.com.ui.util.TipoProduto;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Produto", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Nome:", ColorPalette.TEXT));
        nomeField = createStyledTextField();
        fieldsPanel.add(nomeField);

        fieldsPanel.add(createStyledLabel("Referência:", ColorPalette.TEXT));
        referenciaField = createStyledTextField();
        fieldsPanel.add(referenciaField);

        fieldsPanel.add(createStyledLabel("Fornecedor:", ColorPalette.TEXT));
        fornecedorField = createStyledTextField();
        fieldsPanel.add(fornecedorField);

        fieldsPanel.add(createStyledLabel("Marca:", ColorPalette.TEXT));
        marcaField = createStyledTextField();
        fieldsPanel.add(marcaField);

        fieldsPanel.add(createStyledLabel("Categoria:", ColorPalette.TEXT));
        categoriaField = createStyledTextField();
        fieldsPanel.add(categoriaField);

        fieldsPanel.add(createStyledLabel("Tipo de Produto:", ColorPalette.TEXT));
        tipoProdutoComboBox = new JComboBox<>(TipoProduto.values());
        tipoProdutoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(ColorPalette.PRIMARY);
                    setForeground(ColorPalette.WHITE_TEXT);
                } else {
                    setBackground(ColorPalette.PANEL_BACKGROUND);
                    setForeground(ColorPalette.TEXT);
                }
                return this;
            }
        });
        fieldsPanel.add(tipoProdutoComboBox);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);
        JButton novoButton = createStyledButton("Novo", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton salvarButton = createStyledButton("Salvar", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton excluirButton = createStyledButton("Excluir", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "Referência", "Fornecedor", "Marca", "Categoria", "Tipo Produto"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(tableModel);

        // Estilo da Tabela
        tabelaProdutos.setBackground(ColorPalette.PANEL_BACKGROUND);
        tabelaProdutos.setForeground(ColorPalette.TEXT);
        tabelaProdutos.setGridColor(new Color(200, 200, 200));
        tabelaProdutos.setSelectionBackground(ColorPalette.PRIMARY);
        tabelaProdutos.setSelectionForeground(ColorPalette.WHITE_TEXT);
        tabelaProdutos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaProdutos.setRowHeight(25);

        JTableHeader tableHeader = tabelaProdutos.getTableHeader();
        tableHeader.setBackground(ColorPalette.PRIMARY);
        tableHeader.setForeground(ColorPalette.WHITE_TEXT);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaProdutos);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
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
                            tipoProdutoComboBox.setSelectedIndex(0);
                        }
                    } else {
                        tipoProdutoComboBox.setSelectedIndex(0);
                    }
                }
            }
        });

        carregarProdutos();
    }

    private void carregarProdutos() {
        tableModel.setRowCount(0);
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

    private JLabel createStyledLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setCaretColor(ColorPalette.TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProdutoScreen().setVisible(true);
        });
    }
}
