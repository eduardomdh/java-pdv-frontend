package br.com.ui.view;

import br.com.model.Contato;
import br.com.ui.util.ColorPalette;

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

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Contato", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Telefone:", ColorPalette.TEXT));
        telefoneField = createStyledTextField();
        fieldsPanel.add(telefoneField);

        fieldsPanel.add(createStyledLabel("Email:", ColorPalette.TEXT));
        emailField = createStyledTextField();
        fieldsPanel.add(emailField);

        fieldsPanel.add(createStyledLabel("Endereço:", ColorPalette.TEXT));
        enderecoField = createStyledTextField();
        fieldsPanel.add(enderecoField);

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
        String[] colunas = {"ID", "Telefone", "Email", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaContatos = new JTable(tableModel);

        // Estilo da Tabela
        tabelaContatos.setBackground(ColorPalette.PANEL_BACKGROUND);
        tabelaContatos.setForeground(ColorPalette.TEXT);
        tabelaContatos.setGridColor(new Color(200, 200, 200));
        tabelaContatos.setSelectionBackground(ColorPalette.PRIMARY);
        tabelaContatos.setSelectionForeground(ColorPalette.WHITE_TEXT);
        tabelaContatos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaContatos.setRowHeight(25);

        JTableHeader tableHeader = tabelaContatos.getTableHeader();
        tableHeader.setBackground(ColorPalette.PRIMARY);
        tableHeader.setForeground(ColorPalette.WHITE_TEXT);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaContatos);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

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
