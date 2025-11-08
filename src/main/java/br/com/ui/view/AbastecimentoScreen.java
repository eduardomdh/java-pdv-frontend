package br.com.ui.view;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.common.service.ApiServiceException;
import br.com.pessoa.dto.PessoaResponse;
import br.com.service.BombaService;
import br.com.service.PdfService;
import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AbastecimentoScreen extends JFrame {

    private String loggedInUsername;
    private JPanel pumpsPanel;
    private BombaService bombaService;
    private PdfService pdfService;

    public AbastecimentoScreen(String username) {
        this.loggedInUsername = username;
        this.bombaService = new BombaService();
        this.pdfService = new PdfService();
        setTitle("Central de Abastecimento - PDV Posto de Combustível");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(10, 10));

        // --- Painel Superior (Usuário e Logout) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ColorPalette.BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel userLabel = new JLabel("Bem-vindo, " + loggedInUsername + "!", SwingConstants.LEFT);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(ColorPalette.TEXT);
        topPanel.add(userLabel, BorderLayout.WEST);

        JButton logoutButton = createStyledButton("Sair");
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });
        JPanel logoutButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButtonPanel.setOpaque(false);
        logoutButtonPanel.add(logoutButton);
        topPanel.add(logoutButtonPanel, BorderLayout.EAST);

        // --- Título Principal ---
        JLabel mainTitleLabel = new JLabel("Central de Abastecimento", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        mainTitleLabel.setForeground(ColorPalette.PRIMARY);
        mainTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // --- Container do Cabeçalho (Título e Painel Superior) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(mainTitleLabel, BorderLayout.CENTER);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // --- Painel de Bombas (com GridLayout) ---
        pumpsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        pumpsPanel.setOpaque(false);
        pumpsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adiciona margem

        contentPane.add(pumpsPanel, BorderLayout.CENTER);

        carregarBombas();
    }

    private void carregarBombas() {
        System.out.println("Iniciando o carregamento das bombas...");
        SwingWorker<List<BombaDTO>, Void> worker = new SwingWorker<List<BombaDTO>, Void>() {
            @Override
            protected List<BombaDTO> doInBackground() throws Exception {
                System.out.println("Buscando bombas do backend...");
                return bombaService.buscarTodas();
            }

            @Override
            protected void done() {
                try {
                    pumpsPanel.removeAll();
                    List<BombaDTO> bombas = get();
                    System.out.println("Bombas recebidas: " + (bombas != null ? bombas.size() : "null"));

                    if (bombas != null && !bombas.isEmpty()) {
                        for (BombaDTO bomba : bombas) {
                            System.out.println("Criando painel para a bomba: " + bomba.getNome());
                            pumpsPanel.add(createPumpSection(bomba));
                        }
                    } else {
                        System.out.println("Nenhuma bomba encontrada ou a lista está vazia.");
                    }

                    pumpsPanel.revalidate();
                    pumpsPanel.repaint();
                    System.out.println("Painel de bombas atualizado.");

                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause();
                    System.err.println("Erro ao carregar bombas: " + cause.getMessage());
                    cause.printStackTrace();

                    if (cause instanceof IOException || cause instanceof ApiServiceException) {
                        JOptionPane.showMessageDialog(AbastecimentoScreen.this, "Erro ao carregar bombas: " + cause.getMessage(), "Erro de API", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(AbastecimentoScreen.this, "Ocorreu um erro inesperado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        worker.execute();
    }

    private JPanel createPumpSection(BombaDTO bomba) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(bomba.getNome(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sectionPanel.add(Box.createVerticalGlue());

        Color statusColor;
        switch (bomba.getStatus()) {
            case "ATIVA":
                statusColor = Color.ORANGE;
                break;
            case "CONCLUIDA":
                statusColor = Color.GREEN;
                break;
            default:
                statusColor = Color.RED;
                break;
        }

        JLabel statusLabel = new JLabel("Status: " + bomba.getStatus(), SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setForeground(statusColor);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        sectionPanel.add(statusLabel);

        // Lógica de interação baseada no status
        if (bomba.getStatus().equals("INATIVA")) {
            sectionPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            sectionPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    abrirDialogoAbastecimento(bomba);
                }
            });
        } else if (bomba.getStatus().equals("CONCLUIDA")) {
            sectionPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            sectionPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(AbastecimentoScreen.this,
                            "Aguarde, preparando a bomba para um novo uso.",
                            "Bomba em Preparação",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });

            Timer timer = new Timer(5000, e -> {
                System.out.println("Timer de 5s disparado para a bomba " + bomba.getNome() + ". Resetando para INATIVA.");
                try {
                    bombaService.atualizarStatus(bomba.getId(), "INATIVA");
                    carregarBombas();
                } catch (IOException | ApiServiceException ex) {
                    JOptionPane.showMessageDialog(AbastecimentoScreen.this,
                            "Erro ao resetar a bomba: " + ex.getMessage(),
                            "Erro de API",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }

        return sectionPanel;
    }

    private void abrirDialogoAbastecimento(BombaDTO bomba) {
        AbastecimentoDialog dialog = new AbastecimentoDialog(this, bomba);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            try {
                // 1. Atualiza o status para ATIVA
                bombaService.atualizarStatus(bomba.getId(), "ATIVA");

                // 2. Recarrega a tela IMEDIATAMENTE para mostrar a bomba laranja
                carregarBombas();

                // 3. Abre a animação (que agora é independente)
                ProdutoDTO produto = dialog.getProdutoSelecionado();
                double litros = dialog.getLitrosAbastecidos();
                double reais = dialog.getReaisAbastecidos();
                new AnimacaoAbastecimentoDialog(this, bomba, produto, litros, reais).setVisible(true);

                // 4. Após a animação, recarrega a tela de novo para mostrar o status CONCLUIDA
                carregarBombas();

                // 5. Pergunta sobre o cupom fiscal
                int resposta = JOptionPane.showConfirmDialog(this,
                        "Deseja imprimir o cupom fiscal (NFC-e)?",
                        "Impressão de Cupom",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (resposta == JOptionPane.YES_OPTION) {
                    SelecaoClienteScreen clienteScreen = new SelecaoClienteScreen(this);
                    clienteScreen.setVisible(true);

                    PessoaResponse clienteSelecionado = clienteScreen.getClienteSelecionado();
                    boolean consumidorNaoIdentificado = clienteScreen.isConsumidorNaoIdentificado();

                    if (clienteSelecionado != null || consumidorNaoIdentificado) {
                        try {
                            pdfService.gerarDanfeNfce(loggedInUsername, bomba, produto, litros, reais, clienteSelecionado);
                            JOptionPane.showMessageDialog(this, "PDF da NFC-e gerado e aberto com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this, "Erro ao gerar ou abrir o PDF: " + ex.getMessage(), "Erro de PDF", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                }

            } catch (IOException | ApiServiceException e) {
                JOptionPane.showMessageDialog(this, "Erro ao iniciar abastecimento: " + e.getMessage(), "Erro de API", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.PRIMARY);
        button.setForeground(ColorPalette.WHITE_TEXT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup(); // Inicializa o FlatLaf
            new AbastecimentoScreen("TesteUser").setVisible(true);
        });
    }
}
