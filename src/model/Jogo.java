package model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * API do componente Model.
 *
 * É a única classe pública do pacote (fora a exceção): View e Controller só
 * falam com as regras por aqui, e recebem apenas textos e números, nunca os
 * objetos internos. Na 3ª iteração esta classe vira Singleton + Façade.
 *
 * Fluxo de um turno: {@link #iniciarTurno} → ações ({@link #mover},
 * {@link #pegarTributo}, {@link #atacarCidade}, {@link #construirYurt},
 * {@link #moverKhan}) em qualquer ordem → {@link #encerrarTurno}.
 */
public class Jogo {
	static final int MIN_JOGADORES = 2;
	static final int MAX_JOGADORES = 5;

	private final Random sorteio;
	private final List<Jogador> jogadores = new ArrayList<>();
	private Tabuleiro tabuleiro;
	private Khan khan;
	private TrilhaKurultai kurultai;
	private int indiceDaVez;
	private Turno turno;

	public Jogo() {
		this(new Random());
	}

	/** Usado nos testes, com semente fixa, para o sorteio ser previsível. */
	Jogo(Random sorteio) {
		this.sorteio = sorteio;
	}

	// ================= PREPARAÇÃO =================

	/**
	 * Prepara uma partida. A ordem das cores é a ordem de jogo.
	 * O 1º e o 2º começam com 1 moeda, os demais com 2; todos os peões começam
	 * em Karakorum.
	 */
	public void iniciarPartida(String... cores) {
		if (cores == null || cores.length < MIN_JOGADORES || cores.length > MAX_JOGADORES) {
			throw new JogadaInvalidaException("O jogo é para 2 a 5 jogadores");
		}
		if (new HashSet<>(Arrays.asList(cores)).size() != cores.length) {
			throw new JogadaInvalidaException("Cada jogador precisa de uma cor diferente");
		}
		tabuleiro = FabricaTabuleiro.criarTabuleiroDeExemplo(sorteio);
		jogadores.clear();
		for (int i = 0; i < cores.length; i++) {
			Jogador j = new Jogador(cores[i], i < 2 ? 1 : 2);
			j.posicionarEm(tabuleiro.karakorum());
			jogadores.add(j);
		}
		khan = new Khan();
		kurultai = new TrilhaKurultai(cores.length);
		indiceDaVez = 0;
		turno = null;
	}

	// ================= TURNO =================

	/**
	 * Começa o turno do jogador da vez.
	 *
	 * Por enquanto quem joga informa quantos movimentos e ações de tributo a
	 * coluna ativada dá. Na próxima iteração isto passa a vir das colunas do
	 * tabuleiro do jogador e das peças de ativação.
	 */
	public void iniciarTurno(int movimentos, int acoesDeTributo, boolean colunaTemKhan) {
		exigirPartida();
		if (turno != null) {
			throw new JogadaInvalidaException("Encerre o turno atual antes de começar outro");
		}
		turno = new Turno(daVez(), movimentos, acoesDeTributo, colunaTemKhan);
	}

	/** Passa a vez. O peão precisa terminar numa parada onde caiba. */
	public void encerrarTurno() {
		Turno t = exigirTurno();
		if (!t.jogador().posicao().cabePeaoDe(t.jogador())) {
			throw new JogadaInvalidaException("Não se pode terminar o movimento numa parada ocupada: "
					+ t.jogador().posicao());
		}
		if (t.pendenteUsarKhan()) {
			throw new JogadaInvalidaException("A coluna ativada exige usar o Khan antes de encerrar o turno");
		}
		turno = null;
		indiceDaVez = (indiceDaVez + 1) % jogadores.size();
	}

	// ================= AÇÕES DA COLUNA =================

	/**
	 * Move o peão para {@code destino} gastando 1 movimento.
	 *
	 * O destino precisa ser vizinho da posição atual, ou alcançável passando só
	 * por paradas com yurt do próprio jogador (o yurt permite pular a parada).
	 * Paradas puladas não contam como visitadas. Pode-se passar por uma parada
	 * ocupada, mas não terminar o movimento nela.
	 */
	public void mover(String destino) {
		Turno t = exigirTurno();
		Jogador j = t.jogador();
		Parada alvo = tabuleiro.parada(destino);
		if (alvo == j.posicao()) {
			throw new JogadaInvalidaException("O peão já está em " + destino);
		}
		if (!alcancavelComUmMovimento(j, alvo)) {
			throw new JogadaInvalidaException(destino + " não é vizinha de " + j.posicao());
		}
		if (t.movimentosRestantes() == 1 && !alvo.cabePeaoDe(j)) {
			throw new JogadaInvalidaException("Não se pode terminar o movimento numa parada ocupada: " + destino);
		}
		t.gastarMovimento();
		j.posicionarEm(alvo);
		t.registrarVisita(alvo);
	}

	/** Busca em largura: vizinha direta, ou caminho que só atravessa yurts do próprio jogador. */
	private boolean alcancavelComUmMovimento(Jogador j, Parada alvo) {
		Set<Parada> vistas = new HashSet<>();
		Deque<Parada> fila = new ArrayDeque<>();
		fila.add(j.posicao());
		vistas.add(j.posicao());
		while (!fila.isEmpty()) {
			Parada atual = fila.poll();
			for (Parada vizinha : atual.vizinhas()) {
				if (vizinha == alvo) {
					return true;
				}
				if (vizinha.temYurtDe(j) && vistas.add(vizinha)) {
					fila.add(vizinha);
				}
			}
		}
		return false;
	}

	/**
	 * Pega 1 peça de tributo de uma província vizinha a alguma parada por onde
	 * o peão passou neste turno.
	 */
	public void pegarTributo(String provincia) {
		Turno t = exigirTurno();
		Provincia p = tabuleiro.provincia(provincia);
		if (!vizinhaDeParadaVisitada(p, t)) {
			throw new JogadaInvalidaException(provincia + " não é vizinha de nenhuma parada deste turno");
		}
		if (p.pecas() == 0) {
			throw new JogadaInvalidaException("A província " + provincia + " está vazia");
		}
		t.gastarAcaoDeTributo();
		t.jogador().receberTributo(p.retirarTributo(), 1);
	}

	/** A área (província ou cidade) é vizinha de alguma parada por onde o peão passou neste turno? */
	private boolean vizinhaDeParadaVisitada(AreaDoMapa area, Turno t) {
		for (Parada visitada : t.paradasVisitadas()) {
			if (area.ehVizinhaDe(visitada)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Move o Khan. Numa província do Khan, ela e as 2 províncias
	 * apontadas pelas setas recebem 1 tributo cada (máximo 3 por província).
	 * Use {@code "MELHORIAS"} para a área de melhorias.
	 */
	public void moverKhan(String destino) {
		Turno t = exigirTurno();
		t.exigirKhanDisponivel();
		if (Khan.AREA_DE_MELHORIAS.equalsIgnoreCase(destino)) {
			khan.moverPara(Khan.AREA_DE_MELHORIAS);
			// TODO (iteração futura): revelar novas melhorias e pegar moeda/espada da área.
		} else {
			Provincia p = tabuleiro.provincia(destino);
			if (!p.ehDoKhan()) {
				throw new JogadaInvalidaException(destino + " não é uma província do Khan");
			}
			khan.moverPara(p.nome());
			p.receberTributo();
			for (Provincia ligada : p.ligadasPeloKhan()) {
				ligada.receberTributo();
			}
		}
		t.registrarUsoDoKhan();
	}

	// ================= AÇÕES EXTRAS (a qualquer momento do turno) =================

	/**
	 * Toma 1 tesouro de uma cidade vizinha a uma parada deste turno.
	 * O 1º tesouro da cidade no turno custa 1 espada; os seguintes, 2 cada.
	 * Quem toma o último tesouro conquista a cidade: um yurt seu vai para o meio
	 * dela e a próxima cidade da pilha é revelada.
	 */
	public void atacarCidade(String cidade, String tipoDeTesouro) {
		Turno t = exigirTurno();
		Jogador j = t.jogador();
		Cidade c = tabuleiro.cidade(cidade);
		TipoTesouro tipo = TipoTesouro.de(tipoDeTesouro);
		if (!c.estaRevelada() || c.quantidadeDeTesouros() == 0) {
			throw new JogadaInvalidaException(cidade + " não tem tesouros para atacar");
		}
		if (!vizinhaDeParadaVisitada(c, t)) {
			throw new JogadaInvalidaException(cidade + " não é vizinha de nenhuma parada deste turno");
		}
		if (!c.temTesouro(tipo)) {
			throw new JogadaInvalidaException(cidade + " não tem tesouro do tipo " + tipo);
		}
		j.gastarTributo(TipoTributo.ESPADA, t.custoEmEspadas(c));
		j.receberTesouro(c.retirarTesouro(tipo));
		t.registrarTesouroTomado(c);

		if (c.quantidadeDeTesouros() == 0) {
			c.registrarConquista(j);
			// se o jogador não tiver mais yurts, conquista a cidade mesmo assim
			if (j.yurtsDisponiveis() > 0) {
				j.usarYurt();
			}
			tabuleiro.revelarProximaCidade();
		}
	}

	/**
	 * Gasta 1 peça de tributo de yurt para construir um yurt numa parada por
	 * onde o peão passou neste turno. Não vale em Karakorum nem
	 * onde já houver yurt (a parada dupla aceita 2).
	 */
	public void construirYurt(String parada) {
		Turno t = exigirTurno();
		Jogador j = t.jogador();
		Parada p = tabuleiro.parada(parada);
		if (!t.visitou(p)) {
			throw new JogadaInvalidaException("Só se pode construir em parada visitada neste turno: " + parada);
		}
		if (j.yurtsDisponiveis() == 0) {
			throw new JogadaInvalidaException(j.cor() + " não tem mais yurts para colocar");
		}
		if (j.tributos(TipoTributo.YURT) == 0) {
			throw new JogadaInvalidaException(j.cor() + " não tem peça de tributo de yurt");
		}
		p.colocarYurt(j); // valida Karakorum e parada cheia antes de gastar
		j.gastarTributo(TipoTributo.YURT, 1);
		j.usarYurt();
	}

	// ================= CONSULTAS (para View e Controller) =================

	public String corDaVez() {
		exigirPartida();
		return daVez().cor();
	}

	public String posicaoDe(String cor) {
		return jogador(cor).posicao().nome();
	}

	public int tributosDe(String cor, String tipo) {
		return jogador(cor).tributos(TipoTributo.de(tipo));
	}

	public int tesourosDe(String cor) {
		return jogador(cor).totalDeTesouros();
	}

	public int yurtsDisponiveisDe(String cor) {
		return jogador(cor).yurtsDisponiveis();
	}

	public int votosDe(String cor) {
		return kurultai.votosDe(jogador(cor));
	}

	public int pecasNaProvincia(String provincia) {
		exigirPartida();
		return tabuleiro.provincia(provincia).pecas();
	}

	public List<String> cidadesDisponiveis() {
		exigirPartida();
		List<String> nomes = new ArrayList<>();
		for (Cidade c : tabuleiro.cidadesReveladas()) {
			if (c.quantidadeDeTesouros() > 0) {
				nomes.add(c.nome());
			}
		}
		return nomes;
	}

	public String localDoKhan() {
		exigirPartida();
		return khan.local();
	}

	public boolean fimDePartidaDisparado() {
		exigirPartida();
		return kurultai.fimDisparado();
	}

	// ================= acesso interno (mesmo pacote, usado pelos testes) =================

	Tabuleiro tabuleiro() {
		return tabuleiro;
	}

	Jogador daVez() {
		return jogadores.get(indiceDaVez);
	}

	Jogador jogador(String cor) {
		exigirPartida();
		for (Jogador j : jogadores) {
			if (j.cor().equals(cor)) {
				return j;
			}
		}
		throw new JogadaInvalidaException("Não há jogador da cor " + cor);
	}

	TrilhaKurultai kurultai() {
		return kurultai;
	}

	private void exigirPartida() {
		if (tabuleiro == null) {
			throw new JogadaInvalidaException("A partida ainda não foi iniciada");
		}
	}

	private Turno exigirTurno() {
		exigirPartida();
		if (turno == null) {
			throw new JogadaInvalidaException("Nenhum turno em andamento: chame iniciarTurno");
		}
		return turno;
	}
}
