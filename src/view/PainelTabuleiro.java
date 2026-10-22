package view;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import model.Jogo; // importa a fachada do Model

public class PainelTabuleiro extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private BufferedImage imagemTabuleiro;
    private Map<String, Point> coordenadasParadas;

    public PainelTabuleiro() {
        carregarImagens();
        inicializarCoordenadas();
    }

    private void carregarImagens() {
        try {
            // Carrega a imagem do tabuleiro da pasta resources
            imagemTabuleiro = ImageIO.read(new File("src/images/tabuleiro.jpg"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo 'tabuleiro.jpg': " + e.getMessage());
        }
    }
    
    private void inicializarCoordenadas() {  //AJUSTAR PARA FICAR QUE NEM NO TABULEIRO
        coordenadasParadas = new HashMap<>();
        
        // AJUSTADA JÁ
        coordenadasParadas.put("Karakorum", new Point(585, 360)); 
        
        // PARADAS AINDA NAO ESTAO CORRETAS
        coordenadasParadas.put("P1", new Point(350, 300));
        coordenadasParadas.put("P2", new Point(450, 300));
        coordenadasParadas.put("P3", new Point(550, 300));
        coordenadasParadas.put("P4", new Point(400, 200));
        coordenadasParadas.put("P5", new Point(600, 200));
        coordenadasParadas.put("P6", new Point(700, 200));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. OBRIGATÓRIO: Desenhar a imagem do tabuleiro usando drawImage
        if (imagemTabuleiro != null) {
            // Desenha a imagem preenchendo o painel (ou nas dimensões originais)
            g2d.drawImage(imagemTabuleiro, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.RED);
            g2d.drawString("Erro: 'tabuleiro.jpg' não encontrada na pasta resources/", 30, 30);
        }

        // 2. OBRIGATÓRIO: Desenhar os peões consultando o Model e usando Java2D
        desenharPaoesDosJogadores(g2d);
    }

    private void desenharPaoesDosJogadores(Graphics2D g2d) { // ver qnts jogadores
        try {
            String[] coresJogadores = {"Vermelho", "Azul", "Verde"};
            
            // Variável para espalhar os peões caso estejam na mesma casa
            int deslocamentoX = -40; 

            for (String cor : coresJogadores) {
                try {
                    String nomeParadaAtual = Jogo.obterInstancia().posicaoDe(cor);
                    Point ponto = coordenadasParadas.get(nomeParadaAtual);

                    if (ponto != null) {
                        // Define a cor e ajusta a posição horizontal para não ficarem colados no mesmo pixel
                        if (cor.equalsIgnoreCase("Vermelho")) {
                            g2d.setColor(Color.RED);
                        } else if (cor.equalsIgnoreCase("Azul")) {
                            g2d.setColor(Color.CYAN);
                            deslocamentoX = 0; // peão do meio
                        } else {
                            g2d.setColor(Color.GREEN);
                            deslocamentoX = 40; // peão da direita
                        }

                        // Desenha o peão com o deslocamento aplicado
                        g2d.fillOval(ponto.x + deslocamentoX - 6, ponto.y - 6, 25, 25);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(ponto.x + deslocamentoX - 6, ponto.y - 6, 25, 25);
                    }
                } catch (Exception ex) {
                    // Ignora se der erro na consulta de algum jogador
                }
            }
        } catch (Exception e) {
            // Ignora se a partida não foi iniciada
        }
    }
}