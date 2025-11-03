package br.com.ui.view;

import br.com.model.User;
import br.com.model.UserRepository;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class GerenciamentoAcessoScreen extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;
    private JComboBox<String> tipoAcessoComboBox;
    private JTable tabelaUsuarios;
    private DefaultTableModel tableModel;
    private final UserRepository userRepository;
    private Long selectedUserId = null;

    public GerenciamentoAcessoScreen() {
        this.userRepository = UserRepository.getInstance();
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
        tipoAcessoComboBox = new JComboBox<>(new String[]{"funcionario", "gerente", "administrador"});
        fieldsPanel.add(tipoAcessoComboBox);
        tipoAcessoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(ColorPalette.PRIMARY);
                    setForeground(ColorPalette.WHITE_TEXT);
                } else {
                    setBackground(ColorPalette.PANEL_BACKGROUND);
                    setForeground(ColorPalette.TEXT);
                }
                return this;
            }
        });

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
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        tabelaUsuarios = new JTable(tableModel);

        // Estilo da Tabela
        tabelaUsuarios.setBackground(ColorPalette.PANEL_BACKGROUND);
        tabelaUsuarios.setForeground(ColorPalette.TEXT);
        tabelaUsuarios.setGridColor(new Color(200, 200, 200));
        tabelaUsuarios.setSelectionBackground(ColorPalette.PRIMARY);
        tabelaUsuarios.setSelectionForeground(ColorPalette.WHITE_TEXT);
        tabelaUsuarios.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaUsuarios.setRowHeight(25);

        JTableHeader tableHeader = tabelaUsuarios.getTableHeader();
        tableHeader.setBackground(ColorPalette.PRIMARY);
        tableHeader.setForeground(ColorPalette.WHITE_TEXT);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaUsuarios);
        tableScrollPane.getViewport().setBackground(ColorPalette.BACKGROUND);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- Ações ---
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarUsuario());
        excluirButton.addActionListener(e -> excluirUsuario());

        tabelaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaUsuarios.getSelectedRow();
                if (selectedRow != -1) {
                    selectedUserId = (Long) tableModel.getValueAt(selectedRow, 0);
                    Optional<User> userOpt = userRepository.findById(selectedUserId);
                    userOpt.ifPresent(user -> {
                        loginField.setText(user.getLogin());
                        passwordField.setText(user.getPassword());
                        tipoAcessoComboBox.setSelectedItem(user.getTipoAcesso());
                    });
                }
            }
        });

        carregarUsuarios();
    }

    private void carregarUsuarios() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<User> users = userRepository.getAllUsers();
        for (User user : users) {
            tableModel.addRow(new Object[]{user.getId(), user.getLogin(), user.getTipoAcesso()});
        }
    }

    private void salvarUsuario() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());
        String tipoAcesso = (String) tipoAcessoComboBox.getSelectedItem();

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login e senha são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedUserId == null) { // Create
            User newUser = new User(null, login, password, tipoAcesso);
            userRepository.addUser(newUser);
        } else { // Update
            User updatedUser = new User(selectedUserId, login, password, tipoAcesso);
            userRepository.updateUser(updatedUser);
        }
        carregarUsuarios();
        limparCampos();
    }

    private void excluirUsuario() {
        if (selectedUserId == null) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o usuário selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userRepository.removeUser(selectedUserId);
            carregarUsuarios();
            limparCampos();
        }
    }

    private void limparCampos() {
        selectedUserId = null;
        loginField.setText("");
        passwordField.setText("");
        tipoAcessoComboBox.setSelectedIndex(0);
        tabelaUsuarios.clearSelection();
    }

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
}
