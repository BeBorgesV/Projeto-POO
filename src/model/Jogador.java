package model;

import java.util.HashMap;
import java.util.Map;

/** Um herdeiro do Khan: peão, peças de tributo, tesouros e yurts guardados. */
class Jogador {
	static final int YURTS_INICIAIS = 12;

	private final String cor;
	private Parada posicao;
	private final Map<TipoTributo, Integer> tributos = new HashMap<>();
	private final Map<TipoTesouro, Integer> tesouros = new HashMap<>();
	private int yurtsDisponiveis = YURTS_INICIAIS;

	// moedasIniciais: 1 para o 1º e o 2º a jogar, 2 para os demais
	Jogador(String cor, int moedasIniciais) {
		this.cor = cor;
		for (TipoTributo t : TipoTributo.values()) {
			tributos.put(t, 0);
		}
		tributos.put(TipoTributo.MOEDA, moedasIniciais);
	}

	String cor() {
		return cor;
	}

	// ---------- posição do peão ----------

	Parada posicao() {
		return posicao;
	}

	/** Tira o peão da parada atual e o coloca em {@code destino}. */
	void posicionarEm(Parada destino) {
		if (posicao != null) {
			posicao.removerPeao(this);
		}
		posicao = destino;
		destino.adicionarPeao(this);
	}

	// ---------- peças de tributo ----------

	int tributos(TipoTributo tipo) {
		return tributos.get(tipo);
	}

	void receberTributo(TipoTributo tipo, int quantidade) {
		tributos.put(tipo, tributos.get(tipo) + quantidade);
	}

	void gastarTributo(TipoTributo tipo, int quantidade) {
		int atual = tributos.get(tipo);
		if (atual < quantidade) {
			throw new JogadaInvalidaException(cor + " precisa de " + quantidade + " " + tipo + " e só tem " + atual);
		}
		tributos.put(tipo, atual - quantidade);
	}

	// ---------- tesouros ----------

	void receberTesouro(TipoTesouro tipo) {
		tesouros.put(tipo, tesouros(tipo) + 1);
	}

	int tesouros(TipoTesouro tipo) {
		return tesouros.getOrDefault(tipo, 0);
	}

	int totalDeTesouros() {
		int total = 0;
		for (int q : tesouros.values()) {
			total += q;
		}
		return total;
	}

	// ---------- yurts guardados (as 12 peças da cor do jogador) ----------

	int yurtsDisponiveis() {
		return yurtsDisponiveis;
	}

	void usarYurt() {
		if (yurtsDisponiveis == 0) {
			throw new JogadaInvalidaException(cor + " não tem mais yurts para colocar");
		}
		yurtsDisponiveis--;
	}

	@Override
	public String toString() {
		return cor;
	}
}
