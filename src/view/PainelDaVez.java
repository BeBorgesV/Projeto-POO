package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

import controller.ControladorDoJogo;
import model.Jogo;
import model.ObservadoIF;
import model.ObservadorIF;

/**
 * Área pintada com a cor do jogador da vez (2ª iteração, Figura 4) com o que
 * resta no turno e um quadro com os recursos e votos de todos. No fim da
 * partida mostra a apuração. Observador do Jogo.
 */
class PainelDaVez extends JPanel implements ObservadorIF {
	private static final long serialVersionUID = 1L;

	private static final Font TITULO = new Font("SansSerif", Font.BOLD, 17);
	private static final Font NOME = new Font("SansSerif", Font.BOLD, 26);
	private static final Font PASSO = new Font("SansSerif", Font.BOLD, 14);
	private static final Font TURNO = new Font("SansSerif", Font.BOLD, 13);
	private static final int ALTURA_DA_FAIXA = 136;
	private static final Font TEXTO = new Font("SansSerif", Font.PLAIN, 12);
	private static final Font NEGRITO = new Font("SansSerif", Font.BOLD, 12);
	private static final Font CABECALHO = new Font("SansSerif", Font.BOLD, 11);
	// colunas do quadro: título em 2 linhas e o centro de cada coluna (os números ficam centralizados embaixo)
	private static final String[][] TITULOS = { { "Espadas", "" }, { "Moedas", "" }, { "Peças", "de yurt" },
			{ "Tesouros", "" }, { "Yurts no", "estoque" }, { "Votos", "" } };
	private static final int[] CENTROS = { 112, 161, 210, 260, 313, 362 };

	private final Jogo jogo = Jogo.getJogo();

	PainelDaVez() {
		setPreferredSize(new Dimension(400, 304));
		setMaximumSize(getPreferredSize());
		ControladorDoJogo.getControlador().registra(this);
	}

	@Override
	public void notify(ObservadoIF o) {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (!jogo.partidaIniciada()) {
			return;
		}
		if (jogo.partidaEncerrada()) {
			desenharResultado(g2);
			return;
		}
		desenharFaixaDaVez(g2);
		desenharQuadro(g2, ALTURA_DA_FAIXA + 20);
	}

	/**
	 * Faixa na cor do jogador da vez: o nome do herdeiro em destaque, o
	 * próximo passo numa caixa branca e o que ainda resta no turno.
	 */
	private void desenharFaixaDaVez(Graphics2D g2) {
		String cor = jogo.corDaVez();
		g2.setColor(Cores.de(cor));
		g2.fillRect(0, 0, getWidth(), ALTURA_DA_FAIXA);
		g2.setColor(Cores.texto(cor));
		g2.setFont(NOME);
		String nome = "Vez de " + jogo.herdeiroDe(cor);
		g2.drawString(nome, 10, 32);
		g2.setFont(TITULO);
		g2.drawString("(" + cor + ")", 18 + g2.getFontMetrics(NOME).stringWidth(nome), 32);

		// próximo passo: a coisa mais importante a fazer agora
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(8, 44, getWidth() - 16, 30, 12, 12);
		g2.setColor(Color.BLACK);
		g2.setFont(PASSO);
		g2.drawString("▶ " + proximoPasso(cor), 16, 64);

		g2.setColor(Cores.texto(cor));
		g2.setFont(TURNO);
		if (jogo.turnoEmAndamento()) {
			g2.drawString("Coluna " + jogo.colunaAtivada() + "   |   Movimentos: " + jogo.movimentosRestantes()
					+ "   |   Tributos: " + jogo.acoesDeTributoRestantes(), 10, 98);
			g2.setFont(TEXTO);
			g2.drawString("Virtuais: " + jogo.moedasVirtuais() + " moeda, " + jogo.espadasVirtuais() + " espada, "
					+ jogo.yurtsVirtuais() + " yurt" + (jogo.tesourosVirtuais().isEmpty() ? "" : ", tesouro " + jogo.tesourosVirtuais()), 10, 120);
		} else if (jogo.quantidadeDePeoes(cor) > 1) {
			g2.drawString("Altani joga com o peão " + (jogo.peaoAtivo(cor) + 1) + " (marcado com * no mapa)", 10, 98);
		}
	}

