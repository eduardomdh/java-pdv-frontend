package br.com.ui.view;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.common.service.ApiServiceException;
import br.com.service.BombaService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AnimacaoAbastecimentoDialog extends JDialog {

    private JProgressBar progressBar;
    private JLabel litrosLabel;
    private JLabel reaisLabel;
    private BombaService bombaService;

    public AnimacaoAbastecimentoDialog(Frame owner, BombaDTO bomba, ProdutoDTO produto, double litros, double reais) {
        super(owner, "Abastecendo Bomba " + bomba.getNome(), true);
        this.bombaService = new BombaService();

        final BombaDTO finalBomba = bomba;
        final double finalLitros = litros;
        final double finalReais = reais;
        final JDialog self = this;

        setSize(400, 200);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(3, 1, 10, 10));
        getContentPane().setBackground(ColorPalette.BACKGROUND);

        litrosLabel = new JLabel("Litros: 0.00");
        reaisLabel = new JLabel("Reais: R$ 0.00");
        progressBar = new JProgressBar(0, 100);

        add(litrosLabel);
        add(reaisLabel);
        add(progressBar);

        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(50);
                    double litrosAtuais = (finalLitros * i) / 100;
                    double reaisAtuais = (finalReais * i) / 100;
                    final int progressValue = i;

                    SwingUtilities.invokeLater(() -> {
                        litrosLabel.setText(String.format("Litros: %.2f", litrosAtuais));
                        reaisLabel.setText(String.format("Reais: R$ %.2f", reaisAtuais));
                        progressBar.setValue(progressValue);
                    });
                }

                bombaService.atualizarStatus(finalBomba.getId(), "CONCLUIDA");

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(self, "Abastecimento concluído!");
                    self.dispose();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException | ApiServiceException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(self, "Erro ao atualizar status da bomba: " + e.getMessage(), "Erro de API", JOptionPane.ERROR_MESSAGE);
                    self.dispose();
                });
            }
        }).start();
    }
}
