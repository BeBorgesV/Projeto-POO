package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import model.Jogo;

/** Área pintada com a cor do jogador da vez, com os movimentos que ainda restam. */
class PainelDaVez extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Font FONTE_TITULO = new Font("SansSerif", Font.BOLD, 20);
	private static final Font FONTE_TEXTO = new Font("SansSerif", Font.PLAIN, 16);
	private static final Map<String, Color> CORES = new HashMap<>();

	static {
		CORES.put("Azul", new Color(47, 95, 179));
		CORES.put("Amarelo", new Color(242, 194, 48));
		CORES.put("Preto", new Color(60, 60, 60));
		CORES.put("Vermelho", new Color(208, 48, 48));
		CORES.put("Verde", new Color(60, 154, 60));
	}

	private final Jogo jogo;

	PainelDaVez(Jogo jogo) {
		this.jogo = jogo;
		setPreferredSize(new Dimension(240, 130));
		setMaximumSize(getPreferredSize());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		String cor = jogo.corDaVez();
		g2.setColor(CORES.get(cor));
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.setColor(cor.equals("Amarelo") ? Color.BLACK : Color.WHITE);
		g2.setFont(FONTE_TITULO);
		g2.drawString("Vez do " + cor, 15, 40);
		g2.setFont(FONTE_TEXTO);
		if (jogo.turnoEmAndamento()) {
			g2.drawString("Movimentos restantes: " + jogo.movimentosRestantes(), 15, 80);
		} else {
			g2.drawString("Escolha os movimentos", 15, 80);
			g2.drawString("e ative a coluna", 15, 102);
		}
	}
}
