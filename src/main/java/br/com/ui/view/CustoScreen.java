package br.com.ui.view;

import br.com.custo.dto.CustoRequest;
import br.com.custo.dto.CustoResponse;
import br.com.custo.enums.TipoCusto;
import br.com.custo.service.CustoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CustoScreen extends JFrame {

    private JTextField impostoField, custoVariavelField, custoFixoField, margemLucroField, dataProcessamentoField;
    private JComboBox<TipoCusto> tipoCustoComboBox;
    private JTable tabelaCustos;
    private DefaultTableModel tableModel;

    private final CustoService custoService;

    public CustoScreen() {
        this.custoService = new CustoService();

        setTitle("Gerenciamento de Custos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Custo", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Imposto:", ColorPalette.TEXT));
        impostoField = createStyledTextField();
        fieldsPanel.add(impostoField);

        fieldsPanel.add(createStyledLabel("Custo Variável:", ColorPalette.TEXT));
        custoVariavelField = createStyledTextField();
        fieldsPanel.add(custoVariavelField);

        fieldsPanel.add(createStyledLabel("Custo Fixo:", ColorPalette.TEXT));
        custoFixoField = createStyledTextField();
        fieldsPanel.add(custoFixoField);

        fieldsPanel.add(createStyledLabel("Margem de Lucro:", ColorPalette.TEXT));
        margemLucroField = createStyledTextField();
        fieldsPanel.add(margemLucroField);

        fieldsPanel.add(createStyledLabel("Data Processamento (yyyy-mm-dd):", ColorPalette.TEXT));
        dataProcessamentoField = createStyledTextField();
        fieldsPanel.add(dataProcessamentoField);

        fieldsPanel.add(createStyledLabel("Tipo de Custo:", ColorPalette.TEXT));
        tipoCustoComboBox = new JComboBox<>(TipoCusto.values());
        fieldsPanel.add(tipoCustoComboBox);

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
        String[] colunas = {"ID", "Imposto", "C. Variável", "C. Fixo", "M. Lucro", "Data", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaCustos = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaCustos);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Ações
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarCusto());
        excluirButton.addActionListener(e -> excluirCusto());

        carregarCustos();
    }

    private void carregarCustos() {
        tableModel.setRowCount(0);
        try {
            List<CustoResponse> custos = custoService.findCustos();
            for (CustoResponse custo : custos) {
                tableModel.addRow(new Object[]{
                        custo.id(),
                        custo.imposto(),
                        custo.custoVariavel(),
                        custo.custoFixo(),
                        custo.margemLucro(),
                        custo.dataProcessamento(),
                        custo.tipoCusto()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar custos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void salvarCusto() {
        try {
            double imposto = Double.parseDouble(impostoField.getText());
            double custoVariavel = Double.parseDouble(custoVariavelField.getText());
            double custoFixo = Double.parseDouble(custoFixoField.getText());
            double margemLucro = Double.parseDouble(margemLucroField.getText());
            LocalDate dataProcessamento = LocalDate.parse(dataProcessamentoField.getText());

            CustoRequest request = new CustoRequest(
                    imposto,
                    custoVariavel,
                    custoFixo,
                    margemLucro,
                    dataProcessamento,
                    (TipoCusto) tipoCustoComboBox.getSelectedItem()
            );

            custoService.createCusto(request);
            JOptionPane.showMessageDialog(this, "Custo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarCustos();
            limparCampos();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-mm-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de número inválido para um dos campos de custo.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar custo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void excluirCusto() {
        int selectedRow = tabelaCustos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um custo para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaCustos.getValueAt(selectedRow, 0); // Assumindo que o ID está na primeira coluna

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o custo selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                custoService.deleteCusto(id);
                JOptionPane.showMessageDialog(this, "Custo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarCustos(); // Recarrega a lista da API
                limparCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir custo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void limparCampos() {
        impostoField.setText("");
        custoVariavelField.setText("");
        custoFixoField.setText("");
        margemLucroField.setText("");
        dataProcessamentoField.setText("");
        tipoCustoComboBox.setSelectedIndex(0);
        tabelaCustos.clearSelection();
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
            new CustoScreen().setVisible(true);
        });
    }
}
