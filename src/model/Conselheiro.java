package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conselheiro impresso no mapa (6 paradas duplas e Karakorum, manual p. 13).
 *
 * Cada entrega do conjunto pedido dá votos (Karakorum dá 2). O primeiro a
 * entregar leva a peça de bônus que está em cima dele.
 */
class Conselheiro {
	/** Quantidade de tesouros quando o conselheiro aceita qualquer tipo. */
	private final int quaisquer;
	private final Map<TipoTesouro, Integer> pedido = new HashMap<>();
	final int votos;
	private PecaBonus bonus;

	/** Conselheiro que pede tesouros específicos (podem repetir). */
	Conselheiro(int votos, TipoTesouro... tesouros) {
		this.votos = votos;
		this.quaisquer = 0;
		for (TipoTesouro t : tesouros) {
			pedido.put(t, pedido.getOrDefault(t, 0) + 1);
		}
	}

	/** Conselheiro que aceita {@code quantidade} tesouros de qualquer tipo. */
	Conselheiro(int votos, int quantidade) {
		this.votos = votos;
		this.quaisquer = quantidade;
	}

	void receberBonus(PecaBonus b) {
		bonus = b;
	}

	PecaBonus bonus() {
		return bonus;
	}

	/** Retira a peça de bônus (só a 1ª entrega a leva). */
	PecaBonus entregarBonus() {
		PecaBonus b = bonus;
		bonus = null;
		return b;
	}

	/** Tesouros de uma entrega: os reais a gastar e, se precisar, 1 tesouro virtual do turno. */
	static class Entrega {
		final List<TipoTesouro> reais = new ArrayList<>();
		TipoTesouro virtual;
	}

	/**
	 * Escolhe os tesouros da entrega a partir do que o jogador tem, podendo
	 * completar com no máximo 1 tesouro virtual do turno. Lança exceção se não
	 * der para entregar.
	 */
	Entrega montarEntrega(Map<TipoTesouro, Integer> doJogador, List<TipoTesouro> virtuais) {
		Map<TipoTesouro, Integer> resto = new HashMap<>(doJogador);
		Entrega entrega = new Entrega();
		List<TipoTesouro> reais = entrega.reais;
		if (quaisquer > 0) {
			// shortcut: escolhe sozinho os tesouros de que o jogador tem mais; trocar por escolha na tela se o grupo quiser
			for (int i = 0; i < quaisquer; i++) {
				TipoTesouro maior = null;
				for (TipoTesouro t : TipoTesouro.values()) {
					if (resto.getOrDefault(t, 0) > 0 && (maior == null || resto.get(t) > resto.get(maior))) {
						maior = t;
					}
				}
				if (maior != null) {
					reais.add(maior);
					resto.put(maior, resto.get(maior) - 1);
				} else if (entrega.virtual == null && !virtuais.isEmpty()) {
					entrega.virtual = virtuais.get(0);
				} else {
					throw new JogadaInvalidaException("Faltam tesouros para esta entrega");
				}
			}
			return entrega;
		}
		for (Map.Entry<TipoTesouro, Integer> e : pedido.entrySet()) {
			for (int i = 0; i < e.getValue(); i++) {
				TipoTesouro t = e.getKey();
				if (resto.getOrDefault(t, 0) > 0) {
					reais.add(t);
					resto.put(t, resto.get(t) - 1);
				} else if (entrega.virtual == null && virtuais.contains(t)) {
					entrega.virtual = t;
				} else {
					throw new JogadaInvalidaException("Faltam tesouros para esta entrega: o conselheiro pede " + descricao());
				}
			}
		}
		return entrega;
	}

	String descricao() {
		if (quaisquer > 0) {
			return quaisquer + " tesouros quaisquer";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<TipoTesouro, Integer> e : pedido.entrySet()) {
			sb.append(sb.length() == 0 ? "" : " + ").append(e.getValue()).append(" ").append(e.getKey());
		}
		return sb.toString();
	}
}
