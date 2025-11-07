package br.com.ui.view;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.common.service.ApiServiceException;
import br.com.service.BombaService;
import br.com.service.ProdutoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AbastecimentoDialog extends JDialog {

    private JComboBox<String> combustivelComboBox;
    private JTextField litrosTextField;
    private JTextField reaisTextField;
    private ProdutoService produtoService;
    private BombaService bombaService;
    private List<ProdutoDTO> produtos;

    public AbastecimentoDialog(Frame owner, BombaDTO bomba) {
        super(owner, "Abastecer Bomba " + bomba.getNome(), true);
        this.produtoService = new ProdutoService();
        this.bombaService = new BombaService();

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(4, 2, 10, 10));
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
            String combustivelSelecionado = (String) combustivelComboBox.getSelectedItem();
            Optional<ProdutoDTO> produtoOpt = produtos.stream()
                    .filter(p -> p.getNome().equals(combustivelSelecionado))
                    .findFirst();

            if (produtoOpt.isPresent()) {
                ProdutoDTO produto = produtoOpt.get();
                double precoUnitario = produto.getPrecos().get(0).getValor().doubleValue(); // Assumindo que o preço está na primeira posição

                double litros = 0;
                double reais = 0;

                try {
                    if (!litrosTextField.getText().isEmpty()) {
                        litros = Double.parseDouble(litrosTextField.getText());
                        reais = litros * precoUnitario;
                    } else if (!reaisTextField.getText().isEmpty()) {
                        reais = Double.parseDouble(reaisTextField.getText());
                        litros = reais / precoUnitario;
                    } else {
                        JOptionPane.showMessageDialog(this, "Preencha litros ou reais.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    bombaService.atualizarStatus(bomba.getId(), "ATIVA");
                    dispose();
                    new AnimacaoAbastecimentoDialog((Frame) getOwner(), bomba, produto, litros, reais).setVisible(true);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valores inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (IOException | ApiServiceException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao se comunicar com a API: " + ex.getMessage(), "Erro de API", JOptionPane.ERROR_MESSAGE);
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
                    List<String> combustiveis = produtos.stream()
                            .filter(p -> p.getTipoProduto().equals("COMBUSTIVEL"))
                            .map(ProdutoDTO::getNome)
                            .collect(Collectors.toList());

                    for (String combustivel : combustiveis) {
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
}
