package br.com.ui.view;

import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.service.PessoaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SelecaoPessoaScreen extends JDialog {

    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;
    private JButton selecionarButton;
    private PessoaResponse pessoaSelecionada;
    private List<PessoaResponse> listaPessoas;

    private final PessoaService pessoaService;

    public SelecaoPessoaScreen(Frame owner) {
        super(owner, "Selecionar Pessoa", true);
        this.pessoaService = new PessoaService();

        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "CPF/CNPJ"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPessoas = new JTable(tableModel);
        tabelaPessoas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPessoas);

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selecionarButton = new JButton("Selecionar");
        JButton cancelarButton = new JButton("Cancelar");
        buttonsPanel.add(selecionarButton);
        buttonsPanel.add(cancelarButton);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Ações
        selecionarButton.addActionListener(e -> onSelecionar());
        cancelarButton.addActionListener(e -> onCancelar());

        carregarPessoas();
    }

    private void carregarPessoas() {
        tableModel.setRowCount(0);
        try {
            this.listaPessoas = pessoaService.findPessoas();
            for (PessoaResponse pessoa : listaPessoas) {
                tableModel.addRow(new Object[]{
                        pessoa.id(),
                        pessoa.nomeCompleto(),
                        pessoa.cpfCnpj()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pessoas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onSelecionar() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long pessoaId = (Long) tableModel.getValueAt(selectedRow, 0);
        this.pessoaSelecionada = listaPessoas.stream()
                .filter(p -> p.id().equals(pessoaId))
                .findFirst()
                .orElse(null);
        setVisible(false);
    }

    private void onCancelar() {
        this.pessoaSelecionada = null;
        setVisible(false);
    }

    public PessoaResponse getPessoaSelecionada() {
        return pessoaSelecionada;
    }
}
