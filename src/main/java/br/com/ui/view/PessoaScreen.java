package br.com.ui.view;

import br.com.pessoa.dto.PessoaRequest;
import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.enums.TipoPessoa;
import br.com.pessoa.service.PessoaService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PessoaScreen extends JFrame {

    private JTextField nomeCompletoField, cpfCnpjField, numeroCtpsField, dataNascimentoField;
    private JComboBox<TipoPessoa> tipoPessoaComboBox;
    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;
    private Long pessoaIdEmEdicao; // Novo campo para armazenar o ID da pessoa em edição

    private final PessoaService pessoaService;

    public PessoaScreen() {
        this.pessoaService = new PessoaService();

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
        tipoPessoaComboBox = new JComboBox<>(TipoPessoa.values());
        fieldsPanel.add(tipoPessoaComboBox);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);
        JButton novoButton = createStyledButton("Novo", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton salvarButton = createStyledButton("Salvar", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton editarButton = createStyledButton("Editar", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT); // Novo botão Editar
        JButton excluirButton = createStyledButton("Excluir", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(editarButton); // Adiciona o botão Editar
        buttonsPanel.add(excluirButton);

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "CPF/CNPJ", "Nascimento", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaPessoas = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPessoas);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Ações
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarPessoa());
        excluirButton.addActionListener(e -> excluirPessoa());
        editarButton.addActionListener(e -> editarPessoa()); // Ação para o botão Editar

        carregarPessoas();
    }

    private void carregarPessoas() {
        tableModel.setRowCount(0);
        try {
            List<PessoaResponse> pessoas = pessoaService.findPessoas();
            for (PessoaResponse pessoa : pessoas) {
                tableModel.addRow(new Object[]{
                        pessoa.id(),
                        pessoa.nomeCompleto(),
                        pessoa.cpfCnpj(),
                        pessoa.dataNascimento(),
                        pessoa.tipoPessoa()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pessoas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void salvarPessoa() {
        try {
            LocalDate dataNascimento = dataNascimentoField.getText().isEmpty() ? null : LocalDate.parse(dataNascimentoField.getText());
            Long numeroCtps = numeroCtpsField.getText().isEmpty() ? null : Long.parseLong(numeroCtpsField.getText());

            PessoaRequest request = new PessoaRequest(
                    nomeCompletoField.getText(),
                    cpfCnpjField.getText(),
                    numeroCtps,
                    dataNascimento,
                    (TipoPessoa) tipoPessoaComboBox.getSelectedItem()
            );

            if (pessoaIdEmEdicao == null) { // Se não há ID em edição, é uma nova pessoa
                pessoaService.createPessoa(request);
                JOptionPane.showMessageDialog(this, "Pessoa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Se há um ID em edição, é uma atualização
                pessoaService.updatePessoa(pessoaIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Pessoa atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarPessoas();
            limparCampos();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-mm-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Número de CTPS inválido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar pessoa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editarPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        pessoaIdEmEdicao = (Long) tabelaPessoas.getValueAt(selectedRow, 0); // Pega o ID da primeira coluna
        nomeCompletoField.setText(tabelaPessoas.getValueAt(selectedRow, 1).toString());
        cpfCnpjField.setText(tabelaPessoas.getValueAt(selectedRow, 2).toString());
        // CTPS pode ser nulo, então verificamos antes de tentar converter
        Object ctpsValue = tabelaPessoas.getValueAt(selectedRow, 3);
        numeroCtpsField.setText(ctpsValue != null ? ctpsValue.toString() : "");
        dataNascimentoField.setText(tabelaPessoas.getValueAt(selectedRow, 4).toString());
        tipoPessoaComboBox.setSelectedItem(TipoPessoa.valueOf(tabelaPessoas.getValueAt(selectedRow, 5).toString()));

        JOptionPane.showMessageDialog(this, "Campos preenchidos para edição. Altere os dados e clique em Salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaPessoas.getValueAt(selectedRow, 0); // Assumindo que o ID está na primeira coluna

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a pessoa selecionada?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pessoaService.deletePessoa(id);
                JOptionPane.showMessageDialog(this, "Pessoa excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPessoas(); // Recarrega a lista da API
                limparCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir pessoa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void limparCampos() {
        nomeCompletoField.setText("");
        cpfCnpjField.setText("");
        numeroCtpsField.setText("");
        dataNascimentoField.setText("");
        tipoPessoaComboBox.setSelectedIndex(0);
        tabelaPessoas.clearSelection();
        pessoaIdEmEdicao = null; // Limpa o ID em edição
    }

    // Métodos de estilo (createStyledLabel, etc.) permanecem os mesmos
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PessoaScreen().setVisible(true);
        });
    }
}
