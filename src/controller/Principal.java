package controller;

import model.Jogo;
import view.JanelaDoJogo;

/**
 * Ponto de entrada do programa.
 *
 * As janelas iniciais (nova partida e escolha dos herdeiros) ainda não
 * existem, então a partida começa direto com 4 jogadores.
 */
public class Principal {

	public static void main(String[] args) {
		Jogo jogo = new Jogo();
		jogo.iniciarPartida("Azul", "Amarelo", "Vermelho", "Verde");

		JanelaDoJogo janela = new JanelaDoJogo(jogo);
		janela.adicionarTratadorDoTabuleiro(new TratadorDoTabuleiro(jogo, janela));
		janela.adicionarTratadorDeAtivar(new TratadorDeAtivarColuna(jogo, janela));
		janela.adicionarTratadorDeEncerrar(new TratadorDeEncerrarTurno(jogo, janela));
		janela.setVisible(true);
	}
}
