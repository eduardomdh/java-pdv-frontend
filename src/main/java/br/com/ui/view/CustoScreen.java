package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.custo.dto.CustoRequest;
import br.com.custo.dto.CustoResponse;
import br.com.custo.enums.TipoCusto;
import br.com.custo.service.CustoService;
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

public class CustoScreen extends JFrame {

    private JTextField impostoField, custoVariavelField, custoFixoField, margemLucroField, dataProcessamentoField;
    private JComboBox<TipoCusto> tipoCustoComboBox;
    private JTable tabelaCustos;
    private DefaultTableModel tableModel;
    private Long custoIdEmEdicao;

    private final CustoService custoService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CustoScreen() {
        this.custoService = new CustoService();

        setTitle("Gerenciamento de Custos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Custo", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Imposto (%):", ColorPalette.TEXT));
        impostoField = createStyledTextField();
        fieldsPanel.add(impostoField);

        fieldsPanel.add(createStyledLabel("Custo Variável (R$):", ColorPalette.TEXT));
        custoVariavelField = createStyledTextField();
        fieldsPanel.add(custoVariavelField);

        fieldsPanel.add(createStyledLabel("Custo Fixo (R$):", ColorPalette.TEXT));
        custoFixoField = createStyledTextField();
        fieldsPanel.add(custoFixoField);

        fieldsPanel.add(createStyledLabel("Margem de Lucro (%):", ColorPalette.TEXT));
        margemLucroField = createStyledTextField();
        fieldsPanel.add(margemLucroField);

        fieldsPanel.add(createStyledLabel("Data Processamento (dd/MM/yyyy):", ColorPalette.TEXT));
        dataProcessamentoField = createStyledTextField();
        fieldsPanel.add(dataProcessamentoField);

        fieldsPanel.add(createStyledLabel("Tipo de Custo:", ColorPalette.TEXT));
        tipoCustoComboBox = new JComboBox<>(TipoCusto.values());
        fieldsPanel.add(tipoCustoComboBox);

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

        String[] colunas = {"ID", "Imposto", "C. Variável", "C. Fixo", "M. Lucro", "Data", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaCustos = new JTable(tableModel);
        tabelaCustos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tabelaCustos);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarCusto());
        excluirButton.addActionListener(e -> excluirCusto());
        editarButton.addActionListener(e -> editarCusto());

        carregarCustos();
    }

    private void carregarCustos() {
        tableModel.setRowCount(0);
        try {
            List<CustoResponse> custos = custoService.findCustos();
            for (CustoResponse custo : custos) {
                tableModel.addRow(new Object[]{
                        custo.id(),
                        String.format("%.2f", custo.imposto()),
                        String.format("%.2f", custo.custoVariavel()),
                        String.format("%.2f", custo.custoFixo()),
                        String.format("%.2f", custo.margemLucro()),
                        custo.dataProcessamento().format(dateFormatter),
                        custo.tipoCusto()
                });
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar os custos: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para buscar os custos. Verifique sua conexão.");
        }
    }

    private void salvarCusto() {
        try {
            double imposto = Double.parseDouble(impostoField.getText().replace(",", "."));
            double custoVariavel = Double.parseDouble(custoVariavelField.getText().replace(",", "."));
            double custoFixo = Double.parseDouble(custoFixoField.getText().replace(",", "."));
            double margemLucro = Double.parseDouble(margemLucroField.getText().replace(",", "."));
            LocalDate dataProcessamento = LocalDate.parse(dataProcessamentoField.getText(), dateFormatter);

            CustoRequest request = new CustoRequest(
                    imposto,
                    custoVariavel,
                    custoFixo,
                    margemLucro,
                    dataProcessamento,
                    (TipoCusto) tipoCustoComboBox.getSelectedItem()
            );

            if (custoIdEmEdicao == null) {
                custoService.createCusto(request);
                JOptionPane.showMessageDialog(this, "Custo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                custoService.updateCusto(custoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Custo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarCustos();
            limparCampos();

        } catch (DateTimeParseException ex) {
            showErrorDialog("Erro de Formato", "Formato de data inválido. Use dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            showErrorDialog("Erro de Formato", "Formato de número inválido para um dos campos de custo. Use vírgula (,) como separador decimal.");
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível salvar o custo: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para salvar o custo. Verifique sua conexão.");
        }
    }

    private void editarCusto() {
        int selectedRow = tabelaCustos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um custo para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        custoIdEmEdicao = (Long) tabelaCustos.getValueAt(selectedRow, 0);
        impostoField.setText(tabelaCustos.getValueAt(selectedRow, 1).toString().replace('.', ','));
        custoVariavelField.setText(tabelaCustos.getValueAt(selectedRow, 2).toString().replace('.', ','));
        custoFixoField.setText(tabelaCustos.getValueAt(selectedRow, 3).toString().replace('.', ','));
        margemLucroField.setText(tabelaCustos.getValueAt(selectedRow, 4).toString().replace('.', ','));
        dataProcessamentoField.setText(tabelaCustos.getValueAt(selectedRow, 5).toString());
        tipoCustoComboBox.setSelectedItem(tabelaCustos.getValueAt(selectedRow, 6));

        JOptionPane.showMessageDialog(this, "Campos preenchidos para edição. Altere os dados e clique em Salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirCusto() {
        int selectedRow = tabelaCustos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um custo para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaCustos.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o custo selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                custoService.deleteCusto(id);
                JOptionPane.showMessageDialog(this, "Custo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarCustos();
                limparCampos();
            } catch (ApiServiceException e) {
                showErrorDialog("Erro de API", "Não foi possível excluir o custo: " + e.getMessage());
            } catch (IOException e) {
                showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para excluir o custo. Verifique sua conexão.");
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
        custoIdEmEdicao = null;
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
            new CustoScreen().setVisible(true);
        });
    }
}
