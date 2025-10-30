package br.com.ui.view;

import br.com.model.Contato;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContatoScreen extends JFrame {

    private JTextField telefoneField, emailField, enderecoField;
    private JTable tabelaContatos;
    private DefaultTableModel tableModel;
    private List<Contato> contatos = new ArrayList<>(); // Internal list to simulate data
    private long nextContatoId = 1; // For simulating new contacts

    public ContatoScreen() {
        setTitle("Gerenciamento de Contatos");
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
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Dados do Contato");
        titledBorder.setTitleColor(textColor);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titledBorder
        ));

        fieldsPanel.add(createStyledLabel("Telefone:"));
        telefoneField = createStyledTextField();
        fieldsPanel.add(telefoneField);

        fieldsPanel.add(createStyledLabel("Email:"));
        emailField = createStyledTextField();
        fieldsPanel.add(emailField);

        fieldsPanel.add(createStyledLabel("Endereço:"));
        enderecoField = createStyledTextField();
        fieldsPanel.add(enderecoField);

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
        String[] colunas = {"ID", "Telefone", "Email", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaContatos = new JTable(tableModel);

        // Estilo da Tabela
        tabelaContatos.setBackground(panelBackground);
        tabelaContatos.setForeground(textColor);
        tabelaContatos.setGridColor(new Color(80, 80, 80));
        tabelaContatos.setSelectionBackground(buttonBackground);
        tabelaContatos.setSelectionForeground(buttonForeground);
        tabelaContatos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaContatos.setRowHeight(25);

        // Estilo do Header da Tabela
        JTableHeader tableHeader = tabelaContatos.getTableHeader();
        tableHeader.setBackground(new Color(80, 80, 80));
        tableHeader.setForeground(buttonBackground); // Yellow text
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaContatos);
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
        salvarButton.addActionListener(e -> salvarContato());
        excluirButton.addActionListener(e -> excluirContato());
        tabelaContatos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        // Carrega os dados iniciais (empty for now)
        carregarContatos();
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

    private void carregarContatos() {
        tableModel.setRowCount(0);
        // In a real application, this would load data from a service
        for (Contato c : contatos) {
            tableModel.addRow(new Object[]{c.getId(), c.getTelefone(), c.getEmail(), c.getEndereco()});
        }
    }

    private void salvarContato() {
        Contato novoContato = new Contato(nextContatoId++, telefoneField.getText(), emailField.getText(), enderecoField.getText());
        contatos.add(novoContato);
        carregarContatos();
        limparCampos();
    }

    private void excluirContato() {
        int selectedRow = tabelaContatos.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            contatos.removeIf(c -> c.getId().equals(id));
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
