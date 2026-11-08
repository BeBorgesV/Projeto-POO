package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import controller.ControladorDoJogo;
import model.Jogo;
import model.ObservadoIF;
import model.ObservadorIF;

/**
 * Desenha o tabuleiro e tudo o que está sobre ele com drawImage(): tributos
 * nas províncias, tesouros nas cidades, yurts, peões, Khan, peças de bônus
 * dos conselheiros e a área de melhorias.
 *
 * É observador do Jogo (Cap. 17): se registra pelo Controller e, a cada
 * notificação, redesenha consultando a fachada.
 */
class PainelTabuleiro extends JPanel implements ObservadorIF {
	private static final long serialVersionUID = 1L;

	static final double ESCALA = 0.33;
	private static final int RAIO_DO_CLIQUE = 55;
	private static final int LARGURA_PEAO = 28;
	private static final int ALTURA_PEAO = 42;
	private static final int FICHA = 22;
	private static final Font FONTE = new Font("SansSerif", Font.BOLD, 12);

	private final Jogo jogo = Jogo.getJogo();
	private final Imagens imagens;
	private final CoordenadasDoMapa mapa = new CoordenadasDoMapa();
	private final Image tabuleiro;
	private final int largura;
	private final int altura;

	PainelTabuleiro(Imagens imagens) {
		this.imagens = imagens;
		Image original = imagens.get("tabuleiro.jpg");
		largura = (int) (original.getWidth(null) * ESCALA);
		altura = (int) (original.getHeight(null) * ESCALA);
		tabuleiro = reduzir(original);
		setPreferredSize(new Dimension(largura, altura));
		ControladorDoJogo.getControlador().registra(this);
	}

	@Override
	public void notify(ObservadoIF o) {
		repaint();
	}

	/** Reduz a imagem do tabuleiro uma vez só, para não redimensionar a cada repintura. */
	private Image reduzir(Image original) {
		BufferedImage reduzida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = reduzida.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.drawImage(original, 0, 0, largura, altura, null);
		g2.dispose();
		return reduzida;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(tabuleiro, 0, 0, null);
		if (!jogo.partidaIniciada()) {
			return;
		}
		g2.setFont(FONTE);
		desenharAreaDeMelhorias(g2);
		desenharBonus(g2);
		desenharProvincias(g2);
		desenharCidades(g2);
		desenharYurts(g2);
		desenharPeoes(g2);
		desenharKhan(g2);
	}

	private int x(Point p) {
		return (int) (p.x * ESCALA);
	}

	private int y(Point p) {
		return (int) (p.y * ESCALA);
	}

	private void desenharAreaDeMelhorias(Graphics2D g2) {
		List<String> ids = jogo.melhoriasDisponiveis();
		for (int i = 0; i < ids.size(); i++) {
			if (!ids.get(i).isEmpty()) {
				Point p = mapa.lugar(CoordenadasDoMapa.AREA + i);
				g2.drawImage(imagens.get("melhorias/" + ids.get(i) + ".jpg"), x(p) - 37, y(p) - 19, 74, 39, null);
			}
		}
	}

	private void desenharBonus(Graphics2D g2) {
		for (String parada : jogo.conselheiros()) {
			String bonus = jogo.bonusDoConselheiro(parada);
			Point p = mapa.lugar(CoordenadasDoMapa.BONUS + parada);
			if (bonus != null && p != null) {
				g2.drawImage(imagens.get("bonus/" + bonus + ".jpg"), x(p) - 22, y(p) - 22, 44, 44, null);
			}
		}
	}

	/** Uma ficha do tipo de tributo por peça na província, lado a lado. */
	private void desenharProvincias(Graphics2D g2) {
		for (String provincia : jogo.provincias()) {
			Point p = mapa.lugar(provincia);
			Image ficha = imagens.get("tributos/" + jogo.tributoDaProvincia(provincia) + ".jpg");
			int pecas = jogo.pecasNaProvincia(provincia);
			for (int i = 0; i < pecas; i++) {
				g2.drawImage(ficha, x(p) - FICHA / 2 + (i - (pecas - 1) / 2) * (FICHA - 4), y(p) + 14, FICHA, FICHA, null);
			}
		}
	}

