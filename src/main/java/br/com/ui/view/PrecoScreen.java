package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.preco.dto.PrecoRequest;
import br.com.preco.dto.PrecoResponse;
import br.com.preco.service.PrecoService;
import br.com.produto.dto.ProdutoResponse;
import br.com.produto.service.ProdutoService;
import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrecoScreen extends JFrame {

    private JTextField valorField, dataAlteracaoField;
    private JTextField produtoField;
    private JButton selecionarProdutoButton;
    private ProdutoResponse produtoSelecionado;
    private JTable tabelaPrecos;
    private DefaultTableModel tableModel;
    private Long precoIdEmEdicao;

    private final PrecoService precoService;
    private final ProdutoService produtoService;
    private Map<Long, ProdutoResponse> produtosMap;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PrecoScreen() {
        this.precoService = new PrecoService();
        this.produtoService = new ProdutoService();
        this.produtosMap = new HashMap<>();

        setTitle("Gerenciamento de Preços");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados de Preço", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Produto:", ColorPalette.TEXT));
        JPanel produtoPanel = new JPanel(new BorderLayout());
        produtoField = createStyledTextField();
        produtoField.setEditable(false);
        selecionarProdutoButton = new JButton("Selecionar");
        produtoPanel.add(produtoField, BorderLayout.CENTER);
        produtoPanel.add(selecionarProdutoButton, BorderLayout.EAST);
        fieldsPanel.add(produtoPanel);

        fieldsPanel.add(createStyledLabel("Valor (R$):", ColorPalette.TEXT));
        valorField = createStyledTextField();
        fieldsPanel.add(valorField);
        fieldsPanel.add(createStyledLabel("Data Alteração (dd/MM/yyyy):", ColorPalette.TEXT));
        dataAlteracaoField = createStyledTextField();
        fieldsPanel.add(dataAlteracaoField);

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

        String[] colunas = {"ID", "Produto", "Valor", "Data de Alteração"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPrecos = new JTable(tableModel);
        tabelaPrecos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPrecos);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        selecionarProdutoButton.addActionListener(e -> abrirSelecaoProduto());
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarPreco());
        excluirButton.addActionListener(e -> excluirPreco());
        editarButton.addActionListener(e -> editarPreco());

        carregarMapaProdutos();
        carregarPrecos();
    }

    private void abrirSelecaoProduto() {
        SelecaoProdutoScreen selecaoProdutoScreen = new SelecaoProdutoScreen(this);
        selecaoProdutoScreen.setVisible(true);
        ProdutoResponse produto = selecaoProdutoScreen.getProdutoSelecionado();
        if (produto != null) {
            this.produtoSelecionado = produto;
            produtoField.setText(produto.nome());
        }
    }

    private void carregarMapaProdutos() {
        try {
            List<ProdutoResponse> produtos = produtoService.findProducts();
            produtosMap.clear();
            for (ProdutoResponse produto : produtos) {
                produtosMap.put(produto.id(), produto);
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar os dados dos produtos: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para buscar os produtos. Verifique sua conexão.");
        }
    }

    private void carregarPrecos() {
        tableModel.setRowCount(0);
        try {
            List<PrecoResponse> precos = precoService.findPrecos();
            for (PrecoResponse preco : precos) {
                ProdutoResponse produtoAssociado = produtosMap.get(preco.produtoId());
                String nomeProduto = (produtoAssociado != null) ? produtoAssociado.nome() : "Produto Desconhecido";
                tableModel.addRow(new Object[]{
                        preco.id(),
                        nomeProduto,
                        String.format("%.2f", preco.valor()),
                        preco.dataAlteracao().format(dateFormatter)
                });
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar os preços: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para buscar os preços. Verifique sua conexão.");
        }
    }

    private void salvarPreco() {
        try {
            if (produtoSelecionado == null) {
                showErrorDialog("Validação", "É necessário selecionar um produto.");
                return;
            }
            BigDecimal valor = new BigDecimal(valorField.getText().replace(",", "."));
            LocalDate dataAlteracao = LocalDate.parse(dataAlteracaoField.getText(), dateFormatter);

            PrecoRequest request = new PrecoRequest(
                    valor,
                    dataAlteracao,
                    LocalDate.now(),
                    produtoSelecionado.id()
            );

            if (precoIdEmEdicao == null) {
                precoService.createPreco(request);
                JOptionPane.showMessageDialog(this, "Preço salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                precoService.updatePreco(precoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Preço atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarPrecos();
            limparCampos();

        } catch (DateTimeParseException ex) {
            showErrorDialog("Erro de Formato", "Formato de data inválido. Use dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            showErrorDialog("Erro de Formato", "Formato de valor inválido. Use vírgula (,) como separador decimal.");
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível salvar o preço: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para salvar o preço. Verifique sua conexão.");
        }
    }

    private void editarPreco() {
        int selectedRow = tabelaPrecos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um preço para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        precoIdEmEdicao = (Long) tableModel.getValueAt(selectedRow, 0);
        
        try {
            PrecoResponse precoParaEditar = precoService.findPrecoById(precoIdEmEdicao);
            if (precoParaEditar != null) {
                valorField.setText(String.valueOf(precoParaEditar.valor()).replace('.', ','));
                dataAlteracaoField.setText(precoParaEditar.dataAlteracao().format(dateFormatter));

                ProdutoResponse produtoAssociado = produtosMap.get(precoParaEditar.produtoId());
                if (produtoAssociado != null) {
                    this.produtoSelecionado = produtoAssociado;
                    produtoField.setText(produtoAssociado.nome());
                } else {
                    this.produtoSelecionado = null;
                    produtoField.setText("Produto não encontrado");
                }
                JOptionPane.showMessageDialog(this, "Campos preenchidos para edição. Altere os dados e clique em Salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar os dados do preço para edição: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para buscar os dados do preço. Verifique sua conexão.");
        }
    }

    private void excluirPreco() {
        int selectedRow = tabelaPrecos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um preço para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o preço selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Funcionalidade de exclusão de preço ainda não disponível.", "Não Implementado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparCampos() {
        valorField.setText("");
        dataAlteracaoField.setText("");
        produtoField.setText("");
        produtoSelecionado = null;
        tabelaPrecos.clearSelection();
        precoIdEmEdicao = null;
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
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new PrecoScreen().setVisible(true);
        });
    }
}
