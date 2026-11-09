package model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Contagens sobre o mapa e a cartela usadas pelas cartas de conselheiro e
 * pela apuração final. Ficam juntas para as cartas não repetirem código.
 */
final class Contagens {

	private Contagens() {
	}

	/** Yurts do jogador numa região: nas paradas e nas cidades que ele conquistou. */
	static int yurtsNaRegiao(Tabuleiro t, Jogador j, Regiao r) {
		int n = 0;
		for (Parada p : t.paradas()) {
			if (p.regiao() == r) {
				n += p.yurtsDe(j);
			}
		}
		for (Cidade c : t.cidades()) {
			if (c.regiao() == r && c.yurtDe(j)) {
				n++;
			}
		}
		return n;
	}

	static int yurtsNoMapa(Jogador j) {
		return Jogador.YURTS_INICIAIS - j.yurtsDisponiveis();
	}

	static int cidadesConquistadas(Tabuleiro t, Jogador j) {
		int n = 0;
		for (Cidade c : t.cidades()) {
			if (c.conquistador() == j) {
				n++;
			}
		}
		return n;
	}

	/** Quantas áreas (cidades ou províncias) têm pelo menos {@code minimo} yurts do jogador em paradas vizinhas. */
	static int areasComYurtsVizinhos(Tabuleiro t, Jogador j, Collection<? extends AreaDoMapa> areas, int minimo) {
		int n = 0;
		for (AreaDoMapa a : areas) {
			int yurts = 0;
			for (Parada p : t.paradas()) {
				if (a.ehVizinhaDe(p)) {
					yurts += p.yurtsDe(j);
				}
			}
			if (yurts >= minimo) {
				n++;
			}
		}
		return n;
	}

	/** Maior grupo de paradas vizinhas, todas com yurt do jogador (busca em largura). */
	static int maiorRedeDeYurts(Tabuleiro t, Jogador j) {
		Set<Parada> vistas = new HashSet<>();
		int maior = 0;
		for (Parada inicio : t.paradas()) {
			if (inicio.yurtsDe(j) == 0 || !vistas.add(inicio)) {
				continue;
			}
			int tamanho = 0;
			Deque<Parada> fila = new ArrayDeque<>();
			fila.add(inicio);
			while (!fila.isEmpty()) {
				Parada atual = fila.poll();
				tamanho++;
				for (Parada v : atual.vizinhas()) {
					if (v.yurtsDe(j) > 0 && vistas.add(v)) {
						fila.add(v);
					}
				}
			}
			maior = Math.max(maior, tamanho);
		}
		return maior;
	}

	/** Paradas duplas com pelo menos 1 yurt do jogador (Karakorum não conta). */
	static int paradasDuplasComYurt(Tabuleiro t, Jogador j) {
		int n = 0;
		for (Parada p : t.paradas()) {
			if (p.tipo() == Parada.Tipo.DUPLA && p.yurtsDe(j) > 0) {
				n++;
			}
		}
		return n;
	}

	/** Menor número de movimentos de algum peão do jogador até Karakorum (desempate final). */
	static int distanciaAKarakorum(Tabuleiro t, Jogador j) {
		int menor = Integer.MAX_VALUE;
		for (Parada origem : j.posicoes()) {
			Set<Parada> vistas = new HashSet<>();
			Deque<Parada> fila = new ArrayDeque<>();
			Deque<Integer> distancias = new ArrayDeque<>();
			fila.add(origem);
			distancias.add(0);
			vistas.add(origem);
			while (!fila.isEmpty()) {
				Parada atual = fila.poll();
				int d = distancias.poll();
				if (atual == t.karakorum()) {
					menor = Math.min(menor, d);
					break;
				}
				for (Parada v : atual.vizinhas()) {
					if (vistas.add(v)) {
						fila.add(v);
						distancias.add(d + 1);
					}
				}
			}
		}
		return menor;
	}
}
