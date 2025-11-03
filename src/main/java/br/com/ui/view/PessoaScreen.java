package br.com.ui.view;

import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class PessoaScreen extends JFrame {

    private JTextField nomeCompletoField, cpfCnpjField, numeroCtpsField, dataNascimentoField;
    private JComboBox<String> tipoPessoaComboBox;
    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;

    public PessoaScreen() {
        setTitle("Gerenciamento de Pessoas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados da Pessoa", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Nome Completo:", ColorPalette.TEXT));
        nomeCompletoField = createStyledTextField();
        fieldsPanel.add(nomeCompletoField);

        fieldsPanel.add(createStyledLabel("CPF/CNPJ:", ColorPalette.TEXT));
        cpfCnpjField = createStyledTextField();
        fieldsPanel.add(cpfCnpjField);

        fieldsPanel.add(createStyledLabel("Nº CTPS:", ColorPalette.TEXT));
        numeroCtpsField = createStyledTextField();
        fieldsPanel.add(numeroCtpsField);

        fieldsPanel.add(createStyledLabel("Data Nascimento (yyyy-mm-dd):", ColorPalette.TEXT));
        dataNascimentoField = createStyledTextField();
        fieldsPanel.add(dataNascimentoField);

        fieldsPanel.add(createStyledLabel("Tipo de Pessoa:", ColorPalette.TEXT));
        tipoPessoaComboBox = new JComboBox<>(new String[]{"FISICA", "JURIDICA"});
        tipoPessoaComboBox.setRenderer(new DefaultListCellRenderer() {
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
        fieldsPanel.add(tipoPessoaComboBox);

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
        String[] colunas = {"ID", "Nome", "CPF/CNPJ", "Nascimento", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaPessoas = new JTable(tableModel);

        // Estilo da Tabela
        tabelaPessoas.setBackground(ColorPalette.PANEL_BACKGROUND);
        tabelaPessoas.setForeground(ColorPalette.TEXT);
        tabelaPessoas.setGridColor(new Color(200, 200, 200));
        tabelaPessoas.setSelectionBackground(ColorPalette.PRIMARY);
        tabelaPessoas.setSelectionForeground(ColorPalette.WHITE_TEXT);
        tabelaPessoas.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaPessoas.setRowHeight(25);

        JTableHeader tableHeader = tabelaPessoas.getTableHeader();
        tableHeader.setBackground(ColorPalette.PRIMARY);
        tableHeader.setForeground(ColorPalette.WHITE_TEXT);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaPessoas);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Ações (a serem implementadas)
        novoButton.addActionListener(e -> limparCampos());

        salvarButton.addActionListener(e -> {
            String[] rowData = {
                    String.valueOf(tableModel.getRowCount() + 1),
                    nomeCompletoField.getText(),
                    cpfCnpjField.getText(),
                    dataNascimentoField.getText(),
                    (String) tipoPessoaComboBox.getSelectedItem()
            };
            tableModel.addRow(rowData);
            limparCampos();
        });

        excluirButton.addActionListener(e -> {
            int selectedRow = tabelaPessoas.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        tabelaPessoas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaPessoas.getSelectedRow();
                if (selectedRow != -1) {
                    nomeCompletoField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    cpfCnpjField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    dataNascimentoField.setText((String) tableModel.getValueAt(selectedRow, 3));
                    tipoPessoaComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
                }
            }
        });
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

    private void limparCampos() {
        nomeCompletoField.setText("");
        cpfCnpjField.setText("");
        numeroCtpsField.setText("");
        dataNascimentoField.setText("");
        tipoPessoaComboBox.setSelectedIndex(0);
        tabelaPessoas.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PessoaScreen().setVisible(true);
        });
    }
}
