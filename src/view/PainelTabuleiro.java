package view;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import model.Jogo; // importa a fachada do Model

public class PainelTabuleiro extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private BufferedImage imagemTabuleiro;
    private Map<String, Point> coordenadasParadas; // colocar parada

    public PainelTabuleiro() {
        carregarImagens();
        inicializarCoordenadas();

        // pra descobrir as coordenadas das paradas:
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println("Coordenadas clicadas -> X: " + x + ", Y: " + y);
            }
        });
    }

    private void carregarImagens() {
        try {
            // Carrega a imagem do tabuleiro da pasta resources
            imagemTabuleiro = ImageIO.read(new File("src/images/tabuleiro.jpg"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo 'tabuleiro.jpg': " + e.getMessage());
        }
    }
    
    private void inicializarCoordenadas() {  
        coordenadasParadas = new HashMap<>();
        
        // Posição central (Karakorum)
        coordenadasParadas.put("Karakorum", new Point(585, 360)); 
        
        // paradas da regiao amarela (numeradas de acordo com uma imagem)
        coordenadasParadas.put("P1", new Point(50, 136));
        coordenadasParadas.put("P2", new Point(200, 64));
        coordenadasParadas.put("P3", new Point(190, 142));
        coordenadasParadas.put("P4_DUPLA1", new Point(348, 43));
        coordenadasParadas.put("P4_DUPLA2", new Point(378, 63));
        coordenadasParadas.put("P5", new Point(350, 146));
        coordenadasParadas.put("P6", new Point(441, 222));
        coordenadasParadas.put("P7", new Point(518, 129));
        coordenadasParadas.put("P8", new Point(635, 175));
        coordenadasParadas.put("P9", new Point(714, 61));
        coordenadasParadas.put("P10", new Point(766, 157));
        coordenadasParadas.put("P11_DUPLA1", new Point(890, 129));
        coordenadasParadas.put("P11_DUPLA2", new Point(935, 130));
        
        //fazer resto dps
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (imagemTabuleiro != null) {
            g2d.drawImage(imagemTabuleiro, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.RED);
            g2d.drawString("Erro: 'tabuleiro.jpg' não encontrada na pasta resources/", 30, 30);
        }
        desenharPaoesDosJogadores(g2d);
    }

    private void desenharPaoesDosJogadores(Graphics2D g2d) {
        try {
            // Pede dinamicamente ao Model a lista de cores de todos os jogadores da partida
            List<String> coresJogadores = Jogo.obterInstancia().obterCoresJogadores();
            
            if (coresJogadores == null || coresJogadores.isEmpty()) {
                return;
            }

            int totalJogadores = coresJogadores.size();
            int indice = 0;

            for (String cor : coresJogadores) {
                try {
                    String nomeParadaAtual = Jogo.obterInstancia().posicaoDe(cor);
                    Point ponto = coordenadasParadas.get(nomeParadaAtual);

                    if (ponto != null) {
                        // Define a cor visual do peão com base na string da cor do jogador
                        if (cor.equalsIgnoreCase("Vermelho")) {
                            g2d.setColor(Color.RED);
                        } else if (cor.equalsIgnoreCase("Azul")) {
                            g2d.setColor(Color.CYAN);
                        } else if (cor.equalsIgnoreCase("Verde")) {
                            g2d.setColor(Color.GREEN);
                        } else if (cor.equalsIgnoreCase("Amarelo")) {
                            g2d.setColor(Color.YELLOW);
                        } else {
                            g2d.setColor(Color.MAGENTA); // Cor extra para 5º jogador (ex: Roxo/Rosa)
                        }

                        // CÁLCULO DINÂMICO DE ESPALHAMENTO:
                        int deslocamentoX = (indice - (totalJogadores / 2)) * 28;

                        // Desenha o peão preenchido e com borda preta
                        g2d.fillOval(ponto.x + deslocamentoX - 8, ponto.y - 8, 22, 22);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(ponto.x + deslocamentoX - 8, ponto.y - 8, 22, 22);
                    }
                } catch (Exception ex) {
                    // Ignora se der erro na consulta de algum jogador específico
                }
                indice++;
            }
        } catch (Exception e) {
            // Ignora se a partida ainda não foi iniciada
        }
    }
}