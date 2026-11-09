package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Um ponto de parada nas rotas do mapa.
 *
 * Parada simples: 1 peão e 1 yurt. Parada dupla (onde ficam os conselheiros):
 * 2 peões e até 2 yurts. Karakorum: todos os peões e nenhum yurt.
 */
class Parada {

	enum Tipo {
		SIMPLES(1, 1), DUPLA(2, 2), KARAKORUM(Integer.MAX_VALUE, 0);

		final int maxPeoes;
		final int maxYurts;

		Tipo(int maxPeoes, int maxYurts) {
			this.maxPeoes = maxPeoes;
			this.maxYurts = maxYurts;
		}
	}

	private final String nome;
	private final Tipo tipo;
	/** Região da parada; null em Karakorum (não pertence a nenhuma região). */
	private final Regiao regiao;
	private final Set<Parada> vizinhas = new HashSet<>();
	private final List<Jogador> peoes = new ArrayList<>();
	private final List<Jogador> donosDosYurts = new ArrayList<>();

	Parada(String nome, Tipo tipo, Regiao regiao) {
		this.nome = nome;
		this.tipo = tipo;
		this.regiao = regiao;
	}

	Regiao regiao() {
		return regiao;
	}

	String nome() {
		return nome;
	}

	Tipo tipo() {
		return tipo;
	}

	/** Cria uma rota nos dois sentidos entre esta parada e {@code outra}. */
	void ligarA(Parada outra) {
		vizinhas.add(outra);
		outra.vizinhas.add(this);
	}

	boolean ehVizinhaDe(Parada outra) {
		return vizinhas.contains(outra);
	}

	Set<Parada> vizinhas() {
		return Collections.unmodifiableSet(vizinhas);
	}

	// ---------- peões ----------

	void adicionarPeao(Jogador j) {
		peoes.add(j);
	}

	void removerPeao(Jogador j) {
		peoes.remove(j);
	}

	/** Um jogador só pode terminar o movimento aqui se houver lugar para o seu peão. */
	boolean cabePeaoDe(Jogador j) {
		int outros = 0;
		for (Jogador p : peoes) {
			if (p != j) {
				outros++;
			}
		}
		return outros < tipo.maxPeoes;
	}

	// ---------- yurts ----------

	boolean temYurtDe(Jogador j) {
		return donosDosYurts.contains(j);
	}

	boolean cabeYurt() {
		return donosDosYurts.size() < tipo.maxYurts;
	}

	void colocarYurt(Jogador dono) {
		if (tipo == Tipo.KARAKORUM) {
			throw new JogadaInvalidaException("Não se pode construir yurts em Karakorum");
		}
		if (!cabeYurt()) {
			throw new JogadaInvalidaException("A parada " + nome + " já tem yurt");
		}
		donosDosYurts.add(dono);
	}

	int quantidadeDeYurts() {
		return donosDosYurts.size();
	}

	int yurtsDe(Jogador j) {
		int n = 0;
		for (Jogador dono : donosDosYurts) {
			if (dono == j) {
				n++;
			}
		}
		return n;
	}

	List<Jogador> donosDosYurts() {
		return Collections.unmodifiableList(donosDosYurts);
	}

	/** Habilidade do Tolui: o único yurt que pode ficar em Karakorum. */
	void colocarYurtDoTolui(Jogador tolui) {
		donosDosYurts.add(tolui);
	}

	@Override
	public String toString() {
		return nome;
	}
}
