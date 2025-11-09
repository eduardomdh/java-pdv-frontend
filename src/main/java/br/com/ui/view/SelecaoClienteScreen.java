package br.com.ui.view;

import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.service.PessoaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SelecaoClienteScreen extends JDialog {

    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private JButton selecionarButton;
    private JButton consumidorNaoIdentificadoButton;
    private PessoaResponse clienteSelecionado;
    private boolean consumidorNaoIdentificado = false;
    private List<PessoaResponse> listaClientes;

    private final PessoaService pessoaService;

    public SelecaoClienteScreen(Frame owner) {
        super(owner, "Selecionar Cliente", true);
        this.pessoaService = new PessoaService();

        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID", "Nome", "CPF/CNPJ"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tabelaClientes);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selecionarButton = new JButton("Selecionar Cliente");
        consumidorNaoIdentificadoButton = new JButton("Consumidor Não Identificado");
        JButton cancelarButton = new JButton("Cancelar");
        buttonsPanel.add(selecionarButton);
        buttonsPanel.add(consumidorNaoIdentificadoButton);
        buttonsPanel.add(cancelarButton);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        selecionarButton.addActionListener(e -> onSelecionar());
        consumidorNaoIdentificadoButton.addActionListener(e -> onConsumidorNaoIdentificado());
        cancelarButton.addActionListener(e -> onCancelar());

        carregarClientes();
    }

    private void carregarClientes() {
        tableModel.setRowCount(0);
        try {
            this.listaClientes = pessoaService.findPessoas();
            for (PessoaResponse cliente : listaClientes) {
                tableModel.addRow(new Object[]{
                        cliente.id(),
                        cliente.nomeCompleto(),
                        cliente.cpfCnpj()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onSelecionar() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long clienteId = (Long) tableModel.getValueAt(selectedRow, 0);
        this.clienteSelecionado = listaClientes.stream()
                .filter(p -> p.id().equals(clienteId))
                .findFirst()
                .orElse(null);
        this.consumidorNaoIdentificado = false;
        setVisible(false);
    }

    private void onConsumidorNaoIdentificado() {
        this.clienteSelecionado = null;
        this.consumidorNaoIdentificado = true;
        setVisible(false);
    }

    private void onCancelar() {
        this.clienteSelecionado = null;
        this.consumidorNaoIdentificado = false;
        setVisible(false);
    }

    public PessoaResponse getClienteSelecionado() {
        return clienteSelecionado;
    }

    public boolean isConsumidorNaoIdentificado() {
        return consumidorNaoIdentificado;
    }
}
