package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.JogadaInvalidaException;
import model.Jogo;
import view.JanelaDoJogo;

/** Passa a vez para o próximo jogador. */
class TratadorDeEncerrarTurno implements ActionListener {
	private final Jogo jogo;
	private final JanelaDoJogo janela;

	TratadorDeEncerrarTurno(Jogo jogo, JanelaDoJogo janela) {
		this.jogo = jogo;
		this.janela = janela;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			jogo.encerrarTurno();
			janela.atualizar();
		} catch (JogadaInvalidaException ex) {
			// o peão parou numa parada ocupada: precisa se mover antes de passar a vez
		}
	}
}
