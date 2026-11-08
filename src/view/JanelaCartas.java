package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.ControladorDoJogo;
import model.Jogo;
import model.ObservadoIF;
import model.ObservadorIF;

/**
 * Janela de cartas do jogador da vez: conselheiro secreto, as cartas de
 * conselheiro aberto que ainda estão na mesa, os tesouros e as peças de
 * bônus guardadas, tudo com drawImage(). No começo da partida, os botões
 * servem para escolher 1 das 2 cartas secretas.
 *
 * Observadora do Jogo: abre e fecha conforme {@code Jogo.cartasVisiveis()}.
 */
public class JanelaCartas extends JFrame implements ObservadorIF {
	private static final long serialVersionUID = 1L;

	public static final String SECRETO_1 = "SECRETO_1";
	public static final String SECRETO_2 = "SECRETO_2";

	private static final int CARTA_L = 130;
	private static final int CARTA_A = 204;
	private static final Font FONTE = new Font("SansSerif", Font.BOLD, 14);

	private final Jogo jogo = Jogo.getJogo();
	private final Imagens imagens = new Imagens();
	private final PainelCartas painel = new PainelCartas();
	private final JButton primeira = new JButton("Ficar com a 1ª carta secreta");
	private final JButton segunda = new JButton("Ficar com a 2ª carta secreta");
	private final JPanel escolha = new JPanel(new FlowLayout());

	private class PainelCartas extends JPanel {
		private static final long serialVersionUID = 1L;

		PainelCartas() {
			setPreferredSize(new Dimension(880, 560));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (jogo.partidaIniciada()) {
				desenhar((Graphics2D) g);
			}
		}
	}

	public JanelaCartas() {
		super("Cartas");
		primeira.setActionCommand(SECRETO_1);
		segunda.setActionCommand(SECRETO_2);
		escolha.add(primeira);
		escolha.add(segunda);
		add(painel, BorderLayout.CENTER);
		add(escolha, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		pack();
		ControladorDoJogo.getControlador().registra(this);
	}

	public void adicionarTratadorDosBotoes(ActionListener tratador) {
		primeira.addActionListener(tratador);
		segunda.addActionListener(tratador);
	}

	public void adicionarTratadorDaJanela(WindowListener tratador) {
		addWindowListener(tratador);
	}

	@Override
	public void notify(ObservadoIF o) {
		if (!jogo.partidaIniciada()) {
			return;
		}
		String cor = jogo.corDaVez();
		setTitle("Cartas de " + jogo.herdeiroDe(cor) + " (" + cor + ")");
		escolha.setVisible(jogo.precisaEscolherSecreto(cor) && !jogo.partidaEncerrada());
		if (jogo.cartasVisiveis() != isVisible()) {
			setVisible(jogo.cartasVisiveis());
		}
		painel.repaint();
	}

	private void desenhar(Graphics2D g2) {
		String cor = jogo.corDaVez();
		g2.setFont(FONTE);
		g2.setColor(Color.BLACK);

		g2.drawString(jogo.precisaEscolherSecreto(cor) ? "Conselheiros secretos (escolha 1)" : "Conselheiro secreto", 10, 20);
		List<String> secretos = jogo.conselheirosSecretosDe(cor);
		for (int i = 0; i < secretos.size(); i++) {
			g2.drawImage(imagens.get("secretos/" + secretos.get(i) + ".jpg"), 10 + i * (CARTA_L + 10), 30, CARTA_L, CARTA_A, null);
		}

		g2.drawString("Conselheiros abertos na mesa", 300, 20);
		List<String> abertos = jogo.conselheirosAbertos();
		for (int i = 0; i < abertos.size(); i++) {
			g2.drawImage(imagens.get("abertos/" + abertos.get(i) + ".jpg"), 300 + i * (CARTA_L + 10), 30, CARTA_L, CARTA_A, null);
		}

		g2.drawString("Tesouros", 10, 270);
		List<String> tipos = jogo.tiposDeTesouro();
		for (int i = 0; i < tipos.size(); i++) {
			int x = 10 + i * 90;
			g2.drawImage(imagens.get("tesouros/" + tipos.get(i) + ".jpg"), x, 280, 60, 60, null);
			g2.drawString("x " + jogo.tesourosDoTipo(cor, tipos.get(i)), x + 62, 315);
		}

		g2.drawString("Peças de tributo", 470, 270);
		String[] tributos = { "ESPADA", "YURT", "MOEDA" };
		for (int i = 0; i < tributos.length; i++) {
			int x = 470 + i * 90;
			g2.drawImage(imagens.get("tributos/" + tributos[i] + ".jpg"), x, 280, 60, 60, null);
			g2.drawString("x " + jogo.tributosDe(cor, tributos[i]), x + 62, 315);
		}

		g2.drawString("Peças de bônus guardadas (" + jogo.bonusObtidosDe(cor) + " obtidas no total)", 10, 380);
		List<String> bonus = jogo.bonusGuardadosDe(cor);
		for (int i = 0; i < bonus.size(); i++) {
			g2.drawImage(imagens.get("bonus/" + bonus.get(i) + ".jpg"), 10 + i * 90, 390, 80, 80, null);
		}

		g2.drawString("Conselheiros do mapa: R2 2 sedas, R11 2 ferros, P1 2 peles, P8 2 especiarias,", 10, 500);
		g2.drawString("C7 2 bois, C9 3 tesouros quaisquer, Karakorum ferro + pele + boi (2 votos)", 10, 520);
	}
}
