package br.com.ui.view;

import br.com.preco.dto.PrecoRequest;
import br.com.preco.dto.PrecoResponse;
import br.com.preco.service.PrecoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PrecoScreen extends JFrame {

    private JTextField valorField, dataAlteracaoField;
    private JTable tabelaPrecos;
    private DefaultTableModel tableModel;
    private Long precoIdEmEdicao; // Novo campo para armazenar o ID do preço em edição

    private final PrecoService precoService;

    public PrecoScreen() {
        this.precoService = new PrecoService();

        setTitle("Gerenciamento de Preços");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados de Preço", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Valor (ex: 5.89):", ColorPalette.TEXT));
        valorField = createStyledTextField();
        fieldsPanel.add(valorField);
        fieldsPanel.add(createStyledLabel("Data Alteração (yyyy-mm-dd):", ColorPalette.TEXT));
        dataAlteracaoField = createStyledTextField();
        fieldsPanel.add(dataAlteracaoField);

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
        String[] colunas = {"ID", "Valor", "Data de Alteração"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaPrecos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaPrecos);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarPreco());
        excluirButton.addActionListener(e -> excluirPreco());
        editarButton.addActionListener(e -> editarPreco()); // Ação para o botão Editar

        carregarPrecos();
    }

    private void carregarPrecos() {
        tableModel.setRowCount(0);
        try {
            List<PrecoResponse> precos = precoService.findPrecos();
            for (PrecoResponse preco : precos) {
                tableModel.addRow(new Object[]{
                        preco.id(),
                        preco.valor(),
                        preco.dataAlteracao()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar preços: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void salvarPreco() {
        try {
            BigDecimal valor = new BigDecimal(valorField.getText());
            LocalDate dataAlteracao = LocalDate.parse(dataAlteracaoField.getText());

            PrecoRequest request = new PrecoRequest(
                    valor,
                    dataAlteracao,
                    LocalDate.now()
            );

            if (precoIdEmEdicao == null) { // Se não há ID em edição, é um novo preço
                precoService.createPreco(request);
                JOptionPane.showMessageDialog(this, "Preço salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Se há um ID em edição, é uma atualização
                precoService.updatePreco(precoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Preço atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarPrecos();
            limparCampos();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-mm-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de valor inválido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar preço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editarPreco() {
        int selectedRow = tabelaPrecos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um preço para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        precoIdEmEdicao = (Long) tabelaPrecos.getValueAt(selectedRow, 0); // Pega o ID da primeira coluna
        valorField.setText(tabelaPrecos.getValueAt(selectedRow, 1).toString());
        dataAlteracaoField.setText(tabelaPrecos.getValueAt(selectedRow, 2).toString());

        JOptionPane.showMessageDialog(this, "Campos preenchidos para edição. Altere os dados e clique em Salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirPreco() {
        int selectedRow = tabelaPrecos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um preço para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaPrecos.getValueAt(selectedRow, 0); // Assumindo que o ID está na primeira coluna

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o preço selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // PrecoService não tem delete, então vou simular ou adicionar se necessário
                // precoService.deletePreco(id);
                JOptionPane.showMessageDialog(this, "Funcionalidade de exclusão ainda não implementada no backend para Preço.", "Aviso", JOptionPane.WARNING_MESSAGE);
                carregarPrecos(); // Recarrega a lista da API
                limparCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir preço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void limparCampos() {
        valorField.setText("");
        dataAlteracaoField.setText("");
        tabelaPrecos.clearSelection();
        precoIdEmEdicao = null; // Limpa o ID em edição
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
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PrecoScreen().setVisible(true);
        });
    }
}
