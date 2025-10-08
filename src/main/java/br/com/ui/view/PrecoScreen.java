package br.com.ui.view;

import br.com.mock.PrecoMock;
import br.com.mock.ProdutoMock;
import br.com.model.Preco;
import br.com.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public PrecoScreen() {
        setTitle("Gerenciamento de Preços por Produto");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Seleção de Produto ---
        JPanel productSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productSelectionPanel.setBorder(BorderFactory.createTitledBorder("Selecione o Produto"));
        produtoComboBox = new JComboBox<>();
        productSelectionPanel.add(new JLabel("Produto:"));
        productSelectionPanel.add(produtoComboBox);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Novo Preço"));
        fieldsPanel.add(new JLabel("Valor (ex: 5.89):"));
        valorField = new JTextField();
        fieldsPanel.add(valorField);
        fieldsPanel.add(new JLabel("Data Alteração (dd/mm/yyyy):"));
        dataAlteracaoField = new JTextField();
        fieldsPanel.add(dataAlteracaoField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton salvarButton = new JButton("Salvar Novo Preço");
        JButton excluirButton = new JButton("Excluir Preço Selecionado");
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Valor", "Data de Alteração"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPrecos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPrecos);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Histórico de Preços do Produto"));

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(productSelectionPanel, BorderLayout.NORTH);
        topPanel.add(fieldsPanel, BorderLayout.CENTER);

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        // --- Ações ---
        produtoComboBox.addActionListener(e -> carregarPrecosDoProduto());
        salvarButton.addActionListener(e -> salvarPreco());
        excluirButton.addActionListener(e -> excluirPreco());

        // Carrega os dados iniciais
        carregarProdutosComboBox();
    }

    private void carregarProdutosComboBox() {
        produtoMap = ProdutoMock.getProdutos().stream()
            .collect(Collectors.toMap(Produto::getNome, Produto::getId));
        produtoMap.keySet().forEach(produtoComboBox::addItem);
        // O ActionListener do ComboBox cuidará de carregar os preços do primeiro item
    }

    private void carregarPrecosDoProduto() {
        tableModel.setRowCount(0);
        String nomeProdutoSelecionado = (String) produtoComboBox.getSelectedItem();
        if (nomeProdutoSelecionado == null) return;

        Long produtoId = produtoMap.get(nomeProdutoSelecionado);
        List<Preco> precos = PrecoMock.getPrecos().stream()
            .filter(p -> p.getProdutoId().equals(produtoId))
            .collect(Collectors.toList());

        for (Preco p : precos) {
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
                null,
                new BigDecimal(valorTexto),
                LocalDate.parse(dataAlteracaoField.getText(), dateFormatter),
                produtoId
            );
            PrecoMock.addPreco(novoPreco);
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
            PrecoMock.removePreco(id);
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
