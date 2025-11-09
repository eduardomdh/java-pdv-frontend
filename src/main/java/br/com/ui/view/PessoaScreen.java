package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.pessoa.dto.PessoaRequest;
import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.enums.TipoPessoa;
import br.com.pessoa.service.PessoaService;
import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PessoaScreen extends JFrame {

    private JTextField nomeCompletoField, cpfCnpjField, numeroCtpsField, dataNascimentoField;
    private JComboBox<TipoPessoa> tipoPessoaComboBox;
    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;
    private Long pessoaIdEmEdicao;

    private final PessoaService pessoaService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PessoaScreen() {
        this.pessoaService = new PessoaService();

        setTitle("Gerenciamento de Pessoas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

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

        fieldsPanel.add(createStyledLabel("Data Nascimento (dd/MM/yyyy):", ColorPalette.TEXT));
        dataNascimentoField = createStyledTextField();
        fieldsPanel.add(dataNascimentoField);

        fieldsPanel.add(createStyledLabel("Tipo de Pessoa:", ColorPalette.TEXT));
        tipoPessoaComboBox = new JComboBox<>(TipoPessoa.values());
        fieldsPanel.add(tipoPessoaComboBox);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);
        JButton novoButton = createStyledButton("Novo", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton salvarButton = createStyledButton("Salvar", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton editarButton = createStyledButton("Editar", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        JButton excluirButton = createStyledButton("Excluir", ColorPalette.PRIMARY, ColorPalette.WHITE_TEXT);
        buttonsPanel.add(novoButton);
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(editarButton);
        buttonsPanel.add(excluirButton);

        String[] colunas = {"ID", "Nome", "CPF/CNPJ", "Nº CTPS", "Nascimento", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPessoas = new JTable(tableModel);
        tabelaPessoas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPessoas);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarPessoa());
        excluirButton.addActionListener(e -> excluirPessoa());
        editarButton.addActionListener(e -> editarPessoa());

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
                        pessoa.numeroCtps(),
                        pessoa.dataNascimento() != null ? pessoa.dataNascimento().format(dateFormatter) : "",
                        pessoa.tipoPessoa()
                });
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar as pessoas: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor. Verifique sua conexão ou tente novamente mais tarde.");
        }
    }

    private void salvarPessoa() {
        try {
            LocalDate dataNascimento = dataNascimentoField.getText().isEmpty() ? null : LocalDate.parse(dataNascimentoField.getText(), dateFormatter);
            Long numeroCtps = numeroCtpsField.getText().isEmpty() ? null : Long.parseLong(numeroCtpsField.getText());

            PessoaRequest request = new PessoaRequest(
                    nomeCompletoField.getText(),
                    cpfCnpjField.getText(),
                    numeroCtps,
                    dataNascimento,
                    (TipoPessoa) tipoPessoaComboBox.getSelectedItem()
            );

            if (pessoaIdEmEdicao == null) {
                pessoaService.createPessoa(request);
                JOptionPane.showMessageDialog(this, "Pessoa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                pessoaService.updatePessoa(pessoaIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Pessoa atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarPessoas();
            limparCampos();

        } catch (DateTimeParseException ex) {
            showErrorDialog("Erro de Formato", "Formato de data inválido. Use dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            showErrorDialog("Erro de Formato", "Número de CTPS inválido. Deve conter apenas números.");
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível salvar a pessoa: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para salvar a pessoa. Verifique sua conexão.");
        }
    }

    private void editarPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        pessoaIdEmEdicao = (Long) tabelaPessoas.getValueAt(selectedRow, 0);
        nomeCompletoField.setText(tabelaPessoas.getValueAt(selectedRow, 1).toString());
        cpfCnpjField.setText(tabelaPessoas.getValueAt(selectedRow, 2).toString());

        Object ctpsValue = tabelaPessoas.getValueAt(selectedRow, 3);
        numeroCtpsField.setText(ctpsValue != null ? ctpsValue.toString() : "");

        Object dataNascimentoValue = tabelaPessoas.getValueAt(selectedRow, 4);
        dataNascimentoField.setText(dataNascimentoValue != null ? dataNascimentoValue.toString() : "");

        Object tipoPessoaValue = tabelaPessoas.getValueAt(selectedRow, 5);
        if (tipoPessoaValue instanceof TipoPessoa) {
            tipoPessoaComboBox.setSelectedItem(tipoPessoaValue);
        } else if (tipoPessoaValue != null) {
            tipoPessoaComboBox.setSelectedItem(TipoPessoa.valueOf(tipoPessoaValue.toString()));
        }

        JOptionPane.showMessageDialog(this, "Campos preenchidos para edição. Altere os dados e clique em Salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaPessoas.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a pessoa selecionada?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pessoaService.deletePessoa(id);
                JOptionPane.showMessageDialog(this, "Pessoa excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPessoas();
                limparCampos();
            } catch (ApiServiceException e) {
                showErrorDialog("Erro de API", "Não foi possível excluir a pessoa: " + e.getMessage());
            } catch (IOException e) {
                showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para excluir a pessoa. Verifique sua conexão.");
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
        pessoaIdEmEdicao = null;
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new PessoaScreen().setVisible(true);
        });
    }
}