	private String proximoPasso(String cor) {
		if (jogo.precisaEscolherSecreto(cor)) {
			return "Escolha o conselheiro secreto (janela de cartas)";
		}
		if (!jogo.turnoEmAndamento()) {
			return "Clique numa coluna livre da cartela";
		}
		if (jogo.khanPendente()) {
			return "Use o Khan antes de encerrar o turno";
		}
		if (jogo.melhoriasGratisPendentes() > 0) {
			return "Linha completa: pegue a melhoria grátis";
		}
		return "Faça suas ações e encerre o turno";
	}

	/**
	 * Uma linha por jogador: espadas, moedas, peças de yurt, tesouros, yurts
	 * no estoque e votos, cada número centralizado sob o título da coluna.
	 */
	private void desenharQuadro(Graphics2D g2, int y) {
		g2.setColor(Color.BLACK);
		g2.setFont(CABECALHO);
		g2.drawString("Jogador", 10, y);
		for (int i = 0; i < TITULOS.length; i++) {
			centralizar(g2, TITULOS[i][0], CENTROS[i], y);
			centralizar(g2, TITULOS[i][1], CENTROS[i], y + 13);
		}
		y += 13;
		g2.drawLine(10, y + 4, getWidth() - 6, y + 4);
		g2.setFont(TEXTO);
		for (String cor : jogo.cores()) {
			y += 20;
			g2.setColor(Cores.de(cor));
			g2.fillRect(10, y - 11, 12, 12);
			g2.setColor(Color.BLACK);
			g2.drawString(jogo.herdeiroDe(cor), 26, y);
			int[] valores = { jogo.tributosDe(cor, "ESPADA"), jogo.tributosDe(cor, "MOEDA"),
					jogo.tributosDe(cor, "YURT"), jogo.tesourosDe(cor), jogo.yurtsDisponiveisDe(cor), jogo.votosDe(cor) };
			for (int i = 0; i < valores.length; i++) {
				centralizar(g2, String.valueOf(valores[i]), CENTROS[i], y);
			}
		}
		y += 24;
		g2.drawString("Kurultai: " + jogo.fichaNeutra() + " de " + jogo.limiteDoKurultai() + " votos", 10, y);
		if (jogo.rodadaFinal()) {
			g2.setFont(NEGRITO);
			g2.drawString("RODADA FINAL", 220, y);
		} else if (jogo.fimDePartidaDisparado()) {
			g2.setFont(NEGRITO);
			g2.drawString("Fim disparado: termine a rodada", 200, y);
		}
	}

	private void centralizar(Graphics2D g2, String texto, int centro, int y) {
		g2.drawString(texto, centro - g2.getFontMetrics().stringWidth(texto) / 2, y);
	}

	private void desenharResultado(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.setFont(TITULO);
		List<String> vencedores = jogo.vencedores();
		StringBuilder nomes = new StringBuilder();
		for (String cor : vencedores) {
			nomes.append(nomes.length() == 0 ? "" : " e ").append(jogo.herdeiroDe(cor));
		}
		g2.drawString("Fim! Novo Khan: " + nomes, 10, 28);
		g2.setFont(TEXTO);
		int y = 60;
		for (String cor : jogo.cores()) {
			g2.setColor(Cores.de(cor));
			g2.fillRect(10, y - 11, 12, 12);
			g2.setColor(Color.BLACK);
			g2.drawString(jogo.herdeiroDe(cor) + ": " + jogo.votosFinaisDe(cor) + " votos, "
					+ jogo.tesourosDe(cor) + " tesouros guardados", 26, y);
			y += 20;
		}
	}
}
