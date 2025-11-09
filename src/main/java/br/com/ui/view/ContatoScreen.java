package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.contato.dto.ContatoRequest;
import br.com.contato.dto.ContatoResponse;
import br.com.contato.enums.TipoContato;
import br.com.contato.service.ContatoService;
import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.service.PessoaService;
import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContatoScreen extends JFrame {

    private JTextField telefoneField, emailField, enderecoField;
    private JComboBox<TipoContato> tipoContatoComboBox;
    private JTextField pessoaField;
    private JButton selecionarPessoaButton;
    private PessoaResponse pessoaSelecionada;
    private JTable tabelaContatos;
    private DefaultTableModel tableModel;
    private Long contatoIdEmEdicao;

    private final ContatoService contatoService;
    private final PessoaService pessoaService;
    private Map<Long, PessoaResponse> pessoasMap;

    public ContatoScreen() {
        this.contatoService = new ContatoService();
        this.pessoaService = new PessoaService();
        this.pessoasMap = new HashMap<>();

        setTitle("Gerenciamento de Contatos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Dados do Contato", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 16), ColorPalette.PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fieldsPanel.add(createStyledLabel("Pessoa:", ColorPalette.TEXT));
        JPanel pessoaPanel = new JPanel(new BorderLayout());
        pessoaField = createStyledTextField();
        pessoaField.setEditable(false);
        selecionarPessoaButton = new JButton("Selecionar");
        pessoaPanel.add(pessoaField, BorderLayout.CENTER);
        pessoaPanel.add(selecionarPessoaButton, BorderLayout.EAST);
        fieldsPanel.add(pessoaPanel);

        fieldsPanel.add(createStyledLabel("Telefone:", ColorPalette.TEXT));
        telefoneField = createStyledTextField();
        fieldsPanel.add(telefoneField);

        fieldsPanel.add(createStyledLabel("Email:", ColorPalette.TEXT));
        emailField = createStyledTextField();
        fieldsPanel.add(emailField);

        fieldsPanel.add(createStyledLabel("Endereço:", ColorPalette.TEXT));
        enderecoField = createStyledTextField();
        fieldsPanel.add(enderecoField);

        fieldsPanel.add(createStyledLabel("Tipo de Contato:", ColorPalette.TEXT));
        tipoContatoComboBox = new JComboBox<>(TipoContato.values());
        fieldsPanel.add(tipoContatoComboBox);

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

        String[] colunas = {"ID", "Pessoa", "Telefone", "Email", "Endereço", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaContatos = new JTable(tableModel);
        tabelaContatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tabelaContatos);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        selecionarPessoaButton.addActionListener(e -> abrirSelecaoPessoa());
        novoButton.addActionListener(e -> limparCampos());
        salvarButton.addActionListener(e -> salvarContato());
        excluirButton.addActionListener(e -> excluirContato());
        editarButton.addActionListener(e -> editarContato());

        carregarMapaPessoas();
        carregarContatos();
    }

    private void abrirSelecaoPessoa() {
        SelecaoPessoaScreen selecaoPessoaScreen = new SelecaoPessoaScreen(this);
        selecaoPessoaScreen.setVisible(true);
        PessoaResponse pessoa = selecaoPessoaScreen.getPessoaSelecionada();
        if (pessoa != null) {
            this.pessoaSelecionada = pessoa;
            pessoaField.setText(pessoa.nomeCompleto());
        }
    }

    private void carregarMapaPessoas() {
        try {
            List<PessoaResponse> pessoas = pessoaService.findPessoas();
            pessoasMap.clear();
            for (PessoaResponse pessoa : pessoas) {
                pessoasMap.put(pessoa.id(), pessoa);
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar os dados das pessoas: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para buscar as pessoas. Verifique sua conexão.");
        }
    }

    private void carregarContatos() {
        tableModel.setRowCount(0);
        try {
            List<ContatoResponse> contatos = contatoService.findContatos();
            for (ContatoResponse contato : contatos) {
                PessoaResponse pessoaAssociada = pessoasMap.get(contato.pessoaId());
                String nomePessoa = (pessoaAssociada != null) ? pessoaAssociada.nomeCompleto() : "Pessoa Desconhecida";
                tableModel.addRow(new Object[]{
                        contato.id(),
                        nomePessoa,
                        contato.telefone(),
                        contato.email(),
                        contato.endereco(),
                        contato.tipoContato()
                });
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar os contatos: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para buscar os contatos. Verifique sua conexão.");
        }
    }

    private void salvarContato() {
        try {
            if (pessoaSelecionada == null) {
                showErrorDialog("Validação", "É necessário selecionar uma pessoa.");
                return;
            }

            ContatoRequest request = new ContatoRequest(
                    telefoneField.getText(),
                    emailField.getText(),
                    enderecoField.getText(),
                    (TipoContato) tipoContatoComboBox.getSelectedItem(),
                    pessoaSelecionada.id()
            );

            if (contatoIdEmEdicao == null) {
                contatoService.createContato(request);
                JOptionPane.showMessageDialog(this, "Contato salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                contatoService.updateContato(contatoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Contato atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarContatos();
            limparCampos();

        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível salvar o contato: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para salvar o contato. Verifique sua conexão.");
        }
    }

    private void editarContato() {
        int selectedRow = tabelaContatos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contato para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        contatoIdEmEdicao = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            ContatoResponse contatoParaEditar = contatoService.findContatoById(contatoIdEmEdicao);
            if (contatoParaEditar != null) {
                telefoneField.setText(contatoParaEditar.telefone());
                emailField.setText(contatoParaEditar.email());
                enderecoField.setText(contatoParaEditar.endereco());
                tipoContatoComboBox.setSelectedItem(contatoParaEditar.tipoContato());

                PessoaResponse pessoaAssociada = pessoasMap.get(contatoParaEditar.pessoaId());
                if (pessoaAssociada != null) {
                    this.pessoaSelecionada = pessoaAssociada;
                    pessoaField.setText(pessoaAssociada.nomeCompleto());
                } else {
                    this.pessoaSelecionada = null;
                    pessoaField.setText("Pessoa não encontrada");
                }
                JOptionPane.showMessageDialog(this, "Campos preenchidos para edição. Altere os dados e clique em Salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ApiServiceException e) {
            showErrorDialog("Erro de API", "Não foi possível carregar os dados do contato para edição: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para buscar os dados do contato. Verifique sua conexão.");
        }
    }

    private void excluirContato() {
        int selectedRow = tabelaContatos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contato para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabelaContatos.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o contato selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                contatoService.deleteContato(id);
                JOptionPane.showMessageDialog(this, "Contato excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarContatos();
                limparCampos();
            } catch (ApiServiceException e) {
                showErrorDialog("Erro de API", "Não foi possível excluir o contato: " + e.getMessage());
            } catch (IOException e) {
                showErrorDialog("Erro de Conexão", "Não foi possível conectar ao servidor para excluir o contato. Verifique sua conexão.");
            }
        }
    }

    private void limparCampos() {
        telefoneField.setText("");
        emailField.setText("");
        enderecoField.setText("");
        tipoContatoComboBox.setSelectedIndex(0);
        pessoaField.setText("");
        pessoaSelecionada = null;
        tabelaContatos.clearSelection();
        contatoIdEmEdicao = null;
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
            new ContatoScreen().setVisible(true);
        });
    }
}
