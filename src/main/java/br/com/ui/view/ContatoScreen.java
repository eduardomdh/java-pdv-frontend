package br.com.ui.view;

import br.com.mock.ContatoMock;
import br.com.model.Contato;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ContatoScreen extends JFrame {

    private JTextField telefoneField, emailField, enderecoField;
    private JTable tabelaContatos;
    private DefaultTableModel tableModel;

    public ContatoScreen() {
        setTitle("Gerenciamento de Contatos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Dados do Contato"));

        fieldsPanel.add(new JLabel("Telefone:"));
        telefoneField = new JTextField();
        fieldsPanel.add(telefoneField);

        fieldsPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        fieldsPanel.add(emailField);

        fieldsPanel.add(new JLabel("Endereço:"));
        enderecoField = new JTextField();
        fieldsPanel.add(enderecoField);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton novoButton = new JButton("Novo");
        JButton salvarButton = new JButton("Salvar");
        JButton excluirButton = new JButton("Excluir");
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Telefone", "Email", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaContatos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaContatos);

        // --- Layout Principal ---
        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(fieldsPanel, BorderLayout.NORTH);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarContato());
        excluirButton.addActionListener(e -> excluirContato());
        tabelaContatos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        // Carrega os dados iniciais
        carregarContatos();
    }

    private void carregarContatos() {
        tableModel.setRowCount(0);
        List<Contato> contatos = ContatoMock.getContatos();
        for (Contato c : contatos) {
            tableModel.addRow(new Object[]{c.getId(), c.getTelefone(), c.getEmail(), c.getEndereco()});
        }
    }

    private void salvarContato() {
        Contato novoContato = new Contato(null, telefoneField.getText(), emailField.getText(), enderecoField.getText());
        ContatoMock.addContato(novoContato);
        carregarContatos();
        limparCampos();
    }

    private void excluirContato() {
        int selectedRow = tabelaContatos.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            ContatoMock.removeContato(id);
            carregarContatos();
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um contato para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCamposComSelecao() {
        int selectedRow = tabelaContatos.getSelectedRow();
        if (selectedRow != -1) {
            telefoneField.setText((String) tableModel.getValueAt(selectedRow, 1));
            emailField.setText((String) tableModel.getValueAt(selectedRow, 2));
            enderecoField.setText((String) tableModel.getValueAt(selectedRow, 3));
        }
    }

    private void limparCampos() {
        telefoneField.setText("");
        emailField.setText("");
        enderecoField.setText("");
        tabelaContatos.clearSelection();
    }
}
