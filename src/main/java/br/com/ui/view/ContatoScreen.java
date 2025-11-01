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
                BorderFactory.createTitledBorder(null, "Dados do Contato", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), primary),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Telefone:", text));
        telefoneField = createStyledTextField();
        fieldsPanel.add(telefoneField);

        fieldsPanel.add(createStyledLabel("Email:", text));
        emailField = createStyledTextField();
        fieldsPanel.add(emailField);

        fieldsPanel.add(createStyledLabel("Endereço:", text));
        enderecoField = createStyledTextField();
        fieldsPanel.add(enderecoField);

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
        String[] colunas = {"ID", "Telefone", "Email", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaContatos = new JTable(tableModel);

        // Estilo da Tabela
        tabelaContatos.setBackground(Color.WHITE);
        tabelaContatos.setForeground(text);
        tabelaContatos.setGridColor(Color.LIGHT_GRAY);
        tabelaContatos.setSelectionBackground(accent);
        tabelaContatos.setSelectionForeground(text);
        tabelaContatos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaContatos.setRowHeight(25);

        JTableHeader tableHeader = tabelaContatos.getTableHeader();
        tableHeader.setBackground(primary);
        tableHeader.setForeground(textOnDark);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaContatos);
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
        salvarButton.addActionListener(e -> salvarContato());
        excluirButton.addActionListener(e -> excluirContato());
        tabelaContatos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        carregarContatos();
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

    private void carregarContatos() {
        tableModel.setRowCount(0);
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
