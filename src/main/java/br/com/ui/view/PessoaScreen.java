package br.com.ui.view;

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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close to not exit the whole app
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
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBackground(background);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Dados da Pessoa");
        titledBorder.setTitleColor(textColor);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titledBorder
        ));

        fieldsPanel.add(createStyledLabel("Nome Completo:"));
        nomeCompletoField = createStyledTextField();
        fieldsPanel.add(nomeCompletoField);

        fieldsPanel.add(createStyledLabel("CPF/CNPJ:"));
        cpfCnpjField = createStyledTextField();
        fieldsPanel.add(cpfCnpjField);

        fieldsPanel.add(createStyledLabel("Nº CTPS:"));
        numeroCtpsField = createStyledTextField();
        fieldsPanel.add(numeroCtpsField);

        fieldsPanel.add(createStyledLabel("Data Nascimento (yyyy-mm-dd):"));
        dataNascimentoField = createStyledTextField();
        fieldsPanel.add(dataNascimentoField);

        fieldsPanel.add(createStyledLabel("Tipo de Pessoa:"));
        tipoPessoaComboBox = createStyledComboBox(new String[]{"FISICA", "JURIDICA"});
        fieldsPanel.add(tipoPessoaComboBox);

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
        String[] colunas = {"ID", "Nome", "CPF/CNPJ", "Nascimento", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaPessoas = new JTable(tableModel);

        // Estilo da Tabela
        tabelaPessoas.setBackground(panelBackground);
        tabelaPessoas.setForeground(textColor);
        tabelaPessoas.setGridColor(new Color(80, 80, 80));
        tabelaPessoas.setSelectionBackground(buttonBackground);
        tabelaPessoas.setSelectionForeground(buttonForeground);
        tabelaPessoas.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaPessoas.setRowHeight(25);

        // Estilo do Header da Tabela
        JTableHeader tableHeader = tabelaPessoas.getTableHeader();
        tableHeader.setBackground(new Color(80, 80, 80));
        tableHeader.setForeground(buttonBackground); // Yellow text
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaPessoas);
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

        // Ações (a serem implementadas)
        novoButton.addActionListener(e -> limparCampos());

        salvarButton.addActionListener(e -> {
            // Lógica para salvar (adicionar ou atualizar)
            // Por enquanto, apenas adiciona na tabela
            String[] rowData = {
                    String.valueOf(tableModel.getRowCount() + 1), // ID temporário
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
                    // Campos CTPS e outros podem ser adicionados aqui
                }
            }
        });
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
        button.setPreferredSize(new Dimension(120, 40));
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
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new PessoaScreen().setVisible(true);
        });
    }
}
