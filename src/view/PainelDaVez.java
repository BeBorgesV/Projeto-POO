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
	private static final Font TEXTO = new Font("SansSerif", Font.PLAIN, 12);
	private static final Font NEGRITO = new Font("SansSerif", Font.BOLD, 12);

	private final Jogo jogo = Jogo.getJogo();

	PainelDaVez() {
		setPreferredSize(new Dimension(400, 300));
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
		String cor = jogo.corDaVez();
		g2.setColor(Cores.de(cor));
		g2.fillRect(0, 0, getWidth(), 120);
		g2.setColor(Cores.texto(cor));
		g2.setFont(TITULO);
		g2.drawString("Vez de " + jogo.herdeiroDe(cor) + " (" + cor + ")", 10, 24);
		g2.setFont(TEXTO);
		if (jogo.turnoEmAndamento()) {
			g2.drawString("Coluna " + jogo.colunaAtivada() + (jogo.khanPendente() ? "  -  falta usar o Khan" : ""), 10, 46);
			g2.drawString("Movimentos: " + jogo.movimentosRestantes() + "    Pegar tributo: "
					+ jogo.acoesDeTributoRestantes(), 10, 66);
			g2.drawString("Virtuais: " + jogo.moedasVirtuais() + " moeda, " + jogo.espadasVirtuais() + " espada, "
					+ jogo.yurtsVirtuais() + " yurt" + (jogo.tesourosVirtuais().isEmpty() ? "" : ", tesouro " + jogo.tesourosVirtuais()), 10, 86);
			if (jogo.melhoriasGratisPendentes() > 0) {
				g2.drawString("Linha completa: pegue " + jogo.melhoriasGratisPendentes() + " melhoria grátis", 10, 106);
			}
		} else if (jogo.precisaEscolherSecreto(cor)) {
			g2.drawString("Escolha o conselheiro secreto na janela de cartas", 10, 50);
		} else {
			g2.drawString("Clique numa coluna livre da cartela para começar", 10, 50);
			if (jogo.quantidadeDePeoes(cor) > 1) {
				g2.drawString("Altani joga com o peão " + (jogo.peaoAtivo(cor) + 1) + " (marcado com *)", 10, 70);
			}
		}
		desenharQuadro(g2, 140);
	}

	/** Uma linha por jogador: espadas, yurts e moedas, tesouros, yurts guardados e votos. */
	private void desenharQuadro(Graphics2D g2, int y) {
		g2.setColor(Color.BLACK);
		g2.setFont(NEGRITO);
		g2.drawString("Jogador        Esp  Yurt  Moe  Tes  Yurts  Votos", 10, y);
		g2.setFont(TEXTO);
		for (String cor : jogo.cores()) {
			y += 20;
			g2.setColor(Cores.de(cor));
			g2.fillRect(10, y - 11, 12, 12);
			g2.setColor(Color.BLACK);
			g2.drawString(jogo.herdeiroDe(cor), 26, y);
			g2.drawString(String.format("%3d   %3d   %3d   %3d    %3d    %3d", jogo.tributosDe(cor, "ESPADA"),
					jogo.tributosDe(cor, "YURT"), jogo.tributosDe(cor, "MOEDA"), jogo.tesourosDe(cor),
					jogo.yurtsDisponiveisDe(cor), jogo.votosDe(cor)), 110, y);
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
