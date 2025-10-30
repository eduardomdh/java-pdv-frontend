package br.com.ui.view;

import br.com.model.User;
import br.com.model.UserRepository;

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

        // --- Cores ---
        Color background = new Color(43, 43, 43);
        Color panelBackground = new Color(60, 63, 65);
        Color textColor = Color.WHITE;
        Color buttonBackground = new Color(255, 204, 0);
        Color buttonForeground = Color.BLACK;

        // --- Layout Principal ---
        Container contentPane = getContentPane();
        contentPane.setBackground(background);
        contentPane.setLayout(new BorderLayout(10, 10));

        // --- Painel de Campos ---
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBackground(background);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Dados do Usuário");
        titledBorder.setTitleColor(textColor);
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

        // --- Painel de Botões ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(background);
        JButton novoButton = createStyledButton("Novo");
        JButton salvarButton = createStyledButton("Salvar");
        JButton excluirButton = createStyledButton("Excluir");
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
        tabelaUsuarios.setBackground(panelBackground);
        tabelaUsuarios.setForeground(textColor);
        tabelaUsuarios.setGridColor(new Color(80, 80, 80));
        tabelaUsuarios.setSelectionBackground(buttonBackground);
        tabelaUsuarios.setSelectionForeground(buttonForeground);
        tabelaUsuarios.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaUsuarios.setRowHeight(25);

        JTableHeader tableHeader = tabelaUsuarios.getTableHeader();
        tableHeader.setBackground(new Color(80, 80, 80));
        tableHeader.setForeground(buttonBackground);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane tableScrollPane = new JScrollPane(tabelaUsuarios);
        tableScrollPane.getViewport().setBackground(background);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(panelBackground, 2));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(background);
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
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(new Color(60, 63, 65));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(new Color(60, 63, 65));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 204, 0));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0), 2));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }
}
