package br.com.ui.view;

import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.service.PessoaService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SelecaoClienteScreen extends JDialog {

    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private PessoaResponse clienteSelecionado;
    private boolean consumidorNaoIdentificado = false;
    private List<PessoaResponse> listaClientes;
    private JTextField searchField;

    private final PessoaService pessoaService;

    public SelecaoClienteScreen(Frame owner) {
        super(owner, "Selecionar Cliente", true);
        this.pessoaService = new PessoaService();

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(ColorPalette.TEXT); // Fundo escuro

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Tabela
        add(createTablePanel(), BorderLayout.CENTER);

        // Botões
        add(createButtonsPanel(), BorderLayout.SOUTH);

        carregarClientes();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(ColorPalette.TEXT); // Fundo escuro
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Selecione um Cliente");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorPalette.WHITE_TEXT); // Texto branco
        headerPanel.add(titleLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBackground(ColorPalette.PANEL_BACKGROUND); // Fundo claro para contraste
        searchField.setForeground(ColorPalette.TEXT); // Texto escuro
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        headerPanel.add(searchField, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ColorPalette.TEXT); // Fundo escuro
        tablePanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        String[] colunas = {"ID", "Nome", "CPF/CNPJ"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaClientes.setRowHeight(30);
        tabelaClientes.setGridColor(ColorPalette.BORDER_COLOR);
        tabelaClientes.setBackground(ColorPalette.TEXT); // Fundo escuro da tabela
        tabelaClientes.setForeground(ColorPalette.WHITE_TEXT); // Texto branco da tabela

        JTableHeader header = tabelaClientes.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.TEXT_MUTED); // Fundo escuro do cabeçalho da tabela
        header.setForeground(ColorPalette.WHITE_TEXT); // Texto branco do cabeçalho da tabela

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        scrollPane.setBackground(ColorPalette.TEXT); // Fundo escuro do scrollPane
        scrollPane.getViewport().setBackground(ColorPalette.TEXT); // Fundo escuro do viewport do scrollPane
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(ColorPalette.TEXT); // Fundo escuro

        JButton consumidorNaoIdentificadoButton = createButton("Consumidor Não Identificado");
        consumidorNaoIdentificadoButton.addActionListener(e -> onConsumidorNaoIdentificado());
        buttonsPanel.add(consumidorNaoIdentificadoButton);

        JButton cancelarButton = createButton("Cancelar");
        cancelarButton.addActionListener(e -> onCancelar());
        buttonsPanel.add(cancelarButton);

        JButton selecionarButton = createButton("Selecionar Cliente");
        selecionarButton.addActionListener(e -> onSelecionar());
        buttonsPanel.add(selecionarButton);

        return buttonsPanel;
    }

    private void carregarClientes() {
        try {
            this.listaClientes = pessoaService.findPessoas();
            updateTable(this.listaClientes);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<PessoaResponse> clientes) {
        tableModel.setRowCount(0);
        for (PessoaResponse cliente : clientes) {
            tableModel.addRow(new Object[]{
                    cliente.id(),
                    cliente.nomeCompleto(),
                    cliente.cpfCnpj()
            });
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tabelaClientes.setRowSorter(sorter);
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void onSelecionar() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tabelaClientes.convertRowIndexToModel(selectedRow);
        Long clienteId = (Long) tableModel.getValueAt(modelRow, 0);
        this.clienteSelecionado = listaClientes.stream()
                .filter(p -> p.id().equals(clienteId))
                .findFirst()
                .orElse(null);
        this.consumidorNaoIdentificado = false;
        dispose();
    }

    private void onConsumidorNaoIdentificado() {
        this.clienteSelecionado = null;
        this.consumidorNaoIdentificado = true;
        dispose();
    }

    private void onCancelar() {
        this.clienteSelecionado = null;
        this.consumidorNaoIdentificado = false;
        dispose();
    }

    public PessoaResponse getClienteSelecionado() {
        return clienteSelecionado;
    }

    public boolean isConsumidorNaoIdentificado() {
        return consumidorNaoIdentificado;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.PRIMARY); // Cor azul
        button.setForeground(ColorPalette.WHITE_TEXT); // Texto branco
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(ColorPalette.PRIMARY_DARK); // Cor azul mais escura ao passar o mouse
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(ColorPalette.PRIMARY); // Cor azul normal
            }
        });

        return button;
    }
}
