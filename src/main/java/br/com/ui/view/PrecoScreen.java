package br.com.ui.view;

import br.com.preco.dto.PrecoRequest;
import br.com.preco.dto.PrecoResponse;
import br.com.preco.enums.TipoPreco;
import br.com.preco.service.PrecoService;
import br.com.produto.dto.ProdutoResponse;
import br.com.produto.service.ProdutoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrecoScreen extends JFrame {

    private JComboBox<String> produtoComboBox;
    private JTextField valorField, dataAlteracaoField;
    private JTable tabelaPrecos;
    private DefaultTableModel tableModel;
    private Map<String, ProdutoResponse> produtoMap;

    private final PrecoService precoService;
    private final ProdutoService produtoService;

    public PrecoScreen() {
        this.precoService = new PrecoService();
        this.produtoService = new ProdutoService();

        setTitle("Gerenciamento de Preços por Produto");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel de Seleção de Produto e Campos ---
        JPanel topFieldsPanel = new JPanel(new BorderLayout(10, 10));
        topFieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        topFieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados de Preço", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel productSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        productSelectionPanel.setOpaque(false);
        produtoComboBox = new JComboBox<>();
        productSelectionPanel.add(createStyledLabel("Produto:", ColorPalette.TEXT));
        productSelectionPanel.add(produtoComboBox);

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.add(createStyledLabel("Valor (ex: 5.89):", ColorPalette.TEXT));
        valorField = createStyledTextField();
        fieldsPanel.add(valorField);
        fieldsPanel.add(createStyledLabel("Data Alteração (yyyy-mm-dd):", ColorPalette.TEXT));
        dataAlteracaoField = createStyledTextField();
        fieldsPanel.add(dataAlteracaoField);

        topFieldsPanel.add(productSelectionPanel, BorderLayout.NORTH);
        topFieldsPanel.add(fieldsPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);
        JButton salvarButton = createStyledButton("Salvar Novo Preço", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        buttonsPanel.add(salvarButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Valor", "Data de Alteração", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaPrecos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPrecos);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(topFieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- Ações ---
        produtoComboBox.addActionListener(e -> carregarPrecosDoProduto());
        salvarButton.addActionListener(e -> salvarPreco());

        this.produtoMap = new HashMap<>();
        carregarProdutosComboBox();
    }

    private void carregarProdutosComboBox() {
        try {
            List<ProdutoResponse> produtos = produtoService.findProducts();
            produtoMap.clear();
            produtoComboBox.removeAllItems();
            for (ProdutoResponse produto : produtos) {
                produtoMap.put(produto.nome(), produto);
                produtoComboBox.addItem(produto.nome());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarPrecosDoProduto() {
        tableModel.setRowCount(0);
        // A API de preços do backend não parece ter um filtro por produto.
        // O ideal seria que tivesse. Por enquanto, vamos listar todos.
        try {
            List<PrecoResponse> precos = precoService.findPrecos();
            for (PrecoResponse preco : precos) {
                tableModel.addRow(new Object[]{
                        preco.id(),
                        preco.valor(),
                        preco.dataAlteracao(),
                        preco.tipoPreco()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar preços: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarPreco() {
        try {
            String nomeProdutoSelecionado = (String) produtoComboBox.getSelectedItem();
            if (nomeProdutoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal valor = new BigDecimal(valorField.getText());
            LocalDate dataAlteracao = LocalDate.parse(dataAlteracaoField.getText());

            // O backend espera um TipoPreco, vamos usar um padrão por enquanto
            PrecoRequest request = new PrecoRequest(
                    valor,
                    dataAlteracao,
                    LocalDate.now(), // O backend espera horaAlteracao, usando data atual
                    TipoPreco.UNITARIO
            );

            precoService.createPreco(request);
            JOptionPane.showMessageDialog(this, "Preço salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarPrecosDoProduto();
            limparCampos();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-mm-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de valor inválido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar preço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        valorField.setText("");
        dataAlteracaoField.setText("");
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
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PrecoScreen().setVisible(true);
        });
    }
}
