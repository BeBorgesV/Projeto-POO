package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Trilha do Kurultai (manual, p. 14).
 *
 * Cada voto ganho avança a ficha do jogador e também a ficha neutra, que marca
 * a lotação do conselho. Quando a ficha neutra atinge o limite para o número de
 * jogadores, o fim da partida é disparado.
 */
class TrilhaKurultai {
	/** Votos da ficha neutra que disparam o fim: 2→10, 3→14, 4→16, 5→17 jogadores. */
	private static final Map<Integer, Integer> LIMITE_POR_JOGADORES = new HashMap<>();
	static {
		LIMITE_POR_JOGADORES.put(2, 10);
		LIMITE_POR_JOGADORES.put(3, 14);
		LIMITE_POR_JOGADORES.put(4, 16);
		LIMITE_POR_JOGADORES.put(5, 17);
	}

	private final Map<Jogador, Integer> votos = new HashMap<>();
	private final int limite;
	private int fichaNeutra;

	TrilhaKurultai(int numeroDeJogadores) {
		Integer l = LIMITE_POR_JOGADORES.get(numeroDeJogadores);
		if (l == null) {
			throw new JogadaInvalidaException("O jogo é para 2 a 5 jogadores");
		}
		limite = l;
	}

	void registrarVotos(Jogador j, int quantidade) {
		votos.merge(j, quantidade, Integer::sum);
		fichaNeutra += quantidade;
	}

	int votosDe(Jogador j) {
		return votos.getOrDefault(j, 0);
	}

	int fichaNeutra() {
		return fichaNeutra;
	}

	int limite() {
		return limite;
	}

	boolean fimDisparado() {
		return fichaNeutra >= limite;
	}
}
