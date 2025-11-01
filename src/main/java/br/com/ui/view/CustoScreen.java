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

        // Cores
        Color background = new Color(240, 240, 240);
        Color primary = new Color(163, 31, 52);
        Color secondary = new Color(0, 153, 102);
        Color text = new Color(51, 51, 51);
        Color textOnDark = Color.WHITE;
        Color accent = new Color(255, 204, 0);

        Container contentPane = getContentPane();
        contentPane.setBackground(background);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Custo", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), primary),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Descrição (ex: Conta de Água):", text));
        descricaoField = createStyledTextField();
        fieldsPanel.add(descricaoField);

        fieldsPanel.add(createStyledLabel("Valor (ex: 480.50):", text));
        valorField = createStyledTextField();
        fieldsPanel.add(valorField);

        fieldsPanel.add(createStyledLabel("Data de Vencimento (dd/mm/yyyy):", text));
        dataVencimentoField = createStyledTextField();
        fieldsPanel.add(dataVencimentoField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);
        JButton novoButton = createStyledButton("Novo", secondary, textOnDark);
        JButton salvarButton = createStyledButton("Salvar", primary, textOnDark);
        JButton excluirButton = createStyledButton("Excluir", accent, text);
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
        tabelaCustos.setBackground(Color.WHITE);
        tabelaCustos.setForeground(text);
        tabelaCustos.setGridColor(Color.LIGHT_GRAY);
        tabelaCustos.setSelectionBackground(accent);
        tabelaCustos.setSelectionForeground(text);
        tabelaCustos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaCustos.setRowHeight(25);

        JTableHeader tableHeader = tabelaCustos.getTableHeader();
        tableHeader.setBackground(primary);
        tableHeader.setForeground(textOnDark);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaCustos);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
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

        carregarCustos();
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
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
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

    private void carregarCustos() {
        tableModel.setRowCount(0);
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
                valorField.setText(custoSelecionado.getValor().toPlainString());
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
