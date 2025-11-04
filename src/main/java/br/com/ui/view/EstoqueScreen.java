package br.com.ui.view;

import br.com.estoque.dto.EstoqueRequest;
import br.com.estoque.dto.EstoqueResponse;
import br.com.estoque.enums.TipoEstoque;
import br.com.estoque.service.EstoqueService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EstoqueScreen extends JFrame {

    private JTextField quantidadeField, localTanqueField, localEnderecoField, loteFabricacaoField, dataValidadeField;
    private JComboBox<TipoEstoque> tipoEstoqueComboBox;
    private JTable tabelaEstoque;
    private DefaultTableModel tableModel;

    private final EstoqueService estoqueService;

    public EstoqueScreen() {
        this.estoqueService = new EstoqueService();

        setTitle("Gerenciamento de Estoque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Estoque", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Quantidade:", ColorPalette.TEXT));
        quantidadeField = createStyledTextField();
        fieldsPanel.add(quantidadeField);

        fieldsPanel.add(createStyledLabel("Local do Tanque:", ColorPalette.TEXT));
        localTanqueField = createStyledTextField();
        fieldsPanel.add(localTanqueField);

        fieldsPanel.add(createStyledLabel("Endereço do Local:", ColorPalette.TEXT));
        localEnderecoField = createStyledTextField();
        fieldsPanel.add(localEnderecoField);

        fieldsPanel.add(createStyledLabel("Lote de Fabricação:", ColorPalette.TEXT));
        loteFabricacaoField = createStyledTextField();
        fieldsPanel.add(loteFabricacaoField);

        fieldsPanel.add(createStyledLabel("Data de Validade (yyyy-mm-dd):", ColorPalette.TEXT));
        dataValidadeField = createStyledTextField();
        fieldsPanel.add(dataValidadeField);

        fieldsPanel.add(createStyledLabel("Tipo de Estoque:", ColorPalette.TEXT));
        tipoEstoqueComboBox = new JComboBox<>(TipoEstoque.values());
        fieldsPanel.add(tipoEstoqueComboBox);

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
        String[] colunas = {"ID", "Quantidade", "Tanque", "Endereço", "Lote", "Validade", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaEstoque = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaEstoque);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Ações
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarEstoque());
        excluirButton.addActionListener(e -> excluirEstoque());

        carregarEstoques();
    }

    private void carregarEstoques() {
        tableModel.setRowCount(0);
        try {
            List<EstoqueResponse> estoques = estoqueService.findEstoques();
            for (EstoqueResponse estoque : estoques) {
                tableModel.addRow(new Object[]{
                        estoque.id(),
                        estoque.quantidade(),
                        estoque.localTanque(),
                        estoque.localEndereco(),
                        estoque.loteFabricacao(),
                        estoque.dataValidade(),
                        estoque.tipoEstoque()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void salvarEstoque() {
        try {
            BigDecimal quantidade = new BigDecimal(quantidadeField.getText());
            LocalDate dataValidade = LocalDate.parse(dataValidadeField.getText());

            EstoqueRequest request = new EstoqueRequest(
                    quantidade,
                    localTanqueField.getText(),
                    localEnderecoField.getText(),
                    loteFabricacaoField.getText(),
                    dataValidade,
                    (TipoEstoque) tipoEstoqueComboBox.getSelectedItem()
            );

            estoqueService.createEstoque(request);
            JOptionPane.showMessageDialog(this, "Estoque salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarEstoques();
            limparCampos();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-mm-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de quantidade inválido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void excluirEstoque() {
        int selectedRow = tabelaEstoque.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um estoque para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0); // Assumindo que o ID está na primeira coluna

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o estoque selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                estoqueService.deleteEstoque(id);
                JOptionPane.showMessageDialog(this, "Estoque excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarEstoques(); // Recarrega a lista da API
                limparCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void limparCampos() {
        quantidadeField.setText("");
        localTanqueField.setText("");
        localEnderecoField.setText("");
        loteFabricacaoField.setText("");
        dataValidadeField.setText("");
        tipoEstoqueComboBox.setSelectedIndex(0);
        tabelaEstoque.clearSelection();
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
            new EstoqueScreen().setVisible(true);
        });
    }
}
