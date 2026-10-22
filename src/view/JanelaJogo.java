package view;

import javax.swing.*;
import java.awt.*;
import model.Jogo;

public class JanelaJogo extends JFrame {
    
    private static final long serialVersionUID = 1L;

    private PainelTabuleiro painelTabuleiro;
    private JLabel labelJogadorDaVez;

    public JanelaJogo() {
        // Configurações básicas da janela (Respeita o limite máximo de 1400x1050)
        setTitle("Herdeiros do Khan");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Uso obrigatório de gerenciador de layout BorderLayout
        setLayout(new BorderLayout());

        // Painel central onde o tabuleiro será desenhado via Java2D
        painelTabuleiro = new PainelTabuleiro();
        add(painelTabuleiro, BorderLayout.CENTER);

        // Painel superior para o indicador de turno
        JPanel painelControle = new JPanel();
        painelControle.setBackground(new Color(230, 220, 200));
        painelControle.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Obtém a cor do jogador da vez diretamente do Model via Singleton
        String corVez = "Desconhecido";
        try {
            corVez = Jogo.obterInstancia().corDaVez();
        } catch (Exception e) {
            // Caso a partida ainda não tenha sido iniciada
        }

        labelJogadorDaVez = new JLabel("Jogador da Vez: " + corVez);
        labelJogadorDaVez.setFont(new Font("Arial", Font.BOLD, 14));

        painelControle.add(labelJogadorDaVez);
        add(painelControle, BorderLayout.NORTH);
    }

    // Método main para testar a abertura da janela diretamente
    public static void main(String[] args) {
        // Executa na thread de eventos do Swing SEM USAR LAMBDA
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Inicializa a partida de exemplo usando o Singleton
                    Jogo.obterInstancia().iniciarPartida("Vermelho", "Azul", "Verde");
                    
                    JanelaJogo janela = new JanelaJogo();
                    janela.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}