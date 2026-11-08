package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tabuleiro do jogador ("cartela"): as 4 colunas com as ações iniciais do
 * herdeiro, as melhorias compradas e as peças de ativação já usadas.
 *
 * Colunas coloridas: preenchidas de baixo para cima, até 4 melhorias.
 * Coluna branca: o espaço N só recebe a melhoria grátis de quem completar
 * a linha N das colunas coloridas (manual, p. 12).
 */
class CartelaDoJogador {
	private final Herdeiro herdeiro;
	private final Map<Coluna, List<Melhoria>> coloridas = new HashMap<>();
	private final Melhoria[] branca = new Melhoria[Coluna.ESPACOS];
	private final Set<Coluna> usadas = new HashSet<>();
	/** Linhas completas cuja melhoria grátis ainda não foi pega. */
	private final List<Integer> linhasPendentes = new ArrayList<>();

	CartelaDoJogador(Herdeiro herdeiro) {
		this.herdeiro = herdeiro;
		for (Coluna c : Coluna.values()) {
			if (c.colorida()) {
				coloridas.put(c, new ArrayList<Melhoria>());
			}
		}
	}

	// ---------- peças de ativação ----------

	boolean livre(Coluna c) {
		return !usadas.contains(c);
	}

	/** Coloca a peça de ativação e devolve as ações da coluna (iniciais + melhorias). */
	List<Acao> ativar(Coluna c) {
		if (!livre(c)) {
			throw new JogadaInvalidaException("A coluna " + c + " já tem uma peça de ativação");
		}
		usadas.add(c);
		List<Acao> acoes = new ArrayList<>(herdeiro.acoesIniciais(c));
		for (Melhoria m : melhorias(c)) {
			acoes.addAll(m.acoes());
		}
		if (acoes.contains(Acao.RECUPERAR)) {
			usadas.clear(); // inclusive a peça que acabou de ser usada
		}
		return acoes;
	}

	/** Tesouros virtuais das melhorias de uma coluna. */
	List<TipoTesouro> tesourosVirtuais(Coluna c) {
		List<TipoTesouro> t = new ArrayList<>();
		for (Melhoria m : melhorias(c)) {
			if (m.tesouro != null) {
				t.add(m.tesouro);
			}
		}
		return t;
	}

	Set<Coluna> usadas() {
		return new HashSet<>(usadas);
	}

	// ---------- melhorias ----------

	/** Melhorias de uma coluna, de baixo para cima. */
	List<Melhoria> melhorias(Coluna c) {
		if (c.colorida()) {
			return new ArrayList<>(coloridas.get(c));
		}
		List<Melhoria> m = new ArrayList<>();
		for (Melhoria b : branca) {
			if (b != null) {
				m.add(b);
			}
		}
		return m;
	}

	List<Melhoria> todasAsMelhorias() {
		List<Melhoria> todas = new ArrayList<>();
		for (Coluna c : Coluna.values()) {
			todas.addAll(melhorias(c));
		}
		return todas;
	}

	boolean cabeEm(Coluna c) {
		return c.colorida() && coloridas.get(c).size() < Coluna.ESPACOS;
	}

	/**
	 * Põe uma melhoria comprada numa coluna colorida. Quem não é o Jochi tem
	 * de respeitar a cor de fundo da peça. Completar uma linha deixa uma
	 * melhoria grátis pendente para a coluna branca.
	 */
	void colocar(Melhoria m, Coluna c) {
		if (!c.colorida()) {
			throw new JogadaInvalidaException("A coluna branca só recebe as melhorias grátis");
		}
		if (herdeiro != Herdeiro.JOCHI && m.cor != c) {
			throw new JogadaInvalidaException("Essa melhoria só pode ir para a coluna " + m.cor);
		}
		if (!cabeEm(c)) {
			throw new JogadaInvalidaException("A coluna " + c + " já está cheia");
		}
		coloridas.get(c).add(m);
		int linha = coloridas.get(c).size();
		if (linhaCompleta(linha) && branca[linha - 1] == null && !linhasPendentes.contains(linha)) {
			linhasPendentes.add(linha);
		}
	}

	/** Todas as 3 colunas coloridas têm pelo menos {@code linha} melhorias? */
	boolean linhaCompleta(int linha) {
		for (List<Melhoria> col : coloridas.values()) {
			if (col.size() < linha) {
				return false;
			}
		}
		return true;
	}

	int linhasCompletas() {
		int n = 0;
		while (n < Coluna.ESPACOS && linhaCompleta(n + 1)) {
			n++;
		}
		return n;
	}

	int melhoriasGratisPendentes() {
		return linhasPendentes.size();
	}

	/** Põe a melhoria grátis no espaço da coluna branca da linha completada mais baixa. */
	void colocarGratis(Melhoria m) {
		if (linhasPendentes.isEmpty()) {
			throw new JogadaInvalidaException("Não há melhoria grátis a receber");
		}
		int linha = linhasPendentes.remove(0);
		branca[linha - 1] = m;
	}

	/** Melhoria inicial do Jochi: ocupa o 1º espaço da coluna branca. */
	void colocarNaBrancaInicial(Melhoria m) {
		branca[0] = m;
	}
}
