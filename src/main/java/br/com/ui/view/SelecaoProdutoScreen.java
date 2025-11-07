package br.com.ui.view;

import br.com.estoque.dto.EstoqueResponse;
import br.com.estoque.service.EstoqueService;
import br.com.preco.dto.PrecoResponse;
import br.com.preco.service.PrecoService;
import br.com.produto.dto.ProdutoResponse;
import br.com.produto.service.ProdutoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SelecaoProdutoScreen extends JDialog {

    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private JButton selecionarButton;
    private ProdutoResponse produtoSelecionado;
    private List<ProdutoResponse> listaProdutos;

    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;
    private final PrecoService precoService;

    public SelecaoProdutoScreen(Frame owner) {
        super(owner, "Selecionar Produto", true);
        this.produtoService = new ProdutoService();
        this.estoqueService = new EstoqueService();
        this.precoService = new PrecoService();

        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "Referência", "Preço", "Estoque"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        tabelaProdutos = new JTable(tableModel);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tabelaProdutos);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selecionarButton = new JButton("Selecionar");
        JButton cancelarButton = new JButton("Cancelar");
        buttonsPanel.add(selecionarButton);
        buttonsPanel.add(cancelarButton);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Ações
        selecionarButton.addActionListener(e -> onSelecionar());
        cancelarButton.addActionListener(e -> onCancelar());

        carregarProdutos();
    }

    private void carregarProdutos() {
        tableModel.setRowCount(0);
        try {
            this.listaProdutos = produtoService.findProducts();
            List<EstoqueResponse> estoques = estoqueService.findEstoques();
            List<PrecoResponse> precos = precoService.findPrecos();

            Map<Long, BigDecimal> estoqueMap = estoques.stream()
                    .collect(Collectors.groupingBy(
                            EstoqueResponse::produtoId,
                            Collectors.reducing(BigDecimal.ZERO, EstoqueResponse::quantidade, BigDecimal::add)
                    ));

            Map<Long, Optional<PrecoResponse>> precoMap = precos.stream()
                    .collect(Collectors.groupingBy(
                            PrecoResponse::produtoId,
                            Collectors.maxBy(Comparator.comparing(PrecoResponse::dataAlteracao))
                    ));

            for (ProdutoResponse produto : listaProdutos) {
                BigDecimal quantidade = estoqueMap.getOrDefault(produto.id(), BigDecimal.ZERO);
                BigDecimal preco = precoMap.getOrDefault(produto.id(), Optional.empty())
                        .map(PrecoResponse::valor)
                        .orElse(BigDecimal.ZERO);

                tableModel.addRow(new Object[]{
                        produto.id(),
                        produto.nome(),
                        produto.referencia(),
                        preco,
                        quantidade
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados combinados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onSelecionar() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long produtoId = (Long) tableModel.getValueAt(selectedRow, 0);
        this.produtoSelecionado = listaProdutos.stream()
                .filter(p -> p.id().equals(produtoId))
                .findFirst()
                .orElse(null);

        setVisible(false);
    }

    private void onCancelar() {
        this.produtoSelecionado = null;
        setVisible(false);
    }

    public ProdutoResponse getProdutoSelecionado() {
        return produtoSelecionado;
    }
}
