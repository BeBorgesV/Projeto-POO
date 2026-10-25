package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import model.Jogo;

/** Desenha o tabuleiro e os peões com drawImage(). */
class PainelTabuleiro extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final double ESCALA = 0.34;
	private static final int RAIO_DO_CLIQUE = 55;
	private static final int LARGURA_PEAO = 28;
	private static final int ALTURA_PEAO = 42;

	private final Jogo jogo;
	private final Imagens imagens;
	private final CoordenadasDoMapa mapa = new CoordenadasDoMapa();
	private final Image tabuleiro;

	PainelTabuleiro(Jogo jogo, Imagens imagens) {
		this.jogo = jogo;
		this.imagens = imagens;
		tabuleiro = imagens.get("tabuleiro.jpg");
		setPreferredSize(new Dimension((int) (tabuleiro.getWidth(null) * ESCALA),
				(int) (tabuleiro.getHeight(null) * ESCALA)));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(tabuleiro, 0, 0, getPreferredSize().width, getPreferredSize().height, null);
		desenharPeoes(g2);
	}

	private void desenharPeoes(Graphics2D g2) {
		Map<String, Integer> peoesNaParada = new HashMap<>();
		for (String cor : jogo.cores()) {
			String parada = jogo.posicaoDe(cor);
			int ordem = peoesNaParada.getOrDefault(parada, 0);
			peoesNaParada.put(parada, ordem + 1);

			List<Point> pontos = mapa.pontosDe(parada);
			Point p = pontos.get(ordem % pontos.size());
			int x = (int) (p.x * ESCALA) - LARGURA_PEAO / 2;
			int y = (int) (p.y * ESCALA) - ALTURA_PEAO + 8;
			g2.drawImage(imagens.get("peao_" + cor.toLowerCase() + ".png"), x, y, LARGURA_PEAO, ALTURA_PEAO, null);
		}
	}

	/** Parada onde o jogador clicou, ou null se o clique foi fora das paradas. */
	String paradaEm(int x, int y) {
		return mapa.paradaEm((int) (x / ESCALA), (int) (y / ESCALA), RAIO_DO_CLIQUE);
	}
}
