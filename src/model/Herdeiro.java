package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Os 5 herdeiros (tabuleiros de jogador). Cada um tem uma cor, uma habilidade
 * e as ações iniciais das colunas, que mudam de tabuleiro para tabuleiro
 * (transcritas das imagens das cartelas).
 *
 * Habilidades (texto das cartelas):
 * - Altani: tem 2 peões; todo turno escolhe um para mover e fazer ações.
 * - Jochi: começa com 2 melhorias ao acaso, uma na coluna branca; não precisa
 *   seguir a cor das melhorias.
 * - Chagatai: 1x por turno, pode deixar de usar 1 cavalo para ganhar 1 espada.
 * - Tolui: começa com 1 yurt em Karakorum; pode pôr yurts em paradas
 *   adjacentes a ele.
 * - Ogedei: 1x por turno, pode pagar 1 moeda para pegar um tesouro de
 *   qualquer cidade (menos o último tesouro dela).
 */
enum Herdeiro {
	ALTANI("Verde", Base.K, Base.L, Base.M),
	JOCHI("Preto", Base.M, Base.K, Base.L),
	CHAGATAI("Vermelho", Base.K, Base.M, Base.L),
	TOLUI("Amarelo", Base.M, Base.L, Base.K),
	OGEDEI("Azul", Base.L, Base.M, Base.K);

	/** Os 3 conjuntos de ações iniciais das colunas coloridas e o da branca. */
	private static final class Base {
		static final List<Acao> K = Arrays.asList(Acao.CAVALO, Acao.PUNHO, Acao.PUNHO, Acao.KHAN);
		static final List<Acao> L = Arrays.asList(Acao.CAVALO, Acao.CAVALO, Acao.PUNHO);
		static final List<Acao> M = Arrays.asList(Acao.CAVALO, Acao.PUNHO, Acao.MOEDA);
		static final List<Acao> BRANCA = Arrays.asList(Acao.CAVALO, Acao.CAVALO, Acao.KHAN, Acao.RECUPERAR);
	}

	final String cor;
	private final Map<Coluna, List<Acao>> acoesIniciais = new HashMap<>();

	Herdeiro(String cor, List<Acao> vermelha, List<Acao> verde, List<Acao> amarela) {
		this.cor = cor;
		acoesIniciais.put(Coluna.VERMELHA, vermelha);
		acoesIniciais.put(Coluna.VERDE, verde);
		acoesIniciais.put(Coluna.AMARELA, amarela);
		acoesIniciais.put(Coluna.BRANCA, Base.BRANCA);
	}

	List<Acao> acoesIniciais(Coluna c) {
		return Collections.unmodifiableList(acoesIniciais.get(c));
	}

	int peoes() {
		return this == ALTANI ? 2 : 1;
	}

	/** Herdeiro associado a uma cor (sem diferenciar maiúsculas). */
	static Herdeiro daCor(String cor) {
		for (Herdeiro h : values()) {
			if (h.cor.equalsIgnoreCase(cor)) {
				return h;
			}
		}
		throw new JogadaInvalidaException("Não há herdeiro da cor " + cor);
	}
}
