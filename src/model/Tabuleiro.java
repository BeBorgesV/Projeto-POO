package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** O tabuleiro principal: paradas, rotas, províncias, cidades e o saco de tesouros. */
class Tabuleiro {
	static final String KARAKORUM = "Karakorum";

	private final Map<String, Parada> paradas = new HashMap<>();
	private final Map<String, Provincia> provincias = new HashMap<>();
	private final Map<String, Cidade> cidades = new HashMap<>();
	/** Peças de cidade viradas para baixo, na ordem em que serão reveladas. */
	private final List<Cidade> pilhaDeCidades = new ArrayList<>();
	/** As 40 peças de tesouro ainda fora do tabuleiro, embaralhadas. */
	private final List<TipoTesouro> sacoDeTesouros = new ArrayList<>();
	private final Random sorteio;

	Tabuleiro(Random sorteio) {
		this.sorteio = sorteio;
		paradas.put(KARAKORUM, new Parada(KARAKORUM, Parada.Tipo.KARAKORUM));
		for (TipoTesouro t : TipoTesouro.values()) {
			for (int i = 0; i < TipoTesouro.PECAS_POR_TIPO; i++) {
				sacoDeTesouros.add(t);
			}
		}
		Collections.shuffle(sacoDeTesouros, sorteio);
	}

	// ---------- montagem do mapa (usada pela FabricaTabuleiro) ----------

	Parada novaParada(String nome, Parada.Tipo tipo) {
		Parada p = new Parada(nome, tipo);
		paradas.put(nome, p);
		return p;
	}

	void ligar(String a, String b) {
		parada(a).ligarA(parada(b));
	}

	Provincia novaProvincia(String nome, Regiao regiao, TipoTributo tipo, String... paradasVizinhas) {
		Provincia p = new Provincia(nome, regiao, tipo);
		for (String n : paradasVizinhas) {
			p.adicionarParadaVizinha(parada(n));
		}
		provincias.put(nome, p);
		return p;
	}

	Cidade novaCidade(String nome, Regiao regiao, String... paradasVizinhas) {
		Cidade c = new Cidade(nome, regiao);
		for (String n : paradasVizinhas) {
			c.adicionarParadaVizinha(parada(n));
		}
		cidades.put(nome, c);
		return c;
	}

	// ---------- preparação ----------

	/** Embaralha as peças de cidade e revela as 3 primeiras, cada uma com 4 tesouros. */
	void prepararCidades() {
		pilhaDeCidades.clear();
		pilhaDeCidades.addAll(cidades.values());
		Collections.shuffle(pilhaDeCidades, sorteio);
		for (int i = 0; i < 3; i++) {
			revelarProximaCidade();
		}
	}

	/** Coloca 1 peça de tributo em cada província. */
	void prepararProvincias() {
		for (Provincia p : provincias.values()) {
			p.receberTributo();
		}
	}

	/** Revela a próxima cidade da pilha. Se a pilha acabou, não faz nada. */
	Cidade revelarProximaCidade() {
		if (pilhaDeCidades.isEmpty()) {
			return null;
		}
		Cidade c = pilhaDeCidades.remove(0);
		c.revelar(sortearTesouros(Cidade.TESOUROS_AO_REVELAR));
		return c;
	}

	/** Revela uma cidade específica com tesouros escolhidos, tirando-a da pilha (usado nos testes). */
	void revelarCidade(String nome, List<TipoTesouro> tesouros) {
		Cidade c = cidade(nome);
		pilhaDeCidades.remove(c);
		c.revelar(tesouros);
	}

	private List<TipoTesouro> sortearTesouros(int quantidade) {
		List<TipoTesouro> sorteados = new ArrayList<>();
		for (int i = 0; i < quantidade && !sacoDeTesouros.isEmpty(); i++) {
			sorteados.add(sacoDeTesouros.remove(0));
		}
		return sorteados;
	}

	// ---------- consultas ----------

	Parada karakorum() {
		return parada(KARAKORUM);
	}

	Parada parada(String nome) {
		Parada p = paradas.get(nome);
		if (p == null) {
			throw new JogadaInvalidaException("Parada inexistente: " + nome);
		}
		return p;
	}

	Provincia provincia(String nome) {
		Provincia p = provincias.get(nome);
		if (p == null) {
			throw new JogadaInvalidaException("Província inexistente: " + nome);
		}
		return p;
	}

	Cidade cidade(String nome) {
		Cidade c = cidades.get(nome);
		if (c == null) {
			throw new JogadaInvalidaException("Cidade inexistente: " + nome);
		}
		return c;
	}

	List<Cidade> cidadesReveladas() {
		List<Cidade> reveladas = new ArrayList<>();
		for (Cidade c : cidades.values()) {
			if (c.estaRevelada()) {
				reveladas.add(c);
			}
		}
		return reveladas;
	}

	int cidadesNaPilha() {
		return pilhaDeCidades.size();
	}
}
