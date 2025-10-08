package br.com.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Dados da Pessoa"));

        fieldsPanel.add(new JLabel("Nome Completo:"));
        nomeCompletoField = new JTextField();
        fieldsPanel.add(nomeCompletoField);

        fieldsPanel.add(new JLabel("CPF/CNPJ:"));
        cpfCnpjField = new JTextField();
        fieldsPanel.add(cpfCnpjField);

        fieldsPanel.add(new JLabel("Nº CTPS:"));
        numeroCtpsField = new JTextField();
        fieldsPanel.add(numeroCtpsField);

        fieldsPanel.add(new JLabel("Data Nascimento (yyyy-mm-dd):"));
        dataNascimentoField = new JTextField();
        fieldsPanel.add(dataNascimentoField);

        fieldsPanel.add(new JLabel("Tipo de Pessoa:"));
        tipoPessoaComboBox = new JComboBox<>(new String[]{"FISICA", "JURIDICA"});
        fieldsPanel.add(tipoPessoaComboBox);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton novoButton = new JButton("Novo");
        JButton salvarButton = new JButton("Salvar");
        JButton excluirButton = new JButton("Excluir");
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "CPF/CNPJ", "Nascimento", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaPessoas = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPessoas);

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.add(fieldsPanel, BorderLayout.NORTH);
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

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
