package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Área de melhorias na parte de baixo do tabuleiro (manual, p. 9 e 11).
 *
 * São 5 espaços, da esquerda (perto do Khan) para a direita. O 2º espaço tem
 * o ícone de moeda e o 4º o de espada: se estiverem vazios quando o Khan
 * chega, o jogador ganha essas peças antes da reposição.
 */
class AreaDeMelhorias {
	static final int ESPACOS = 5;
	static final int ESPACO_DA_MOEDA = 1;
	static final int ESPACO_DA_ESPADA = 3;
	/** Posição usada pela API para "a do topo da pilha". */
	static final int PILHA = -1;

	/** Melhoria de cada espaço (0 a 4); espaço vazio não tem chave. */
	private final Map<Integer, Melhoria> espacos = new HashMap<>();
	private final List<Melhoria> pilha = new ArrayList<>();

	AreaDeMelhorias(Random sorteio) {
		pilha.addAll(Melhoria.todas());
		Collections.shuffle(pilha, sorteio);
		repor();
	}

	/** Preenche os espaços vazios com melhorias da pilha. */
	void repor() {
		for (int i = 0; i < ESPACOS; i++) {
			if (!espacos.containsKey(i) && !pilha.isEmpty()) {
				espacos.put(i, pilha.remove(0));
			}
		}
	}

	boolean vazio(int espaco) {
		return !espacos.containsKey(espaco);
	}

	Melhoria ver(int posicao) {
		if (posicao == PILHA) {
			if (pilha.isEmpty()) {
				throw new JogadaInvalidaException("A pilha de melhorias acabou");
			}
			return pilha.get(0);
		}
		Melhoria m = espacos.get(posicao);
		if (m == null) {
			throw new JogadaInvalidaException("Não há melhoria nesse espaço");
		}
		return m;
	}

	/**
	 * Retira a melhoria. Se veio de um espaço, as que estavam à esquerda dele
	 * deslizam uma casa para a direita (não se revelam novas).
	 */
	Melhoria retirar(int posicao) {
		Melhoria m = ver(posicao);
		if (posicao == PILHA) {
			pilha.remove(0);
			return m;
		}
		for (int i = posicao; i > 0; i--) {
			Melhoria daEsquerda = espacos.get(i - 1);
			if (daEsquerda == null) {
				espacos.remove(i);
			} else {
				espacos.put(i, daEsquerda);
			}
		}
		espacos.remove(0);
		return m;
	}

	/** Tira do topo da pilha (melhorias iniciais do Jochi). */
	Melhoria sortear() {
		return pilha.isEmpty() ? null : pilha.remove(0);
	}

	/** Ids das melhorias nos 5 espaços; "" para espaço vazio. */
	List<String> ids() {
		List<String> ids = new ArrayList<>();
		for (int i = 0; i < ESPACOS; i++) {
			Melhoria m = espacos.get(i);
			ids.add(m == null ? "" : m.id);
		}
		return ids;
	}

	int naPilha() {
		return pilha.size();
	}
}
