package br.com.ui.view;

import br.com.produto.dto.ProdutoRequest;
import br.com.produto.dto.ProdutoResponse;
import br.com.produto.enums.TipoProduto;
import br.com.produto.service.ProdutoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProdutoScreen extends JFrame {

    private JTextField nomeField, referenciaField, fornecedorField, marcaField, categoriaField;
    private JComboBox<TipoProduto> tipoProdutoComboBox;
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private Long produtoIdEmEdicao; // Novo campo para armazenar o ID do produto em edição

    private final ProdutoService produtoService;

    public ProdutoScreen() {
        this.produtoService = new ProdutoService();

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
        fieldsPanel.add(tipoProdutoComboBox);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);
        JButton novoButton = createStyledButton("Novo", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton salvarButton = createStyledButton("Salvar", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton editarButton = createStyledButton("Editar", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT); // Novo botão Editar
        JButton excluirButton = createStyledButton("Excluir", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(editarButton); // Adiciona o botão Editar
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "Referência", "Fornecedor", "Marca", "Categoria", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaProdutos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaProdutos);

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
        editarButton.addActionListener(e -> editarProduto()); // Ação para o botão Editar

        carregarProdutos();
    }

    private void carregarProdutos() {
        tableModel.setRowCount(0);
        try {
            List<ProdutoResponse> produtos = produtoService.findProducts();
            for (ProdutoResponse produto : produtos) {
                tableModel.addRow(new Object[]{
                        produto.id(),
                        produto.nome(),
                        produto.referencia(),
                        produto.fornecedor(),
                        produto.marca(),
                        produto.categoria(),
                        produto.tipoProduto()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void salvarProduto() {
        ProdutoRequest request = new ProdutoRequest(
                nomeField.getText(),
                referenciaField.getText(),
                fornecedorField.getText(),
                marcaField.getText(),
                categoriaField.getText(),
                (TipoProduto) tipoProdutoComboBox.getSelectedItem()
        );

        try {
            if (produtoIdEmEdicao == null) { // Se não há ID em edição, é um novo produto
                produtoService.createProduct(request);
                JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Se há um ID em edição, é uma atualização
                produtoService.updateProduct(produtoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarProdutos(); // Recarrega a lista da API
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editarProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        produtoIdEmEdicao = (Long) tabelaProdutos.getValueAt(selectedRow, 0); // Pega o ID da primeira coluna
        nomeField.setText((String) tabelaProdutos.getValueAt(selectedRow, 1));
        referenciaField.setText((String) tabelaProdutos.getValueAt(selectedRow, 2));
        fornecedorField.setText((String) tabelaProdutos.getValueAt(selectedRow, 3));
        marcaField.setText((String) tabelaProdutos.getValueAt(selectedRow, 4));
        categoriaField.setText((String) tabelaProdutos.getValueAt(selectedRow, 5));
        tipoProdutoComboBox.setSelectedItem(TipoProduto.valueOf(tabelaProdutos.getValueAt(selectedRow, 6).toString())); // Converte String para TipoProduto

        JOptionPane.showMessageDialog(this, "Campos preenchidos para edição. Altere os dados e clique em Salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaProdutos.getValueAt(selectedRow, 0); // Assumindo que o ID está na primeira coluna

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o produto selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                produtoService.deleteProduct(id);
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos(); // Recarrega a lista da API
                limparCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        referenciaField.setText("");
        fornecedorField.setText("");
        marcaField.setText("");
        categoriaField.setText("");
        tipoProdutoComboBox.setSelectedIndex(0);
        tabelaProdutos.clearSelection();
        produtoIdEmEdicao = null; // Limpa o ID em edição
    }

    // Métodos de estilo (createStyledLabel, etc.) permanecem os mesmos
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
