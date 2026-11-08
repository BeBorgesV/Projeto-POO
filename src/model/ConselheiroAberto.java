package model;

import java.util.HashSet;
import java.util.Set;

/**
 * As 14 cartas de conselheiro aberto (manual, p. 18). 4 são reveladas por
 * partida; o primeiro a cumprir a demanda ganha 1 voto e a carta sai do jogo.
 * "Ter" quer dizer só possuir: nada é gasto.
 *
 * Padrão Strategy (Cap. 17): cada carta traz a sua condição
 * ({@link #cumprida}). O turno entra para contar os recursos virtuais das
 * melhorias (espada e tesouro virtuais).
 */
enum ConselheiroAberto {
	ESPADAS_4 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			int virtuais = turno == null ? 0 : turno.espadasVirtuais();
			return j.tributos(TipoTributo.ESPADA) + virtuais >= 4;
		}
	},
	YURTS_6 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return Contagens.yurtsNoMapa(j) >= 6;
		}
	},
	YURTS_10 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return Contagens.yurtsNoMapa(j) >= 10;
		}
	},
	TESOUROS_4 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			int virtuais = turno == null ? 0 : turno.tesourosVirtuais().size();
			return j.totalDeTesouros() + virtuais >= 4;
		}
	},
	TESOUROS_DIFERENTES_5 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			Set<TipoTesouro> tipos = new HashSet<>();
			for (TipoTesouro tipo : TipoTesouro.values()) {
				if (j.tesouros(tipo) > 0) {
					tipos.add(tipo);
				}
			}
			if (turno != null) {
				tipos.addAll(turno.tesourosVirtuais());
			}
			return tipos.size() >= 5;
		}
	},
	YURT_EM_CADA_REGIAO {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return minimoPorRegiao(t, j) >= 1;
		}
	},
	DOIS_YURTS_EM_CADA_REGIAO {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return minimoPorRegiao(t, j) >= 2;
		}
	},
	BONUS_2 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return j.bonusObtidos() >= 2;
		}
	},
	BONUS_3 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return j.bonusObtidos() >= 3;
		}
	},
	CIDADES_2 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return Contagens.cidadesConquistadas(t, j) >= 2;
		}
	},
	CIDADES_3 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return Contagens.cidadesConquistadas(t, j) >= 3;
		}
	},
	LINHA_DE_MELHORIAS {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			return j.cartela().linhasCompletas() >= 1;
		}
	},
	DUAS_COLUNAS_COM_2 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			int colunas = 0;
			for (Coluna c : Coluna.values()) {
				if (j.cartela().melhorias(c).size() >= 2) {
					colunas++;
				}
			}
			return colunas >= 2;
		}
	},
	COLUNA_COM_4 {
		@Override
		boolean cumprida(Tabuleiro t, Jogador j, Turno turno) {
			for (Coluna c : Coluna.values()) {
				if (j.cartela().melhorias(c).size() >= 4) {
					return true;
				}
			}
			return false;
		}
	};

	abstract boolean cumprida(Tabuleiro t, Jogador j, Turno turno);

	private static int minimoPorRegiao(Tabuleiro t, Jogador j) {
		int minimo = Integer.MAX_VALUE;
		for (Regiao r : Regiao.values()) {
			minimo = Math.min(minimo, Contagens.yurtsNaRegiao(t, j, r));
		}
		return minimo;
	}
}
