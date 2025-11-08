package br.com.ui.view;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.common.service.ApiServiceException;
import br.com.service.ProdutoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AbastecimentoDialog extends JDialog {

    private JComboBox<String> combustivelComboBox;
    private JTextField litrosTextField;
    private JTextField reaisTextField;
    private JComboBox<String> pagamentoComboBox;
    private ProdutoService produtoService;
    private List<ProdutoDTO> produtos;

    // Campos para guardar o resultado
    private ProdutoDTO produtoSelecionado;
    private double litrosAbastecidos;
    private double reaisAbastecidos;
    private String formaPagamento;
    private boolean confirmado = false;

    public AbastecimentoDialog(Frame owner, BombaDTO bomba) {
        super(owner, "Abastecer Bomba " + bomba.getNome(), true);
        this.produtoService = new ProdutoService();

        setSize(450, 350);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 2, 10, 10));
        getContentPane().setBackground(ColorPalette.BACKGROUND);

        // --- Componentes ---
        add(new JLabel("Combustível:"));
        combustivelComboBox = new JComboBox<>();
        add(combustivelComboBox);

        add(new JLabel("Litros:"));
        litrosTextField = new JTextField();
        add(litrosTextField);

        add(new JLabel("Reais (R$):"));
        reaisTextField = new JTextField();
        add(reaisTextField);

        add(new JLabel("Forma de Pagamento:"));
        pagamentoComboBox = new JComboBox<>(new String[]{"Dinheiro", "Pix", "Cartão de Crédito", "Cartão de Débito"});
        add(pagamentoComboBox);

        JButton okButton = new JButton("OK");
        add(new JLabel()); // Espaço em branco
        add(okButton);

        // --- Carregar Combustíveis ---
        carregarCombustiveis();

        // --- Listeners ---
        litrosTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
                    reaisTextField.setText("");
                }
            }
        });

        reaisTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
                    litrosTextField.setText("");
                }
            }
        });

        okButton.addActionListener(e -> {
            String itemSelecionado = (String) combustivelComboBox.getSelectedItem();
            if (itemSelecionado == null) return;

            String nomeCombustivel = itemSelecionado.split(" - ")[0];

            Optional<ProdutoDTO> produtoOpt = produtos.stream()
                    .filter(p -> p.getNome().equals(nomeCombustivel))
                    .findFirst();

            if (produtoOpt.isPresent()) {
                produtoSelecionado = produtoOpt.get();
                double precoUnitario = produtoSelecionado.getPrecos().get(0).getValor().doubleValue();

                try {
                    if (!litrosTextField.getText().isEmpty()) {
                        litrosAbastecidos = Double.parseDouble(litrosTextField.getText());
                        reaisAbastecidos = litrosAbastecidos * precoUnitario;
                    } else if (!reaisTextField.getText().isEmpty()) {
                        reaisAbastecidos = Double.parseDouble(reaisTextField.getText());
                        litrosAbastecidos = reaisAbastecidos / precoUnitario;
                    } else {
                        JOptionPane.showMessageDialog(this, "Preencha litros ou reais.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    formaPagamento = (String) pagamentoComboBox.getSelectedItem();
                    confirmado = true;
                    dispose();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valores inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void carregarCombustiveis() {
        SwingWorker<List<ProdutoDTO>, Void> worker = new SwingWorker<List<ProdutoDTO>, Void>() {
            @Override
            protected List<ProdutoDTO> doInBackground() throws Exception {
                return produtoService.buscarTodos();
            }

            @Override
            protected void done() {
                try {
                    produtos = get();
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

                    List<String> combustiveisFormatados = produtos.stream()
                            .filter(p -> p.getTipoProduto().equals("COMBUSTIVEL") && p.getPrecos() != null && !p.getPrecos().isEmpty())
                            .map(p -> String.format("%s - %s", p.getNome(), currencyFormat.format(p.getPrecos().get(0).getValor())))
                            .collect(Collectors.toList());

                    for (String combustivel : combustiveisFormatados) {
                        combustivelComboBox.addItem(combustivel);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof IOException || cause instanceof ApiServiceException) {
                        JOptionPane.showMessageDialog(AbastecimentoDialog.this, "Erro ao carregar combustíveis: " + cause.getMessage(), "Erro de API", JOptionPane.ERROR_MESSAGE);
                    } else {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(AbastecimentoDialog.this, "Ocorreu um erro inesperado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        worker.execute();
    }

    // --- Métodos públicos para obter o resultado ---
    public boolean isConfirmado() {
        return confirmado;
    }

    public ProdutoDTO getProdutoSelecionado() {
        return produtoSelecionado;
    }

    public double getLitrosAbastecidos() {
        return litrosAbastecidos;
    }

    public double getReaisAbastecidos() {
        return reaisAbastecidos;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }
}
