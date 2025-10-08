package br.com.ui.view;

import br.com.mock.CustoMock;
import br.com.model.Custo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CustoScreen extends JFrame {

    private JTextField descricaoField, valorField, dataVencimentoField;
    private JTable tabelaCustos;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public CustoScreen() {
        setTitle("Gerenciamento de Custos Operacionais");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Dados do Custo"));

        fieldsPanel.add(new JLabel("Descrição (ex: Conta de Água):"));
        descricaoField = new JTextField();
        fieldsPanel.add(descricaoField);

        fieldsPanel.add(new JLabel("Valor (ex: 480.50):"));
        valorField = new JTextField();
        fieldsPanel.add(valorField);

        fieldsPanel.add(new JLabel("Data de Vencimento (dd/mm/yyyy):"));
        dataVencimentoField = new JTextField();
        fieldsPanel.add(dataVencimentoField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton novoButton = new JButton("Novo");
        JButton salvarButton = new JButton("Salvar");
        JButton excluirButton = new JButton("Excluir");
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Descrição", "Valor", "Data de Vencimento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCustos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaCustos);

        // --- Layout Principal ---
        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(fieldsPanel, BorderLayout.NORTH);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarCusto());
        excluirButton.addActionListener(e -> excluirCusto());
        tabelaCustos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        // Carrega os dados iniciais
        carregarCustos();
    }

    private void carregarCustos() {
        tableModel.setRowCount(0);
        List<Custo> custos = CustoMock.getCustos();
        for (Custo c : custos) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getDescricao(),
                currencyFormat.format(c.getValor()),
                c.getDataVencimento().format(dateFormatter)
            });
        }
    }

    private void salvarCusto() {
        try {
            // Remove formatação de moeda para fazer o parse
            String valorTexto = valorField.getText().replaceAll("[^0-9,.]", "").replace(".", "").replace(",", ".");
            Custo novoCusto = new Custo(
                null,
                descricaoField.getText(),
                new BigDecimal(valorTexto),
                LocalDate.parse(dataVencimentoField.getText(), dateFormatter)
            );
            CustoMock.addCusto(novoCusto);
            carregarCustos();
            limparCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar custo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCusto() {
        int selectedRow = tabelaCustos.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            CustoMock.removeCusto(id);
            carregarCustos();
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um custo para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCamposComSelecao() {
        int selectedRow = tabelaCustos.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Custo custoSelecionado = CustoMock.getCustos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);

            if (custoSelecionado != null) {
                descricaoField.setText(custoSelecionado.getDescricao());
                valorField.setText(custoSelecionado.getValor().toPlainString()); // Usa toPlainString para evitar notação científica
                dataVencimentoField.setText(custoSelecionado.getDataVencimento().format(dateFormatter));
            }
        }
    }

    private void limparCampos() {
        descricaoField.setText("");
        valorField.setText("");
        dataVencimentoField.setText("");
        tabelaCustos.clearSelection();
    }
}