	/** Tesouros das cidades reveladas e o yurt de quem conquistou. */
	private void desenharCidades(Graphics2D g2) {
		for (String cidade : jogo.cidades()) {
			Point p = mapa.lugar(cidade);
			List<String> tesouros = jogo.tesourosDaCidade(cidade);
			for (int i = 0; i < tesouros.size(); i++) {
				int tx = x(p) - 2 * FICHA + i * FICHA;
				g2.drawImage(imagens.get("tesouros/" + tesouros.get(i) + ".jpg"), tx, y(p) - FICHA - 8, FICHA, FICHA, null);
			}
			String conquistador = jogo.conquistadorDe(cidade);
			if (conquistador != null) {
				desenharYurt(g2, conquistador, x(p), y(p));
			}
		}
	}

	private void desenharYurts(Graphics2D g2) {
		for (String parada : jogo.paradas()) {
			List<String> donos = jogo.yurtsEm(parada);
			List<Point> pontos = mapa.pontosDe(parada);
			for (int i = 0; i < donos.size(); i++) {
				Point p = pontos.get(i % pontos.size());
				desenharYurt(g2, donos.get(i), x(p) + 14, y(p) + 10);
			}
		}
	}

	/** Yurt de um jogador: disco da cor dele com a peça de yurt por cima. */
	private void desenharYurt(Graphics2D g2, String cor, int cx, int cy) {
		g2.setColor(Cores.de(cor));
		g2.fillOval(cx - 13, cy - 13, 26, 26);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		g2.drawOval(cx - 13, cy - 13, 26, 26);
		g2.drawImage(imagens.get("tributos/YURT.jpg"), cx - 9, cy - 9, 18, 18, null);
	}

	private void desenharPeoes(Graphics2D g2) {
		Map<String, Integer> peoesNaParada = new HashMap<>();
		for (String cor : jogo.cores()) {
			for (int peao = 0; peao < jogo.quantidadeDePeoes(cor); peao++) {
				String parada = jogo.posicaoDoPeao(cor, peao);
				int ordem = peoesNaParada.getOrDefault(parada, 0);
				peoesNaParada.put(parada, ordem + 1);

				// se houver mais peões que pontos (peão passando por parada ocupada), desloca para o lado
				List<Point> pontos = mapa.pontosDe(parada);
				Point p = pontos.get(ordem % pontos.size());
				int deslocamento = ordem / pontos.size() * LARGURA_PEAO / 2;
				int px = x(p) - LARGURA_PEAO / 2 + deslocamento;
				int py = y(p) - ALTURA_PEAO + 8;
				g2.drawImage(imagens.get("peao_" + cor.toLowerCase() + ".png"), px, py, LARGURA_PEAO, ALTURA_PEAO, null);
				if (jogo.quantidadeDePeoes(cor) > 1 && jogo.peaoAtivo(cor) == peao) {
					g2.setColor(Color.WHITE); // marca o peão da Altani que está jogando
					g2.drawString("*", px + LARGURA_PEAO / 2 - 3, py);
				}
			}
		}
	}

	private void desenharKhan(Graphics2D g2) {
		String local = jogo.localDoKhan();
		if (local != null) {
			Point p = mapa.lugar(CoordenadasDoMapa.KHAN + local);
			g2.drawImage(imagens.get("peao_khan.png"), x(p) - 14, y(p) - 36, LARGURA_PEAO, ALTURA_PEAO, null);
		}
	}

	/** Parada onde o jogador clicou, ou null se o clique foi fora das paradas. */
	String paradaEm(int x, int y) {
		return mapa.paradaEm((int) (x / ESCALA), (int) (y / ESCALA), RAIO_DO_CLIQUE);
	}
}
