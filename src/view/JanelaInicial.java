package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 1ª janela (2ª iteração, Figura 1): começar uma partida nova ou continuar
 * uma salva. Salvar e carregar ficam para a 4ª iteração, então "Continuar"
 * ainda fica desabilitado.
 */
public class JanelaInicial extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final String NOVO_JOGO = "NOVO_JOGO";
	public static final String CONTINUAR = "CONTINUAR";

	private final JButton novo = new JButton("Novo jogo");
	private final JButton continuar = new JButton("Continuar");

	/** Fundo com a imagem do tabuleiro, desenhada com drawImage(). */
	private static class Fundo extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Image imagem = new Imagens().get("tabuleiro.jpg");

		Fundo() {
			setPreferredSize(new Dimension(700, 700));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(imagem, 0, 0, getWidth(), getHeight(), null);
		}
	}

	public JanelaInicial() {
		super("Herdeiros de Khan - Início");
		novo.setActionCommand(NOVO_JOGO);
		continuar.setActionCommand(CONTINUAR);
		continuar.setEnabled(false);
		continuar.setToolTipText("Salvar e continuar partidas chega na 4ª iteração");
		JPanel botoes = new JPanel(new FlowLayout());
		botoes.add(novo);
		botoes.add(continuar);
		add(new Fundo(), BorderLayout.CENTER);
		add(botoes, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}

	public void adicionarTratadorDosBotoes(ActionListener tratador) {
		novo.addActionListener(tratador);
		continuar.addActionListener(tratador);
	}
}
