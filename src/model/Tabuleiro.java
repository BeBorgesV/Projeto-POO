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
	/** Conselheiros do mapa, pelo nome da parada onde ficam. */
	private final Map<String, Conselheiro> conselheiros = new HashMap<>();
	/** Peças de cidade viradas para baixo, na ordem em que serão reveladas. */
	private final List<Cidade> pilhaDeCidades = new ArrayList<>();
	/** As 40 peças de tesouro ainda fora do tabuleiro, embaralhadas. */
	private final List<TipoTesouro> sacoDeTesouros = new ArrayList<>();
	private final Random sorteio;

	Tabuleiro(Random sorteio) {
		this.sorteio = sorteio;
		paradas.put(KARAKORUM, new Parada(KARAKORUM, Parada.Tipo.KARAKORUM, null));
		for (TipoTesouro t : TipoTesouro.values()) {
			for (int i = 0; i < TipoTesouro.PECAS_POR_TIPO; i++) {
				sacoDeTesouros.add(t);
			}
		}
		Collections.shuffle(sacoDeTesouros, sorteio);
	}

	// ---------- montagem do mapa (usada pela FabricaTabuleiro) ----------

	/** A região vem da letra do nome: R = Rússia, P = Pérsia, C = China. */
	Parada novaParada(String nome, Parada.Tipo tipo) {
		Regiao regiao = nome.startsWith("R") ? Regiao.RUSSIA : nome.startsWith("C") ? Regiao.CHINA : Regiao.PERSIA;
		Parada p = new Parada(nome, tipo, regiao);
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

	void novoConselheiro(String parada, Conselheiro c) {
		parada(parada);
		conselheiros.put(parada, c);
	}

	// ---------- preparação ----------

	/** Põe uma peça de bônus aleatória em cada conselheiro (manual, p. 4, item 5). */
	void prepararConselheiros() {
		List<PecaBonus> bonus = new ArrayList<>(java.util.Arrays.asList(PecaBonus.values()));
		Collections.shuffle(bonus, sorteio);
		List<String> nomes = new ArrayList<>(conselheiros.keySet());
		Collections.sort(nomes); // ordem fixa, para a semente dos testes ser previsível
		for (int i = 0; i < nomes.size() && i < bonus.size(); i++) {
			conselheiros.get(nomes.get(i)).receberBonus(bonus.get(i));
		}
	}

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

	/** Devolve tesouros ao saco (bônus de tesouro: o jogador fica com 1 dos 3). */
	void devolverTesouros(List<TipoTesouro> tesouros) {
		sacoDeTesouros.addAll(tesouros);
		Collections.shuffle(sacoDeTesouros, sorteio);
	}

	List<TipoTesouro> sortearTesouros(int quantidade) {
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

	List<Parada> paradas() {
		return new ArrayList<>(paradas.values());
	}

	List<Cidade> cidades() {
		return new ArrayList<>(cidades.values());
	}

	List<Provincia> provincias() {
		return new ArrayList<>(provincias.values());
	}

	List<Provincia> provinciasDoKhan() {
		List<Provincia> doKhan = new ArrayList<>();
		for (Provincia p : provincias.values()) {
			if (p.ehDoKhan()) {
				doKhan.add(p);
			}
		}
		return doKhan;
	}

	/** Conselheiro da parada, ou null se ela não tiver. */
	Conselheiro conselheiro(String parada) {
		return conselheiros.get(parada);
	}

	List<String> paradasComConselheiro() {
		return new ArrayList<>(conselheiros.keySet());
	}

	/** Todas as cidades foram conquistadas? (o outro gatilho do fim, manual p. 14) */
	boolean todasAsCidadesConquistadas() {
		for (Cidade c : cidades.values()) {
			if (c.conquistador() == null) {
				return false;
			}
		}
		return true;
	}
}
