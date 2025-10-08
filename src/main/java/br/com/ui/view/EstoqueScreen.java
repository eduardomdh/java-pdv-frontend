package br.com.ui.view;

import br.com.mock.EstoqueMock;
import br.com.mock.ProdutoMock;
import br.com.model.Estoque;
import br.com.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EstoqueScreen extends JFrame {

    private JTextField quantidadeField, localTanqueField, localEnderecoField, loteFabricacaoField, dataValidadeField;
    private JComboBox<String> produtoComboBox;
    private JTable tabelaEstoque;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Map<String, Long> produtoMap; // Mapeia nome do produto para ID

    public EstoqueScreen() {
        setTitle("Gerenciamento de Estoque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Dados do Estoque"));

        fieldsPanel.add(new JLabel("Produto:"));
        produtoComboBox = new JComboBox<>();
        fieldsPanel.add(produtoComboBox);

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

        fieldsPanel.add(new JLabel("Data de Validade (dd/mm/yyyy):"));
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
        String[] colunas = {"ID", "Produto", "Quantidade", "Tanque", "Lote", "Validade"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaEstoque = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaEstoque);

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.add(fieldsPanel, BorderLayout.NORTH);
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarEstoque());
        excluirButton.addActionListener(e -> excluirEstoque());
        tabelaEstoque.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        // Carrega os dados iniciais
        carregarProdutosComboBox();
        carregarEstoques();
    }

    private void carregarProdutosComboBox() {
        produtoMap = ProdutoMock.getProdutos().stream()
            .collect(Collectors.toMap(Produto::getNome, Produto::getId));
        produtoMap.keySet().forEach(produtoComboBox::addItem);
    }

    private void carregarEstoques() {
        tableModel.setRowCount(0);
        List<Estoque> estoques = EstoqueMock.getEstoques();
        Map<Long, String> nomeProdutoMap = ProdutoMock.getProdutos().stream()
            .collect(Collectors.toMap(Produto::getId, Produto::getNome));

        for (Estoque e : estoques) {
            tableModel.addRow(new Object[]{
                e.getId(),
                nomeProdutoMap.getOrDefault(e.getProdutoId(), "Produto não encontrado"),
                e.getQuantidade(),
                e.getLocalTanque(),
                e.getLoteFabricacao(),
                e.getDataValidade().format(dateFormatter)
            });
        }
    }

    private void salvarEstoque() {
        try {
            String nomeProdutoSelecionado = (String) produtoComboBox.getSelectedItem();
            Long produtoId = produtoMap.get(nomeProdutoSelecionado);

            Estoque novoEstoque = new Estoque(
                null,
                new BigDecimal(quantidadeField.getText()),
                localTanqueField.getText(),
                localEnderecoField.getText(),
                loteFabricacaoField.getText(),
                LocalDate.parse(dataValidadeField.getText(), dateFormatter),
                produtoId
            );
            EstoqueMock.addEstoque(novoEstoque);
            carregarEstoques();
            limparCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar estoque: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirEstoque() {
        int selectedRow = tabelaEstoque.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            EstoqueMock.removeEstoque(id);
            carregarEstoques();
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item do estoque para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCamposComSelecao() {
        int selectedRow = tabelaEstoque.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Estoque estoqueSelecionado = EstoqueMock.getEstoques().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst().orElse(null);

            if (estoqueSelecionado != null) {
                String nomeProduto = ProdutoMock.getProdutos().stream()
                    .filter(p -> p.getId().equals(estoqueSelecionado.getProdutoId()))
                    .map(Produto::getNome).findFirst().orElse(null);

                produtoComboBox.setSelectedItem(nomeProduto);
                quantidadeField.setText(estoqueSelecionado.getQuantidade().toString());
                localTanqueField.setText(estoqueSelecionado.getLocalTanque());
                localEnderecoField.setText(estoqueSelecionado.getLocalEndereco());
                loteFabricacaoField.setText(estoqueSelecionado.getLoteFabricacao());
                dataValidadeField.setText(estoqueSelecionado.getDataValidade().format(dateFormatter));
            }
        }
    }

    private void limparCampos() {
        produtoComboBox.setSelectedIndex(0);
        quantidadeField.setText("");
        localTanqueField.setText("");
        localEnderecoField.setText("");
        loteFabricacaoField.setText("");
        dataValidadeField.setText("");
        tabelaEstoque.clearSelection();
    }
}
