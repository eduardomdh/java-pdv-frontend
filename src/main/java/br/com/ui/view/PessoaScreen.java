package br.com.ui.view;

import br.com.mock.PessoaMock;
import br.com.model.Pessoa;
import br.com.model.TipoPessoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PessoaScreen extends JFrame {

    private JTextField nomeCompletoField, cpfCnpjField, numeroCtpsField, dataNascimentoField;
    private JComboBox<TipoPessoa> tipoPessoaComboBox;
    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PessoaScreen() {
        setTitle("Gerenciamento de Pessoas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        fieldsPanel.add(new JLabel("Data Nascimento (dd/mm/yyyy):"));
        dataNascimentoField = new JTextField();
        fieldsPanel.add(dataNascimentoField);

        fieldsPanel.add(new JLabel("Tipo de Pessoa:"));
        tipoPessoaComboBox = new JComboBox<>(TipoPessoa.values());
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
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        tabelaPessoas = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPessoas);

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.add(fieldsPanel, BorderLayout.NORTH);
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarPessoa());
        excluirButton.addActionListener(e -> excluirPessoa());
        tabelaPessoas.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        // Carrega os dados iniciais
        carregarPessoas();
    }

    private void carregarPessoas() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Pessoa> pessoas = PessoaMock.getPessoas();
        for (Pessoa p : pessoas) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getNomeCompleto(),
                p.getCpfCnpj(),
                p.getDataNascimento().format(dateFormatter),
                p.getTipoPessoa()
            });
        }
    }

    private void salvarPessoa() {
        try {
            Pessoa novaPessoa = new Pessoa(
                null, // ID será gerado pelo Mock
                nomeCompletoField.getText(),
                cpfCnpjField.getText(),
                Long.parseLong(numeroCtpsField.getText()),
                LocalDate.parse(dataNascimentoField.getText(), dateFormatter),
                (TipoPessoa) tipoPessoaComboBox.getSelectedItem()
            );
            PessoaMock.addPessoa(novaPessoa);
            carregarPessoas(); // Recarrega a tabela
            limparCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar pessoa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            PessoaMock.removePessoa(id);
            carregarPessoas(); // Recarrega a tabela
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCamposComSelecao() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow != -1) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            // Busca a pessoa original no mock para obter todos os dados
            Pessoa pessoaSelecionada = PessoaMock.getPessoas().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);

            if (pessoaSelecionada != null) {
                nomeCompletoField.setText(pessoaSelecionada.getNomeCompleto());
                cpfCnpjField.setText(pessoaSelecionada.getCpfCnpj());
                numeroCtpsField.setText(pessoaSelecionada.getNumeroCtps() != null ? pessoaSelecionada.getNumeroCtps().toString() : "");
                dataNascimentoField.setText(pessoaSelecionada.getDataNascimento().format(dateFormatter));
                tipoPessoaComboBox.setSelectedItem(pessoaSelecionada.getTipoPessoa());
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
    }
}
