package br.com.ui.view;

import br.com.model.Preco;
import br.com.model.Produto;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PrecoScreen extends JFrame {

    private JComboBox<String> produtoComboBox;
    private JTextField valorField, dataAlteracaoField;
    private JTable tabelaPrecos;
    private DefaultTableModel tableModel;
    private Map<String, Long> produtoMap; // Mapeia nome do produto para ID
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private List<Preco> precos = new ArrayList<>(); // Internal list to simulate data
    private long nextPrecoId = 1; // For simulating new prices

    public PrecoScreen() {
        setTitle("Gerenciamento de Preços por Produto");
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

        // --- Painel de Seleção de Produto ---
        JPanel productSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        productSelectionPanel.setBackground(background);
        TitledBorder productBorder = BorderFactory.createTitledBorder("Selecione o Produto");
        productBorder.setTitleColor(textColor);
        productSelectionPanel.setBorder(productBorder);
        produtoComboBox = createStyledComboBox(new String[]{});
        productSelectionPanel.add(createStyledLabel("Produto:"));
        productSelectionPanel.add(produtoComboBox);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.setBackground(background);
        TitledBorder fieldsBorder = BorderFactory.createTitledBorder("Novo Preço");
        fieldsBorder.setTitleColor(textColor);
        fieldsPanel.setBorder(fieldsBorder);
        fieldsPanel.add(createStyledLabel("Valor (ex: 5.89):"));
        valorField = createStyledTextField();
        fieldsPanel.add(valorField);
        fieldsPanel.add(createStyledLabel("Data Alteração (dd/mm/yyyy):"));
        dataAlteracaoField = createStyledTextField();
        fieldsPanel.add(dataAlteracaoField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(background);
        JButton salvarButton = createStyledButton("Salvar Novo Preço");
        JButton excluirButton = createStyledButton("Excluir Preço");
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Valor", "Data de Alteração"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPrecos = new JTable(tableModel);

        // Estilo da Tabela
        tabelaPrecos.setBackground(panelBackground);
        tabelaPrecos.setForeground(textColor);
        tabelaPrecos.setGridColor(new Color(80, 80, 80));
        tabelaPrecos.setSelectionBackground(buttonBackground);
        tabelaPrecos.setSelectionForeground(buttonForeground);
        tabelaPrecos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaPrecos.setRowHeight(25);

        // Estilo do Header da Tabela
        JTableHeader tableHeader = tabelaPrecos.getTableHeader();
        tableHeader.setBackground(new Color(80, 80, 80));
        tableHeader.setForeground(buttonBackground); // Yellow text
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaPrecos);
        tableScrollPane.getViewport().setBackground(background);
        TitledBorder tableBorder = BorderFactory.createTitledBorder("Histórico de Preços do Produto");
        tableBorder.setTitleColor(textColor);
        tableScrollPane.setBorder(tableBorder);

        // --- Layout Principal ---
        JPanel topPanel = new JPanel(new BorderLayout(10,10));
        topPanel.setBackground(background);
        topPanel.add(productSelectionPanel, BorderLayout.NORTH);
        topPanel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(background);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- Ações ---
        produtoComboBox.addActionListener(e -> carregarPrecosDoProduto());
        salvarButton.addActionListener(e -> salvarPreco());
        excluirButton.addActionListener(e -> excluirPreco());

        // Carrega os dados iniciais
        carregarProdutosComboBox();
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

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(new Color(60, 63, 65));
        comboBox.setForeground(Color.WHITE);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 204, 0));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0), 2));
        button.setPreferredSize(new Dimension(180, 40));
        return button;
    }

    private void carregarProdutosComboBox() {


        // Limpa o combo box antes de adicionar novos itens
        produtoComboBox.removeAllItems();
        produtoMap.keySet().forEach(produtoComboBox::addItem);
        // O ActionListener do ComboBox cuidará de carregar os preços do primeiro item
    }

    private void carregarPrecosDoProduto() {
        tableModel.setRowCount(0);
        String nomeProdutoSelecionado = (String) produtoComboBox.getSelectedItem();
        if (nomeProdutoSelecionado == null) return;

        Long produtoId = produtoMap.get(nomeProdutoSelecionado);
        List<Preco> filteredPrecos = precos.stream()
            .filter(p -> p.getProdutoId().equals(produtoId))
            .collect(Collectors.toList());

        for (Preco p : filteredPrecos) {
            tableModel.addRow(new Object[]{
                p.getId(),
                currencyFormat.format(p.getValor()),
                p.getDataAlteracao().format(dateFormatter)
            });
        }
    }

    private void salvarPreco() {
        try {
            String nomeProdutoSelecionado = (String) produtoComboBox.getSelectedItem();
            Long produtoId = produtoMap.get(nomeProdutoSelecionado);

            String valorTexto = valorField.getText().replaceAll("[^0-9,.]", "").replace(".", "").replace(",", ".");

            Preco novoPreco = new Preco(
                nextPrecoId++,
                new BigDecimal(valorTexto),
                LocalDate.parse(dataAlteracaoField.getText(), dateFormatter),
                produtoId
            );
            precos.add(novoPreco); // Add to internal list
            carregarPrecosDoProduto(); // Recarrega a tabela para o produto atual
            limparCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar preço: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPreco() {
        int selectedRow = tabelaPrecos.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            precos.removeIf(p -> p.getId().equals(id)); // Remove from internal list
            carregarPrecosDoProduto();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um preço na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparCampos() {
        valorField.setText("");
        dataAlteracaoField.setText("");
    }
}
