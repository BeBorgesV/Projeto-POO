package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Jogo;

/**
 * Janela do tabuleiro. O tabuleiro e os peões são desenhados com Java2D; os
 * componentes Swing ficam só na lateral, para o testador escolher quantos
 * movimentos a coluna ativada dá.
 */
public class JanelaDoJogo extends JFrame {
	private static final long serialVersionUID = 1L;

	private final Jogo jogo;
	private final PainelTabuleiro painelTabuleiro;
	private final PainelDaVez painelDaVez;
	private final JComboBox<Integer> movimentos = new JComboBox<>();
	private final JButton botaoAtivar = new JButton("Ativar coluna");
	private final JButton botaoEncerrar = new JButton("Encerrar turno");

	public JanelaDoJogo(Jogo jogo) {
		super("Herdeiros do Khan");
		this.jogo = jogo;
		Imagens imagens = new Imagens();
		painelTabuleiro = new PainelTabuleiro(jogo, imagens);
		painelDaVez = new PainelDaVez(jogo);
		for (int i = 1; i <= 6; i++) {
			movimentos.addItem(i);
		}

		JPanel lateral = new JPanel();
		lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
		lateral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		adicionarNaLateral(lateral, painelDaVez);
		lateral.add(Box.createVerticalStrut(25));
		adicionarNaLateral(lateral, new JLabel("Movimentos da coluna:"));
		movimentos.setMaximumSize(new Dimension(240, 30));
		adicionarNaLateral(lateral, movimentos);
		lateral.add(Box.createVerticalStrut(10));
		adicionarNaLateral(lateral, botaoAtivar);
		lateral.add(Box.createVerticalStrut(10));
		adicionarNaLateral(lateral, botaoEncerrar);

		add(painelTabuleiro, BorderLayout.CENTER);
		add(lateral, BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		atualizar();
	}

	private void adicionarNaLateral(JPanel lateral, JComponent c) {
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		lateral.add(c);
	}

	public void adicionarTratadorDoTabuleiro(MouseListener tratador) {
		painelTabuleiro.addMouseListener(tratador);
	}

	public void adicionarTratadorDeAtivar(ActionListener tratador) {
		botaoAtivar.addActionListener(tratador);
	}

	public void adicionarTratadorDeEncerrar(ActionListener tratador) {
		botaoEncerrar.addActionListener(tratador);
	}

	public String paradaEm(int x, int y) {
		return painelTabuleiro.paradaEm(x, y);
	}

	public int movimentosEscolhidos() {
		return (Integer) movimentos.getSelectedItem();
	}

	/** Redesenha a janela depois de uma jogada. */
	public void atualizar() {
		boolean emTurno = jogo.turnoEmAndamento();
		movimentos.setEnabled(!emTurno);
		botaoAtivar.setEnabled(!emTurno);
		botaoEncerrar.setEnabled(emTurno);
		repaint();
	}
}
