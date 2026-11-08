package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Um herdeiro do Khan na partida: peão (ou peões, no caso da Altani), peças
 * de tributo, tesouros, yurts guardados, a cartela com as colunas, as peças
 * de bônus e as cartas de conselheiro secreto.
 */
class Jogador {
	static final int YURTS_INICIAIS = 12;

	private final String cor;
	private final Herdeiro herdeiro;
	private final CartelaDoJogador cartela;
	/** Posição de cada peão; só a Altani tem 2. */
	private final List<Parada> posicoes = new ArrayList<>();
	private int peaoAtivo;
	private final Map<TipoTributo, Integer> tributos = new HashMap<>();
	private final Map<TipoTesouro, Integer> tesouros = new HashMap<>();
	private int yurtsDisponiveis = YURTS_INICIAIS;
	private int bonusObtidos;
	private final List<PecaBonus> bonusGuardados = new ArrayList<>();
	private final List<ConselheiroSecreto> secretos = new ArrayList<>();

	// moedasIniciais: 1 para o 1º e o 2º a jogar, 2 para os demais
	Jogador(String cor, int moedasIniciais) {
		this.cor = cor;
		this.herdeiro = Herdeiro.daCor(cor);
		this.cartela = new CartelaDoJogador(herdeiro);
		for (TipoTributo t : TipoTributo.values()) {
			tributos.put(t, 0);
		}
		tributos.put(TipoTributo.MOEDA, moedasIniciais);
	}

	String cor() {
		return cor;
	}

	Herdeiro herdeiro() {
		return herdeiro;
	}

	CartelaDoJogador cartela() {
		return cartela;
	}

	// ---------- peões ----------

	/** Posição do peão que está jogando. */
	Parada posicao() {
		return posicoes.isEmpty() ? null : posicoes.get(peaoAtivo);
	}

	List<Parada> posicoes() {
		return new ArrayList<>(posicoes);
	}

	int peaoAtivo() {
		return peaoAtivo;
	}

	/** Coloca todos os peões do herdeiro numa parada (início da partida). */
	void posicionarTodosEm(Parada inicio) {
		for (int i = 0; i < herdeiro.peoes(); i++) {
			posicoes.add(inicio);
			inicio.adicionarPeao(this);
		}
	}

	/** Tira o peão ativo da parada atual e o coloca em {@code destino}. */
	void posicionarEm(Parada destino) {
		if (posicoes.isEmpty()) {
			posicoes.add(destino);
		} else {
			posicoes.get(peaoAtivo).removerPeao(this);
			posicoes.set(peaoAtivo, destino);
		}
		destino.adicionarPeao(this);
	}

	/** Habilidade da Altani: escolhe qual dos 2 peões vai jogar. */
	void escolherPeao(int indice) {
		if (indice < 0 || indice >= posicoes.size()) {
			throw new JogadaInvalidaException(cor + " não tem o peão " + (indice + 1));
		}
		peaoAtivo = indice;
	}

	// ---------- peças de tributo ----------

	int tributos(TipoTributo tipo) {
		return tributos.get(tipo);
	}

	void receberTributo(TipoTributo tipo, int quantidade) {
		tributos.put(tipo, tributos.get(tipo) + quantidade);
	}

	void gastarTributo(TipoTributo tipo, int quantidade) {
		int atual = tributos.get(tipo);
		if (atual < quantidade) {
			throw new JogadaInvalidaException(cor + " precisa de " + quantidade + " " + tipo + " e só tem " + atual);
		}
		tributos.put(tipo, atual - quantidade);
	}

	// ---------- tesouros ----------

	void receberTesouro(TipoTesouro tipo) {
		tesouros.put(tipo, tesouros(tipo) + 1);
	}

	int tesouros(TipoTesouro tipo) {
		return tesouros.getOrDefault(tipo, 0);
	}

	Map<TipoTesouro, Integer> tesouros() {
		return new HashMap<>(tesouros);
	}

	void gastarTesouros(List<TipoTesouro> entregues) {
		for (TipoTesouro t : entregues) {
			if (tesouros(t) == 0) {
				throw new JogadaInvalidaException(cor + " não tem tesouro do tipo " + t);
			}
			tesouros.put(t, tesouros(t) - 1);
		}
	}

	int totalDeTesouros() {
		int total = 0;
		for (int q : tesouros.values()) {
			total += q;
		}
		return total;
	}

	// ---------- yurts guardados (as 12 peças da cor do jogador) ----------

	int yurtsDisponiveis() {
		return yurtsDisponiveis;
	}

	void usarYurt() {
		if (yurtsDisponiveis == 0) {
			throw new JogadaInvalidaException(cor + " não tem mais yurts para colocar");
		}
		yurtsDisponiveis--;
	}

	// ---------- peças de bônus ----------

	void registrarBonus(PecaBonus b) {
		bonusObtidos++;
		if (b.deTurno) {
			bonusGuardados.add(b);
		}
	}

	int bonusObtidos() {
		return bonusObtidos;
	}

	List<PecaBonus> bonusGuardados() {
		return new ArrayList<>(bonusGuardados);
	}

	void gastarBonus(PecaBonus b) {
		if (!bonusGuardados.remove(b)) {
			throw new JogadaInvalidaException(cor + " não tem a peça de bônus " + b);
		}
	}

	// ---------- conselheiro secreto ----------

	void receberSecretos(List<ConselheiroSecreto> cartas) {
		secretos.addAll(cartas);
	}

	List<ConselheiroSecreto> secretos() {
		return new ArrayList<>(secretos);
	}

	boolean precisaEscolherSecreto() {
		return secretos.size() > 1;
	}

	/** Fica com uma das 2 cartas recebidas e descarta a outra. */
	void escolherSecreto(int indice) {
		if (!precisaEscolherSecreto()) {
			throw new JogadaInvalidaException(cor + " já escolheu o conselheiro secreto");
		}
		if (indice < 0 || indice >= secretos.size()) {
			throw new JogadaInvalidaException("Carta de conselheiro secreto inválida");
		}
		ConselheiroSecreto escolhido = secretos.get(indice);
		secretos.clear();
		secretos.add(escolhido);
	}

	@Override
	public String toString() {
		return cor;
	}
}
