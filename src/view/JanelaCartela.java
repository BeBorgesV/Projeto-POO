package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.ControladorDoJogo;
import model.Jogo;
import model.ObservadoIF;
import model.ObservadorIF;

/**
 * Janela da cartela (tabuleiro do jogador da vez) com as 4 colunas, as
 * melhorias e as peças de ativação, tudo desenhado com drawImage().
 * Clicar numa coluna livre ativa a coluna.
 *
 * Observadora do Jogo: abre e fecha conforme {@code Jogo.cartelaVisivel()} e
 * redesenha a cada mudança (é uma das "mudanças de estado" do enunciado).
 */
public class JanelaCartela extends JFrame implements ObservadorIF {
	private static final long serialVersionUID = 1L;

	private static final int LARGURA_DA_IMAGEM = 1000;
	private static final int ALTURA_DA_IMAGEM = 546;
	// geometria da imagem da cartela com 1000 px de largura (medida na imagem)
	private static final int[] X_DAS_COLUNAS = { 262, 438, 614, 812 };
	private static final int LARGURA_DO_ESPACO = 153;
	private static final int ALTURA_DO_ESPACO = 80;
	private static final int Y_DO_1O_ESPACO = 318;
	private static final int PASSO_ENTRE_ESPACOS = 90;
	private static final int Y_DA_BASE = 413;

	private final Jogo jogo = Jogo.getJogo();
	private final Imagens imagens = new Imagens();
	private final PainelCartela painel = new PainelCartela();

	/**
	 * Desenha a cartela; fica numa classe interna só para ter o paintComponent.
	 * Tudo é desenhado nas medidas da imagem de 1000 px e reduzido com
	 * Graphics2D.scale, para a janela caber ao lado da janela do jogo.
	 */
	private class PainelCartela extends JPanel {
		private static final long serialVersionUID = 1L;

		PainelCartela() {
			setPreferredSize(new Dimension(LARGURA_DA_IMAGEM, ALTURA_DA_IMAGEM));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (jogo.partidaIniciada()) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.scale(escala(), escala());
				desenhar(g2);
				g2.dispose();
			}
		}
	}

	private double escala() {
		return painel.getWidth() / (double) LARGURA_DA_IMAGEM;
	}

	/** Ajusta a janela para ocupar {@code largura} pixels, mantendo a proporção da cartela. */
	public void ajustarLargura(int largura) {
		Insets bordas = getInsets();
		int interna = largura - bordas.left - bordas.right;
		painel.setPreferredSize(new Dimension(interna, interna * ALTURA_DA_IMAGEM / LARGURA_DA_IMAGEM));
		pack();
	}

	public JanelaCartela() {
		super("Cartela");
		add(painel);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		pack();
		ControladorDoJogo.getControlador().registra(this);
	}

	public void adicionarTratadorDoClique(MouseListener tratador) {
		painel.addMouseListener(tratador);
	}

	public void adicionarTratadorDaJanela(WindowListener tratador) {
		addWindowListener(tratador);
	}

	/** Coluna sob o clique (em pixels da tela, desfeita a escala), ou null. */
	public String colunaEm(int xNaTela, int yNaTela) {
		int x = (int) (xNaTela / escala());
		int y = (int) (yNaTela / escala());
		List<String> colunas = jogo.colunas();
		for (int i = 0; i < X_DAS_COLUNAS.length; i++) {
			if (x >= X_DAS_COLUNAS[i] && x <= X_DAS_COLUNAS[i] + LARGURA_DO_ESPACO && y > 40 && y < 500) {
				return colunas.get(i);
			}
		}
		return null;
	}

	@Override
	public void notify(ObservadoIF o) {
		if (!jogo.partidaIniciada()) {
			return;
		}
		String cor = jogo.corDaVez();
		setTitle("Cartela de " + jogo.herdeiroDe(cor) + " (" + cor + ")");
		if (jogo.cartelaVisivel() != isVisible()) {
			setVisible(jogo.cartelaVisivel());
		}
		painel.repaint();
	}

	private void desenhar(Graphics2D g2) {
		String cor = jogo.corDaVez();
		String herdeiro = jogo.herdeiroDe(cor).toUpperCase();
		g2.drawImage(imagens.get("cartelas/" + herdeiro + ".jpg"), 0, 0, 1000, 546, null);
		List<String> colunas = jogo.colunas();
		List<String> usadas = jogo.colunasUsadasDe(cor);
		for (int i = 0; i < colunas.size(); i++) {
			int x = X_DAS_COLUNAS[i];
			List<String> melhorias = jogo.melhoriasDe(cor, colunas.get(i));
			for (int espaco = 0; espaco < melhorias.size(); espaco++) {
				int y = Y_DO_1O_ESPACO - espaco * PASSO_ENTRE_ESPACOS;
				g2.drawImage(imagens.get("melhorias/" + melhorias.get(espaco) + ".jpg"), x, y, LARGURA_DO_ESPACO,
						ALTURA_DO_ESPACO, null);
			}
			if (usadas.contains(colunas.get(i))) {
				g2.drawImage(imagens.get("ativacao/" + cor.toLowerCase() + ".jpg"), x + 41, Y_DA_BASE + 4, 72, 72, null);
			}
		}
	}
}
