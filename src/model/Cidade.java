package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cidade do mapa, fonte de tesouros (manual, p. 5 e 10).
 *
 * Só as cidades reveladas podem ser atacadas. Quem toma o último tesouro
 * conquista a cidade e coloca um yurt seu no meio dela.
 */
class Cidade implements AreaDoMapa {
	static final int TESOUROS_AO_REVELAR = 4;

	private final String nome;
	private final Regiao regiao;
	private final Set<Parada> paradasVizinhas = new HashSet<>();
	private final List<TipoTesouro> tesouros = new ArrayList<>();
	private boolean revelada;
	private Jogador conquistador;

	Cidade(String nome, Regiao regiao) {
		this.nome = nome;
		this.regiao = regiao;
	}

	String nome() {
		return nome;
	}

	Regiao regiao() {
		return regiao;
	}

	void adicionarParadaVizinha(Parada p) {
		paradasVizinhas.add(p);
	}

	@Override
	public boolean ehVizinhaDe(Parada p) {
		return paradasVizinhas.contains(p);
	}

	/** Revela a cidade com os tesouros sorteados (substitui qualquer tesouro anterior). */
	void revelar(List<TipoTesouro> sorteados) {
		tesouros.clear();
		tesouros.addAll(sorteados);
		revelada = true;
	}

	boolean estaRevelada() {
		return revelada;
	}

	boolean temTesouro(TipoTesouro tipo) {
		return tesouros.contains(tipo);
	}

	int quantidadeDeTesouros() {
		return tesouros.size();
	}

	List<TipoTesouro> tesouros() {
		return new ArrayList<>(tesouros);
	}

	TipoTesouro retirarTesouro(TipoTesouro tipo) {
		if (!tesouros.remove(tipo)) {
			throw new JogadaInvalidaException(nome + " não tem tesouro do tipo " + tipo);
		}
		return tipo;
	}

	boolean foiConquistada() {
		return conquistador != null;
	}

	Jogador conquistador() {
		return conquistador;
	}

	void registrarConquista(Jogador j) {
		conquistador = j;
	}

	@Override
	public String toString() {
		return nome;
	}
}
