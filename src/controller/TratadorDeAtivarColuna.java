package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Jogo;
import view.JanelaDoJogo;

/**
 * Começa o turno com os movimentos escolhidos pelo testador.
 * Por enquanto a coluna não dá ações de tributo nem o Khan.
 */
class TratadorDeAtivarColuna implements ActionListener {
	private final Jogo jogo;
	private final JanelaDoJogo janela;

	TratadorDeAtivarColuna(Jogo jogo, JanelaDoJogo janela) {
		this.jogo = jogo;
		this.janela = janela;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		jogo.iniciarTurno(janela.movimentosEscolhidos(), 0, false);
		janela.atualizar();
	}
}
