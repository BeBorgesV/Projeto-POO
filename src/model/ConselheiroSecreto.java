package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * As 10 cartas de conselheiro secreto (manual, p. 19). No fim, cada limite
 * atingido vale 1 voto (0 a 3).
 *
 * Padrão Strategy (Cap. 17): cada carta traz o seu próprio jeito de medir
 * ({@link #medir}). Uma carta nova entra sem mexer na apuração (aberto/fechado).
 */
enum ConselheiroSecreto {
	VIAJANTE(3, 6, 8) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			return Contagens.maiorRedeDeYurts(t, j);
		}
	},
	SOLDADO(4, 7, 10) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			return j.cartela().todasAsMelhorias().size();
		}
	},
	COLECIONADOR(4, 6, 7) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			Set<String> simbolos = new HashSet<>();
			for (Melhoria m : j.cartela().todasAsMelhorias()) {
				simbolos.add(m.simbolo());
			}
			return simbolos.size();
		}
	},
	SACERDOTE(1, 2, 4) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			Map<String, Integer> porSimbolo = new HashMap<>();
			for (Melhoria m : j.cartela().todasAsMelhorias()) {
				porSimbolo.put(m.simbolo(), porSimbolo.getOrDefault(m.simbolo(), 0) + 1);
			}
			int pares = 0;
			for (int q : porSimbolo.values()) {
				pares += q / 2;
			}
			return pares;
		}
	},
	GOVERNADOR(1, 2, 3) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			int regioes = 0;
			for (Regiao r : Regiao.values()) {
				if (Contagens.yurtsNaRegiao(t, j, r) >= 2
						&& j.cartela().melhorias(Coluna.daRegiao(r)).size() >= 2) {
					regioes++;
				}
			}
			return regioes;
		}
	},
	BAJULADOR(2, 3, 4) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			return Contagens.areasComYurtsVizinhos(t, j, t.provinciasDoKhan(), 2);
		}
	},
	PREFEITO(2, 3, 4) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			return Contagens.areasComYurtsVizinhos(t, j, t.cidades(), 2);
		}
	},
	CIDADAO(3, 6, 8) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			return Contagens.areasComYurtsVizinhos(t, j, t.cidades(), 1);
		}
	},
	CAVALEIRO(2, 4, 5) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			int n = 0;
			for (Melhoria m : j.cartela().todasAsMelhorias()) {
				if (m.acao == Acao.CAVALO || m.acao == Acao.PUNHO) {
					n++;
				}
			}
			return n;
		}
	},
	MENSAGEIRO(2, 4, 6) {
		@Override
		int medir(Tabuleiro t, Jogador j) {
			return Contagens.paradasDuplasComYurt(t, j);
		}
	};

	/** Quanto é preciso medir para ganhar 1, 2 e 3 votos. */
	private final List<Integer> limites;

	ConselheiroSecreto(int umVoto, int doisVotos, int tresVotos) {
		this.limites = Arrays.asList(umVoto, doisVotos, tresVotos);
	}

	abstract int medir(Tabuleiro t, Jogador j);

	/** Votos no fim: 1 por limite atingido. */
	int votos(Tabuleiro t, Jogador j) {
		int medida = medir(t, j);
		int votos = 0;
		for (int limite : limites) {
			if (medida >= limite) {
				votos++;
			}
		}
		return votos;
	}
}
