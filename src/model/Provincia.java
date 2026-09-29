package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Área entre as rotas onde ficam as peças de tributo (manual, p. 5).
 *
 * Cada província tem um ícone que diz qual tipo de tributo ela recebe.
 * As províncias do Khan têm setas apontando para 2 províncias vizinhas,
 * que também recebem tributo quando o Khan chega (manual, p. 9).
 */
class Provincia implements AreaDoMapa {
	static final int MAX_PECAS = 3;

	private final String nome;
	private final Regiao regiao;
	private final TipoTributo tipo;
	private final Set<Parada> paradasVizinhas = new HashSet<>();
	private final List<Provincia> ligadasPeloKhan = new ArrayList<>();
	private boolean doKhan;
	private int pecas;

	Provincia(String nome, Regiao regiao, TipoTributo tipo) {
		this.nome = nome;
		this.regiao = regiao;
		this.tipo = tipo;
	}

	String nome() {
		return nome;
	}

	Regiao regiao() {
		return regiao;
	}

	TipoTributo tipo() {
		return tipo;
	}

	int pecas() {
		return pecas;
	}

	void adicionarParadaVizinha(Parada p) {
		paradasVizinhas.add(p);
	}

	@Override
	public boolean ehVizinhaDe(Parada p) {
		return paradasVizinhas.contains(p);
	}

	/** Marca esta província como província do Khan, com as 2 províncias apontadas pelas setas. */
	void tornarProvinciaDoKhan(Provincia seta1, Provincia seta2) {
		doKhan = true;
		ligadasPeloKhan.clear();
		ligadasPeloKhan.add(seta1);
		ligadasPeloKhan.add(seta2);
	}

	boolean ehDoKhan() {
		return doKhan;
	}

	List<Provincia> ligadasPeloKhan() {
		return Collections.unmodifiableList(ligadasPeloKhan);
	}

	/** Coloca 1 peça de tributo, respeitando o máximo de 3 por província. */
	void receberTributo() {
		if (pecas < MAX_PECAS) {
			pecas++;
		}
	}

	/** Retira 1 peça, que vai para o jogador. */
	TipoTributo retirarTributo() {
		if (pecas == 0) {
			throw new JogadaInvalidaException("A província " + nome + " está vazia");
		}
		pecas--;
		return tipo;
	}

	@Override
	public String toString() {
		return nome;
	}
}
