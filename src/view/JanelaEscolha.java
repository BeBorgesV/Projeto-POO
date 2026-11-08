package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Jogo;

/**
 * 2ª janela (2ª iteração, Figura 2): escolha dos herdeiros que vão jogar.
 * Mostra o retrato de cada cartela (drawImage) com uma caixa de marcar. A
 * ordem de jogo é a ordem das caixas.
 */
public class JanelaEscolha extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final String JOGAR = "JOGAR";

	private final List<JCheckBox> caixas = new ArrayList<>();
	private final List<String> cores = new ArrayList<>();
	private final JButton jogar = new JButton("Jogar");
	private final JLabel mensagem = new JLabel("Marque de 2 a 5 herdeiros");

	/** Retrato do herdeiro: o pedaço esquerdo da imagem da cartela. */
	private static class Retrato extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Image cartela;

		Retrato(Image cartela) {
			this.cartela = cartela;
			setPreferredSize(new Dimension(200, 450));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(cartela, 0, 0, 200, 450, 0, 0, 240, 546, null);
		}
	}

	public JanelaEscolha() {
		super("Herdeiros de Khan - Escolha dos herdeiros");
		Jogo jogo = Jogo.getJogo();
		Imagens imagens = new Imagens();
		JPanel herdeiros = new JPanel(new GridLayout(1, 5, 8, 0));
		for (String cor : jogo.coresDisponiveis()) {
			String herdeiro = jogo.herdeiroDaCor(cor);
			JPanel coluna = new JPanel(new BorderLayout());
			coluna.add(new Retrato(imagens.get("cartelas/" + herdeiro.toUpperCase() + ".jpg")), BorderLayout.CENTER);
			JCheckBox caixa = new JCheckBox(herdeiro + " (" + cor + ")");
			caixa.setBackground(Cores.de(cor));
			caixa.setForeground(Cores.texto(cor));
			caixa.setOpaque(true);
			coluna.add(caixa, BorderLayout.SOUTH);
			herdeiros.add(coluna);
			caixas.add(caixa);
			cores.add(cor);
		}
		jogar.setActionCommand(JOGAR);
		JPanel baixo = new JPanel(new FlowLayout());
		baixo.add(jogar);
		baixo.add(mensagem);
		add(herdeiros, BorderLayout.CENTER);
		add(baixo, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}

	public void adicionarTratadorDoBotao(ActionListener tratador) {
		jogar.addActionListener(tratador);
	}

	public void mostrarMensagem(String texto) {
		mensagem.setText(texto);
	}

	/** Cores marcadas, na ordem das caixas (ordem de jogo). */
	public List<String> coresEscolhidas() {
		List<String> escolhidas = new ArrayList<>();
		for (int i = 0; i < caixas.size(); i++) {
			if (caixas.get(i).isSelected()) {
				escolhidas.add(cores.get(i));
			}
		}
		return escolhidas;
	}
}
