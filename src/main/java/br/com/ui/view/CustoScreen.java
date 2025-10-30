package br.com.ui.view;

import br.com.model.Custo;

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

public class CustoScreen extends JFrame {

    private JTextField descricaoField, valorField, dataVencimentoField;
    private JTable tabelaCustos;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private List<Custo> custos = new ArrayList<>(); // Internal list to simulate data
    private long nextCustoId = 1; // For simulating new costs

    public CustoScreen() {
        setTitle("Gerenciamento de Custos Operacionais");
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

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBackground(background);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Dados do Custo");
        titledBorder.setTitleColor(textColor);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titledBorder
        ));

        fieldsPanel.add(createStyledLabel("Descrição (ex: Conta de Água):"));
        descricaoField = createStyledTextField();
        fieldsPanel.add(descricaoField);

        fieldsPanel.add(createStyledLabel("Valor (ex: 480.50):"));
        valorField = createStyledTextField();
        fieldsPanel.add(valorField);

        fieldsPanel.add(createStyledLabel("Data de Vencimento (dd/mm/yyyy):"));
        dataVencimentoField = createStyledTextField();
        fieldsPanel.add(dataVencimentoField);

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
        String[] colunas = {"ID", "Descrição", "Valor", "Data de Vencimento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCustos = new JTable(tableModel);

        // Estilo da Tabela
        tabelaCustos.setBackground(panelBackground);
        tabelaCustos.setForeground(textColor);
        tabelaCustos.setGridColor(new Color(80, 80, 80));
        tabelaCustos.setSelectionBackground(buttonBackground);
        tabelaCustos.setSelectionForeground(buttonForeground);
        tabelaCustos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaCustos.setRowHeight(25);

        // Estilo do Header da Tabela
        JTableHeader tableHeader = tabelaCustos.getTableHeader();
        tableHeader.setBackground(new Color(80, 80, 80));
        tableHeader.setForeground(buttonBackground); // Yellow text
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaCustos);
        tableScrollPane.getViewport().setBackground(background);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(panelBackground, 2));

        // Adiciona um espaçamento geral
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(background);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarCusto());
        excluirButton.addActionListener(e -> excluirCusto());
        tabelaCustos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        // Carrega os dados iniciais (empty for now)
        carregarCustos();
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

    private void carregarCustos() {
        tableModel.setRowCount(0);
        // In a real application, this would load data from a service
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
                nextCustoId++,
                descricaoField.getText(),
                new BigDecimal(valorTexto),
                LocalDate.parse(dataVencimentoField.getText(), dateFormatter)
            );
            custos.add(novoCusto);
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
            custos.removeIf(c -> c.getId().equals(id));
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
            Custo custoSelecionado = custos.stream()
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
