package br.com.ui.view;

import br.com.acesso.dto.AcessoRequest;
import br.com.acesso.dto.AcessoResponse;
import br.com.acesso.enums.TipoAcesso;
import br.com.acesso.service.AcessoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GerenciamentoAcessoScreen extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;
    private JComboBox<TipoAcesso> tipoAcessoComboBox;
    private JTable tabelaUsuarios;
    private DefaultTableModel tableModel;

    private final AcessoService acessoService;

    public GerenciamentoAcessoScreen() {
        this.acessoService = new AcessoService();

        setTitle("Gerenciamento de Acesso");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(10, 10));

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Dados do Usuário");
        titledBorder.setTitleColor(ColorPalette.TEXT);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titledBorder
        ));

        fieldsPanel.add(createStyledLabel("Login:"));
        loginField = createStyledTextField();
        fieldsPanel.add(loginField);

        fieldsPanel.add(createStyledLabel("Senha:"));
        passwordField = createStyledPasswordField();
        fieldsPanel.add(passwordField);

        fieldsPanel.add(createStyledLabel("Tipo de Acesso:"));
        tipoAcessoComboBox = new JComboBox<>(TipoAcesso.values());
        fieldsPanel.add(tipoAcessoComboBox);

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
        String[] colunas = {"ID", "Login", "Tipo de Acesso"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaUsuarios = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tabelaUsuarios);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarAcesso());
        excluirButton.addActionListener(e -> excluirAcesso());

        carregarAcessos();
    }

    private void carregarAcessos() {
        tableModel.setRowCount(0);
        try {
            List<AcessoResponse> acessos = acessoService.findAcessos();
            for (AcessoResponse acesso : acessos) {
                tableModel.addRow(new Object[]{acesso.id(), acesso.usuario(), acesso.tipoAcesso()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar acessos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void salvarAcesso() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());
        TipoAcesso tipoAcesso = (TipoAcesso) tipoAcessoComboBox.getSelectedItem();

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login e senha são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            AcessoRequest request = new AcessoRequest(login, password, tipoAcesso);
            acessoService.createAcesso(request);
            JOptionPane.showMessageDialog(this, "Acesso salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarAcessos();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar acesso: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void excluirAcesso() {
        int selectedRow = tabelaUsuarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um acesso para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaUsuarios.getValueAt(selectedRow, 0); // Assumindo que o ID está na primeira coluna

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o acesso selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                acessoService.deleteAcesso(id);
                JOptionPane.showMessageDialog(this, "Acesso excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarAcessos(); // Recarrega a lista da API
                limparCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir acesso: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void limparCampos() {
        loginField.setText("");
        passwordField.setText("");
        tipoAcessoComboBox.setSelectedIndex(0);
        tabelaUsuarios.clearSelection();
    }

    // Métodos de estilo (createStyledLabel, etc.) permanecem os mesmos
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ColorPalette.TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setCaretColor(ColorPalette.TEXT);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(ColorPalette.PANEL_BACKGROUND);
        passwordField.setForeground(ColorPalette.TEXT);
        passwordField.setCaretColor(ColorPalette.TEXT);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
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
            new GerenciamentoAcessoScreen().setVisible(true);
        });
    }
}
