package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.JogadaInvalidaException;
import model.Jogo;
import view.JanelaDoJogo;

/** Clique no tabuleiro: tenta mover o peão da vez para a parada clicada. */
class TratadorDoTabuleiro extends MouseAdapter {
	private final Jogo jogo;
	private final JanelaDoJogo janela;

	TratadorDoTabuleiro(Jogo jogo, JanelaDoJogo janela) {
		this.jogo = jogo;
		this.janela = janela;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		String parada = janela.paradaEm(e.getX(), e.getY());
		if (parada == null || !jogo.turnoEmAndamento()) {
			return;
		}
		try {
			jogo.mover(parada);
			janela.atualizar();
		} catch (JogadaInvalidaException ex) {
			// deslocamento inválido: nada acontece e a vez não muda
		}
	}
}
