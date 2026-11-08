package model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * API do componente Model: Singleton + Façade (Cap. 17) e observado do
 * padrão Observer.
 *
 * Singleton: o construtor é privado e a única instância sai de
 * {@link #getJogo()}, como no exemplo CtrlVenda do slide.
 * Façade: é a única porta de entrada das regras; View e Controller só
 * recebem textos, números e listas de textos, nunca os objetos internos.
 * Observer: a cada mudança de estado avisa os observadores registrados
 * ({@link #add}), que pedem de volta o que precisam.
 *
 * Fluxo de um turno: {@link #ativarColuna} → ações em qualquer ordem
 * ({@link #mover}, {@link #pegarTributo}, {@link #moverKhan}, ações extras)
 * → {@link #encerrarTurno}.
 */
public class Jogo implements ObservadoIF {
	// ---------- o que get(int) responde aos observadores ----------
	/** get(EVENTO): o tipo da última mudança (EV_*). */
	public static final int EVENTO = 0;
	/** get(VEZ): índice do jogador da vez. */
	public static final int VEZ = 1;

	public static final int EV_ESTADO = 0;
	public static final int EV_MOVIMENTO = 1;
	public static final int EV_CARTELA = 2;
	public static final int EV_CARTAS = 3;
	public static final int EV_FIM = 4;

	static final int MIN_JOGADORES = 2;
	static final int MAX_JOGADORES = 5;
	static final int CONSELHEIROS_ABERTOS = 4;
	static final int SECRETOS_POR_JOGADOR = 2;
	/** Votos de influência por número de melhorias na coluna (0 a 4), manual p. 15. */
	private static final int[] VOTOS_DO_PRIMEIRO = { 1, 1, 2, 2, 3 };
	private static final int[] VOTOS_DO_SEGUNDO = { 0, 1, 1, 2, 2 };

	private static Jogo jogo = null;

	private final Random sorteio;
	private final boolean mapaOficial;
	private final List<ObservadorIF> observadores = new ArrayList<>();
	private int ultimoEvento = EV_ESTADO;

	private final List<Jogador> jogadores = new ArrayList<>();
	private Tabuleiro tabuleiro;
	private Khan khan;
	private TrilhaKurultai kurultai;
	private AreaDeMelhorias area;
	private final List<ConselheiroAberto> abertos = new ArrayList<>();
	private int indiceDaVez;
	private Turno turno;
	private Coluna colunaAtivada;

	// fim de jogo (manual, p. 14)
	private boolean gatilhoDoFim;
	private boolean rodadaFinal;
	private boolean encerrada;
	private final Map<Jogador, Integer> votosFinais = new HashMap<>();
	private final List<Jogador> vencedores = new ArrayList<>();

	// janelas que a View deve exibir (mudança de estado pedida no enunciado)
	private boolean cartelaVisivel;
	private boolean cartasVisiveis;

	private Jogo(Random sorteio, boolean mapaOficial) {
		this.sorteio = sorteio;
		this.mapaOficial = mapaOficial;
	}

	/** Ponto global de acesso à única instância (Singleton). */
	public static Jogo getJogo() {
		if (jogo == null) {
			jogo = new Jogo(new Random(), true);
		}
		return jogo;
	}

	/**
	 * Só para os testes (mesmo pacote): troca a instância única por uma com
	 * semente fixa e mapa de exemplo, para tudo ser previsível.
	 */
	static Jogo reiniciarParaTeste(Random sorteio) {
		jogo = new Jogo(sorteio, false);
		return jogo;
	}

	// ================= OBSERVER =================

	@Override
	public void add(ObservadorIF o) {
		observadores.add(o);
	}

	@Override
	public void remove(ObservadorIF o) {
		observadores.remove(o);
	}

	@Override
	public int get(int i) {
		return i == VEZ ? indiceDaVez : ultimoEvento;
	}

	private void notificar(int evento) {
		ultimoEvento = evento;
		for (ObservadorIF o : new ArrayList<>(observadores)) {
			o.notify(this);
		}
	}

	// ================= PREPARAÇÃO =================

	/** Cores dos 5 herdeiros, na ordem das cartelas. */
	public List<String> coresDisponiveis() {
		List<String> cores = new ArrayList<>();
		for (Herdeiro h : Herdeiro.values()) {
			cores.add(h.cor);
		}
		return cores;
	}

	/** Nome do herdeiro de uma cor (ex.: "Tolui"). */
	public String herdeiroDaCor(String cor) {
		String nome = Herdeiro.daCor(cor).name();
		return nome.charAt(0) + nome.substring(1).toLowerCase();
	}

	/**
	 * Prepara uma partida (manual, p. 3-4). A ordem das cores é a ordem de
	 * jogo; cada cor joga com o herdeiro dela. Os peões começam em Karakorum,
	 * o 1º e o 2º jogador recebem 1 moeda e os demais 2.
	 */
	public void iniciarPartida(String... cores) {
		if (cores == null || cores.length < MIN_JOGADORES || cores.length > MAX_JOGADORES) {
			throw new JogadaInvalidaException("O jogo é para 2 a 5 jogadores");
		}
		Set<Herdeiro> herdeiros = new HashSet<>();
		for (String cor : cores) {
			herdeiros.add(Herdeiro.daCor(cor));
		}
		if (herdeiros.size() != cores.length) {
			throw new JogadaInvalidaException("Cada jogador precisa de uma cor diferente");
		}
		tabuleiro = mapaOficial ? FabricaTabuleiro.criarTabuleiroOficial(sorteio)
				: FabricaTabuleiro.criarTabuleiroDeExemplo(sorteio);
		jogadores.clear();
		for (int i = 0; i < cores.length; i++) {
			Jogador j = new Jogador(cores[i], i < 2 ? 1 : 2);
			j.posicionarTodosEm(tabuleiro.karakorum());
			jogadores.add(j);
		}
		khan = new Khan();
		kurultai = new TrilhaKurultai(cores.length);
		area = new AreaDeMelhorias(sorteio);
		prepararCartas();
		prepararHerdeiros();
		indiceDaVez = 0;
		turno = null;
		colunaAtivada = null;
		gatilhoDoFim = false;
		rodadaFinal = false;
		encerrada = false;
		votosFinais.clear();
		vencedores.clear();
		cartelaVisivel = false;
		cartasVisiveis = false;
		notificar(EV_ESTADO);
	}

	/** 4 conselheiros abertos na mesa e 2 secretos para cada jogador escolher 1. */
	private void prepararCartas() {
		List<ConselheiroAberto> todasAbertas = new ArrayList<>(Arrays.asList(ConselheiroAberto.values()));
		Collections.shuffle(todasAbertas, sorteio);
		abertos.clear();
		abertos.addAll(todasAbertas.subList(0, CONSELHEIROS_ABERTOS));

		List<ConselheiroSecreto> secretos = new ArrayList<>(Arrays.asList(ConselheiroSecreto.values()));
		Collections.shuffle(secretos, sorteio);
		for (int i = 0; i < jogadores.size(); i++) {
			jogadores.get(i).receberSecretos(secretos.subList(i * SECRETOS_POR_JOGADOR, (i + 1) * SECRETOS_POR_JOGADOR));
		}
	}

	/** Habilidades que valem "no início do jogo" (Tolui e Jochi). */
	private void prepararHerdeiros() {
		for (Jogador j : jogadores) {
			if (j.herdeiro() == Herdeiro.TOLUI) {
				tabuleiro.karakorum().colocarYurtDoTolui(j);
				j.usarYurt();
			} else if (j.herdeiro() == Herdeiro.JOCHI) {
				// 2 melhorias ao acaso: uma na coluna branca e a outra na coluna da cor dela
				j.cartela().colocarNaBrancaInicial(area.sortear());
				Melhoria outra = area.sortear();
				j.cartela().colocar(outra, outra.cor);
			}
		}
	}

	// ================= TURNO =================

	/** Altani escolhe, antes de ativar a coluna, qual dos 2 peões vai jogar. */
	public void escolherPeao(int indice) {
		exigirPartidaEmAndamento();
		if (turno != null) {
			throw new JogadaInvalidaException("O peão é escolhido antes de ativar a coluna");
		}
		if (daVez().herdeiro() != Herdeiro.ALTANI) {
			throw new JogadaInvalidaException("Só a Altani tem 2 peões");
		}
		daVez().escolherPeao(indice);
		notificar(EV_MOVIMENTO);
	}

	/** O jogador da vez escolhe 1 das 2 cartas de conselheiro secreto. */
	public void escolherConselheiroSecreto(int indice) {
		exigirPartidaEmAndamento();
		daVez().escolherSecreto(indice);
		notificar(EV_CARTAS);
	}

	/**
	 * Começa o turno pondo a peça de ativação numa coluna livre
	 * ("VERMELHA", "VERDE", "AMARELA" ou "BRANCA"). As ações do turno são as
	 * iniciais do herdeiro mais as melhorias da coluna.
	 */
	public void ativarColuna(String coluna) {
		exigirPartidaEmAndamento();
		if (turno != null) {
			throw new JogadaInvalidaException("Encerre o turno atual antes de ativar outra coluna");
		}
		Jogador j = daVez();
		if (j.precisaEscolherSecreto()) {
			throw new JogadaInvalidaException("Escolha antes o seu conselheiro secreto");
		}
		Coluna c = Coluna.de(coluna);
		List<Acao> acoes = j.cartela().ativar(c);
		turno = Turno.daColuna(j, acoes, j.cartela().tesourosVirtuais(c));
		colunaAtivada = c;
		verificarConselheirosAbertos();
		notificar(EV_CARTELA);
	}

	/**
	 * Começa o turno informando direto os movimentos, as ações de tributo e o
	 * Khan. Gancho dos testes do mesmo pacote, para não depender das colunas.
	 */
	void iniciarTurno(int movimentos, int acoesDeTributo, boolean colunaTemKhan) {
		exigirPartida();
		if (turno != null) {
			throw new JogadaInvalidaException("Encerre o turno atual antes de começar outro");
		}
		turno = new Turno(daVez(), movimentos, acoesDeTributo, colunaTemKhan);
	}

	/**
	 * Passa a vez. O peão precisa terminar numa parada onde caiba e o Khan,
	 * se obrigatório, tem de ter sido usado. Melhorias grátis não pegas vêm do
	 * topo da pilha. Depois do gatilho do fim, termina a rodada, joga-se mais
	 * uma rodada completa e a partida acaba (manual, p. 14).
	 */
	public void encerrarTurno() {
		Turno t = exigirTurno();
		if (!t.jogador().posicao().cabePeaoDe(t.jogador())) {
			throw new JogadaInvalidaException("Não se pode terminar o movimento numa parada ocupada: "
					+ t.jogador().posicao());
		}
		if (t.pendenteUsarKhan()) {
			throw new JogadaInvalidaException("A coluna ativada exige usar o Khan antes de encerrar o turno");
		}
		CartelaDoJogador cartela = t.jogador().cartela();
		while (cartela.melhoriasGratisPendentes() > 0 && area.naPilha() > 0) {
			cartela.colocarGratis(area.retirar(AreaDeMelhorias.PILHA));
		}
		turno = null;
		colunaAtivada = null;
		verificarGatilhoDoFim();
		indiceDaVez = (indiceDaVez + 1) % jogadores.size();
		if (indiceDaVez == 0 && gatilhoDoFim) {
			if (rodadaFinal) {
				apurar();
			} else {
				rodadaFinal = true;
			}
		}
		notificar(encerrada ? EV_FIM : EV_ESTADO);
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
		verificarConselheirosAbertos();
		notificar(EV_MOVIMENTO);
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
		verificarConselheirosAbertos();
		notificar(EV_ESTADO);
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
	 * Move o Khan. Numa província do Khan, ela e as 2 províncias apontadas
	 * pelas setas recebem 1 tributo cada (máximo 3 por província). Na área de
	 * melhorias ({@code "MELHORIAS"}), o jogador ganha a moeda e a espada dos
	 * espaços vazios com esses ícones e depois a área é reposta.
	 */
	public void moverKhan(String destino) {
		Turno t = exigirTurno();
		t.exigirKhanDisponivel();
		if (Khan.AREA_DE_MELHORIAS.equalsIgnoreCase(destino)) {
			khan.moverPara(Khan.AREA_DE_MELHORIAS);
			if (area.vazio(AreaDeMelhorias.ESPACO_DA_MOEDA)) {
				t.jogador().receberTributo(TipoTributo.MOEDA, 1);
			}
			if (area.vazio(AreaDeMelhorias.ESPACO_DA_ESPADA)) {
				t.jogador().receberTributo(TipoTributo.ESPADA, 1);
			}
			area.repor();
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
		verificarConselheirosAbertos();
		notificar(EV_ESTADO);
	}

	// ================= AÇÕES EXTRAS (a qualquer momento do turno) =================

	/**
	 * Toma 1 tesouro de uma cidade vizinha a uma parada deste turno.
	 * O 1º tesouro da cidade no turno custa 1 espada; os seguintes, 2 cada
	 * (espadas virtuais das melhorias são gastas primeiro).
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
		t.pagar(TipoTributo.ESPADA, t.custoEmEspadas(c));
		j.receberTesouro(c.retirarTesouro(tipo));
		t.registrarTesouroTomado(c);
		if (c.quantidadeDeTesouros() == 0) {
			conquistar(c, j);
		}
		verificarConselheirosAbertos();
		notificar(EV_ESTADO);
	}

	/** Conquista: yurt no meio da cidade (se ainda houver) e revela a próxima cidade. */
	private void conquistar(Cidade c, Jogador j) {
		boolean comYurt = j.yurtsDisponiveis() > 0;
		if (comYurt) {
			j.usarYurt();
		}
		c.registrarConquista(j, comYurt);
		tabuleiro.revelarProximaCidade();
		verificarGatilhoDoFim();
	}

	/**
	 * Gasta 1 tributo de yurt (o virtual primeiro) para construir um yurt numa
	 * parada por onde o peão passou neste turno. O Tolui também pode construir
	 * nas paradas vizinhas à dele. Não vale em Karakorum nem onde já houver
	 * yurt (a parada dupla aceita 2).
	 */
	public void construirYurt(String parada) {
		Turno t = exigirTurno();
		Jogador j = t.jogador();
		Parada p = tabuleiro.parada(parada);
		boolean vizinhaDoTolui = j.herdeiro() == Herdeiro.TOLUI && j.posicao().ehVizinhaDe(p);
		if (!t.visitou(p) && !vizinhaDoTolui) {
			throw new JogadaInvalidaException("Só se pode construir em parada visitada neste turno: " + parada);
		}
		if (j.yurtsDisponiveis() == 0) {
			throw new JogadaInvalidaException(j.cor() + " não tem mais yurts para colocar");
		}
		if (t.disponivel(TipoTributo.YURT) == 0) {
			throw new JogadaInvalidaException(j.cor() + " não tem peça de tributo de yurt");
		}
		p.colocarYurt(j); // valida Karakorum e parada cheia antes de gastar
		t.pagar(TipoTributo.YURT, 1);
		j.usarYurt();
		verificarConselheirosAbertos();
		notificar(EV_ESTADO);
	}

	/**
	 * Compra por 2 moedas a melhoria de um espaço da área (0 a 4) ou, com
	 * posição -1, a do topo da pilha. Ela vai para a coluna da cor de fundo;
	 * o Jochi escolhe a coluna ({@code coluna} vazia = a da cor da peça).
	 * Só vale a partir do próximo turno em que a coluna for ativada.
	 */
	public void comprarMelhoria(int posicao, String coluna) {
		Turno t = exigirTurno();
		Jogador j = t.jogador();
		Melhoria m = area.ver(posicao);
		Coluna destino = coluna == null || coluna.trim().isEmpty() ? m.cor : Coluna.de(coluna);
		if (j.herdeiro() != Herdeiro.JOCHI && destino != m.cor) {
			throw new JogadaInvalidaException("Essa melhoria só pode ir para a coluna " + m.cor);
		}
		if (!j.cartela().cabeEm(destino)) {
			throw new JogadaInvalidaException("A coluna " + destino + " não tem espaço para melhorias");
		}
		if (t.disponivel(TipoTributo.MOEDA) < 2) {
			throw new JogadaInvalidaException("Uma melhoria custa 2 moedas");
		}
		t.pagar(TipoTributo.MOEDA, 2);
		area.retirar(posicao);
		j.cartela().colocar(m, destino);
		verificarConselheirosAbertos();
		notificar(EV_CARTELA);
	}

	/** Pega a melhoria grátis de uma linha completa (espaço 0 a 4 ou -1 para a pilha). */
	public void pegarMelhoriaGratis(int posicao) {
		Turno t = exigirTurno();
		CartelaDoJogador cartela = t.jogador().cartela();
		if (cartela.melhoriasGratisPendentes() == 0) {
			throw new JogadaInvalidaException("Não há melhoria grátis a receber");
		}
		cartela.colocarGratis(area.retirar(posicao));
		verificarConselheirosAbertos();
		notificar(EV_CARTELA);
	}

	/**
	 * Entrega tesouros ao conselheiro de uma parada visitada neste turno.
	 * Ganha os votos dele e, se for a 1ª entrega ali, a peça de bônus.
	 */
	public void entregarTesouros(String parada) {
		Turno t = exigirTurno();
		Jogador j = t.jogador();
		Conselheiro c = tabuleiro.conselheiro(parada);
		if (c == null) {
			throw new JogadaInvalidaException("Não há conselheiro em " + parada);
		}
		if (!t.visitou(tabuleiro.parada(parada))) {
			throw new JogadaInvalidaException("Só se entrega a um conselheiro onde o peão parou neste turno");
		}
		TipoTesouro[] virtual = new TipoTesouro[1];
		List<TipoTesouro> reais = c.montarEntrega(j.tesouros(), t.tesourosVirtuais(), virtual);
		j.gastarTesouros(reais);
		if (virtual[0] != null) {
			t.gastarTesouroVirtual(virtual[0]);
		}
		ganharVotos(j, c.votos);
		PecaBonus b = c.entregarBonus();
		if (b != null) {
			receberBonus(j, b);
		}
		verificarConselheirosAbertos();
		notificar(EV_CARTAS);
	}

	/** Bônus de recurso valem na hora; os de ação ficam guardados (ver {@link PecaBonus}). */
	private void receberBonus(Jogador j, PecaBonus b) {
		j.registrarBonus(b);
		switch (b) {
		case VOTO:
			ganharVotos(j, 1);
			break;
		case ESPADAS:
			j.receberTributo(TipoTributo.ESPADA, 2);
			break;
		case MOEDAS:
			j.receberTributo(TipoTributo.MOEDA, 2);
			break;
		case YURTS:
			j.receberTributo(TipoTributo.YURT, 2);
			break;
		case TESOURO:
			// shortcut: fica com o 1º dos 3 tesouros sorteados; trocar por escolha na tela se o grupo quiser
			List<TipoTesouro> tres = tabuleiro.sortearTesouros(3);
			if (!tres.isEmpty()) {
				j.receberTesouro(tres.remove(0));
				tabuleiro.devolverTesouros(tres);
			}
			break;
		default:
			break; // de turno: fica guardada
		}
	}

	/** Usa uma peça de bônus de ação guardada ("CAVALOS_E_PUNHO" ou "PUNHOS"). */
	public void usarBonus(String bonus) {
		Turno t = exigirTurno();
		PecaBonus b = PecaBonus.de(bonus);
		t.jogador().gastarBonus(b);
		if (b == PecaBonus.CAVALOS_E_PUNHO) {
			t.acrescentar(3, 1);
		} else {
			t.acrescentar(0, 2);
		}
		notificar(EV_ESTADO);
	}

	/** Habilidade do Chagatai: 1x por turno, deixa de usar 1 cavalo para ganhar 1 espada. */
	public void trocarCavaloPorEspada() {
		Turno t = exigirTurno();
		if (t.jogador().herdeiro() != Herdeiro.CHAGATAI) {
			throw new JogadaInvalidaException("Só o Chagatai troca cavalo por espada");
		}
		t.trocarCavaloPorEspada();
		verificarConselheirosAbertos();
		notificar(EV_ESTADO);
	}

	/**
	 * Habilidade do Ogedei: 1x por turno, paga 1 moeda para pegar um tesouro
	 * de qualquer cidade revelada, desde que não seja o último dela.
	 */
	public void pegarTesouroComMoeda(String cidade, String tipoDeTesouro) {
		Turno t = exigirTurno();
		if (t.jogador().herdeiro() != Herdeiro.OGEDEI) {
			throw new JogadaInvalidaException("Só o Ogedei pega tesouro com moeda");
		}
		Cidade c = tabuleiro.cidade(cidade);
		TipoTesouro tipo = TipoTesouro.de(tipoDeTesouro);
		if (!c.estaRevelada() || c.quantidadeDeTesouros() < 2) {
			throw new JogadaInvalidaException("Com moeda não se pega o último tesouro de " + cidade);
		}
		if (!c.temTesouro(tipo)) {
			throw new JogadaInvalidaException(cidade + " não tem tesouro do tipo " + tipo);
		}
		t.usarMoedaPorTesouro();
		t.jogador().receberTesouro(c.retirarTesouro(tipo));
		verificarConselheirosAbertos();
		notificar(EV_ESTADO);
	}

	// ================= VOTOS E FIM DE JOGO =================

	private void ganharVotos(Jogador j, int votos) {
		kurultai.registrarVotos(j, votos);
		verificarGatilhoDoFim();
	}

	/** Cartas de conselheiro aberto: o 1º a cumprir ganha 1 voto e a carta sai do jogo. */
	private void verificarConselheirosAbertos() {
		if (turno == null) {
			return;
		}
		for (ConselheiroAberto carta : new ArrayList<>(abertos)) {
			if (carta.cumprida(tabuleiro, turno.jogador(), turno)) {
				abertos.remove(carta);
				ganharVotos(turno.jogador(), 1);
			}
		}
	}

	private void verificarGatilhoDoFim() {
		if (kurultai.fimDisparado() || tabuleiro.todasAsCidadesConquistadas()) {
			gatilhoDoFim = true;
		}
	}

	/** Apuração final: votos do Kurultai + influência nas 3 regiões + conselheiro secreto. */
	private void apurar() {
		encerrada = true;
		votosFinais.clear();
		for (Jogador j : jogadores) {
			int votos = kurultai.votosDe(j);
			for (ConselheiroSecreto s : j.secretos()) {
				votos += s.votos(tabuleiro, j);
			}
			votosFinais.put(j, votos);
		}
		for (Regiao r : Regiao.values()) {
			pontuarInfluencia(r);
		}
		escolherVencedores();
	}

	/**
	 * Quem tem mais yurts na região e o 2º ganham votos conforme as melhorias
	 * na coluna da região. Empate no 1º: todos são 1º e não há 2º.
	 */
	private void pontuarInfluencia(Regiao r) {
		Map<Jogador, Integer> yurts = new HashMap<>();
		int maior = 0;
		for (Jogador j : jogadores) {
			int n = Contagens.yurtsNaRegiao(tabuleiro, j, r);
			yurts.put(j, n);
			maior = Math.max(maior, n);
		}
		if (maior == 0) {
			return;
		}
		int segundo = 0;
		List<Jogador> primeiros = new ArrayList<>();
		for (Jogador j : jogadores) {
			if (yurts.get(j) == maior) {
				primeiros.add(j);
			} else {
				segundo = Math.max(segundo, yurts.get(j));
			}
		}
		Coluna coluna = Coluna.daRegiao(r);
		for (Jogador j : jogadores) {
			int melhorias = Math.min(j.cartela().melhorias(coluna).size(), Coluna.ESPACOS);
			int ganho = 0;
			if (yurts.get(j) == maior) {
				ganho = VOTOS_DO_PRIMEIRO[melhorias];
			} else if (primeiros.size() == 1 && segundo > 0 && yurts.get(j) == segundo) {
				ganho = VOTOS_DO_SEGUNDO[melhorias];
			}
			votosFinais.put(j, votosFinais.get(j) + ganho);
		}
	}

	/** Mais votos; desempate por mais tesouros guardados e depois por quem está mais perto de Karakorum. */
	private void escolherVencedores() {
		vencedores.clear();
		for (Jogador j : jogadores) {
			if (vencedores.isEmpty()) {
				vencedores.add(j);
				continue;
			}
			int comparacao = comparar(j, vencedores.get(0));
			if (comparacao > 0) {
				vencedores.clear();
				vencedores.add(j);
			} else if (comparacao == 0) {
				vencedores.add(j);
			}
		}
	}

	private int comparar(Jogador a, Jogador b) {
		int c = Integer.compare(votosFinais.get(a), votosFinais.get(b));
		if (c == 0) {
			c = Integer.compare(a.totalDeTesouros(), b.totalDeTesouros());
		}
		if (c == 0) {
			c = Integer.compare(Contagens.distanciaAKarakorum(tabuleiro, b), Contagens.distanciaAKarakorum(tabuleiro, a));
		}
		return c;
	}

	// ================= EXIBIÇÃO DAS JANELAS =================

	/** Pede à View para abrir ou fechar a janela da cartela do jogador da vez. */
	public void exibirCartela(boolean visivel) {
		cartelaVisivel = visivel;
		notificar(EV_CARTELA);
	}

	public boolean cartelaVisivel() {
		return cartelaVisivel;
	}

	/** Pede à View para abrir ou fechar a janela de cartas do jogador da vez. */
	public void exibirCartas(boolean visivel) {
		cartasVisiveis = visivel;
		notificar(EV_CARTAS);
	}

	public boolean cartasVisiveis() {
		return cartasVisiveis;
	}

	// ================= CONSULTAS (para View e Controller) =================

	public boolean partidaIniciada() {
		return tabuleiro != null;
	}

	public String corDaVez() {
		exigirPartida();
		return daVez().cor();
	}

	/** Cores dos jogadores, na ordem de jogo. */
	public List<String> cores() {
		List<String> cores = new ArrayList<>();
		for (Jogador j : jogadores) {
			cores.add(j.cor());
		}
		return cores;
	}

	public String herdeiroDe(String cor) {
		return herdeiroDaCor(jogador(cor).cor());
	}

	public boolean turnoEmAndamento() {
		return turno != null;
	}

	/** Coluna ativada no turno atual, ou null. */
	public String colunaAtivada() {
		return colunaAtivada == null ? null : colunaAtivada.name();
	}

	public int movimentosRestantes() {
		return turno == null ? 0 : turno.movimentosRestantes();
	}

	public int acoesDeTributoRestantes() {
		return turno == null ? 0 : turno.acoesDeTributoRestantes();
	}

	public boolean khanPendente() {
		return turno != null && turno.pendenteUsarKhan();
	}

	public int moedasVirtuais() {
		return turno == null ? 0 : turno.moedasVirtuais();
	}

	public int espadasVirtuais() {
		return turno == null ? 0 : turno.espadasVirtuais();
	}

	public int yurtsVirtuais() {
		return turno == null ? 0 : turno.yurtsVirtuais();
	}

	public List<String> tesourosVirtuais() {
		List<String> nomes = new ArrayList<>();
		if (turno != null) {
			for (TipoTesouro t : turno.tesourosVirtuais()) {
				nomes.add(t.name());
			}
		}
		return nomes;
	}

	// ---------- peões ----------

	public String posicaoDe(String cor) {
		return jogador(cor).posicao().nome();
	}

	public int quantidadeDePeoes(String cor) {
		return jogador(cor).posicoes().size();
	}

	public String posicaoDoPeao(String cor, int peao) {
		return jogador(cor).posicoes().get(peao).nome();
	}

	public int peaoAtivo(String cor) {
		return jogador(cor).peaoAtivo();
	}

	// ---------- recursos dos jogadores ----------

	public int tributosDe(String cor, String tipo) {
		return jogador(cor).tributos(TipoTributo.de(tipo));
	}

	public int tesourosDe(String cor) {
		return jogador(cor).totalDeTesouros();
	}

	public int tesourosDoTipo(String cor, String tipo) {
		return jogador(cor).tesouros(TipoTesouro.de(tipo));
	}

	public List<String> tiposDeTesouro() {
		List<String> nomes = new ArrayList<>();
		for (TipoTesouro t : TipoTesouro.values()) {
			nomes.add(t.name());
		}
		return nomes;
	}

	public int yurtsDisponiveisDe(String cor) {
		return jogador(cor).yurtsDisponiveis();
	}

	public int votosDe(String cor) {
		return kurultai.votosDe(jogador(cor));
	}

	public int fichaNeutra() {
		exigirPartida();
		return kurultai.fichaNeutra();
	}

	public int limiteDoKurultai() {
		exigirPartida();
		return kurultai.limite();
	}

	// ---------- cartela ----------

	public List<String> colunas() {
		List<String> nomes = new ArrayList<>();
		for (Coluna c : Coluna.values()) {
			nomes.add(c.name());
		}
		return nomes;
	}

	/** Colunas com peça de ativação do jogador. */
	public List<String> colunasUsadasDe(String cor) {
		List<String> nomes = new ArrayList<>();
		for (Coluna c : Coluna.values()) {
			if (!jogador(cor).cartela().livre(c)) {
				nomes.add(c.name());
			}
		}
		return nomes;
	}

	/** Ids das melhorias de uma coluna do jogador, de baixo para cima. */
	public List<String> melhoriasDe(String cor, String coluna) {
		List<String> ids = new ArrayList<>();
		for (Melhoria m : jogador(cor).cartela().melhorias(Coluna.de(coluna))) {
			ids.add(m.id);
		}
		return ids;
	}

	public int melhoriasGratisPendentes() {
		return turno == null ? 0 : turno.jogador().cartela().melhoriasGratisPendentes();
	}

	/** Ids das melhorias nos 5 espaços da área ("" = espaço vazio). */
	public List<String> melhoriasDisponiveis() {
		exigirPartida();
		return area.ids();
	}

	public int melhoriasNaPilha() {
		exigirPartida();
		return area.naPilha();
	}

	// ---------- cartas e bônus ----------

	public List<String> conselheirosAbertos() {
		List<String> nomes = new ArrayList<>();
		for (ConselheiroAberto c : abertos) {
			nomes.add(c.name());
		}
		return nomes;
	}

	/** As 2 cartas recebidas (antes da escolha) ou a carta escolhida. */
	public List<String> conselheirosSecretosDe(String cor) {
		List<String> nomes = new ArrayList<>();
		for (ConselheiroSecreto c : jogador(cor).secretos()) {
			nomes.add(c.name());
		}
		return nomes;
	}

	public boolean precisaEscolherSecreto(String cor) {
		return jogador(cor).precisaEscolherSecreto();
	}

	public List<String> bonusGuardadosDe(String cor) {
		List<String> nomes = new ArrayList<>();
		for (PecaBonus b : jogador(cor).bonusGuardados()) {
			nomes.add(b.name());
		}
		return nomes;
	}

	public int bonusObtidosDe(String cor) {
		return jogador(cor).bonusObtidos();
	}

	/** Paradas que têm conselheiro. */
	public List<String> conselheiros() {
		exigirPartida();
		return tabuleiro.paradasComConselheiro();
	}

	/** Peça de bônus que ainda está no conselheiro da parada, ou null. */
	public String bonusDoConselheiro(String parada) {
		Conselheiro c = conselheiroDa(parada);
		return c.bonus() == null ? null : c.bonus().name();
	}

	public String pedidoDoConselheiro(String parada) {
		return conselheiroDa(parada).descricao();
	}

	private Conselheiro conselheiroDa(String parada) {
		exigirPartida();
		Conselheiro c = tabuleiro.conselheiro(parada);
		if (c == null) {
			throw new JogadaInvalidaException("Não há conselheiro em " + parada);
		}
		return c;
	}

	// ---------- mapa ----------

	public List<String> paradas() {
		exigirPartida();
		List<String> nomes = new ArrayList<>();
		for (Parada p : tabuleiro.paradas()) {
			nomes.add(p.nome());
		}
		return nomes;
	}

	/** Cores dos donos dos yurts de uma parada. */
	public List<String> yurtsEm(String parada) {
		exigirPartida();
		List<String> cores = new ArrayList<>();
		for (Jogador j : tabuleiro.parada(parada).donosDosYurts()) {
			cores.add(j.cor());
		}
		return cores;
	}

	public List<String> provincias() {
		exigirPartida();
		List<String> nomes = new ArrayList<>();
		for (Provincia p : tabuleiro.provincias()) {
			nomes.add(p.nome());
		}
		Collections.sort(nomes);
		return nomes;
	}

	public int pecasNaProvincia(String provincia) {
		exigirPartida();
		return tabuleiro.provincia(provincia).pecas();
	}

	public String tributoDaProvincia(String provincia) {
		exigirPartida();
		return tabuleiro.provincia(provincia).tipo().name();
	}

	public List<String> cidades() {
		exigirPartida();
		List<String> nomes = new ArrayList<>();
		for (Cidade c : tabuleiro.cidades()) {
			nomes.add(c.nome());
		}
		Collections.sort(nomes);
		return nomes;
	}

	public boolean cidadeRevelada(String cidade) {
		exigirPartida();
		return tabuleiro.cidade(cidade).estaRevelada();
	}

	public List<String> tesourosDaCidade(String cidade) {
		exigirPartida();
		List<String> nomes = new ArrayList<>();
		for (TipoTesouro t : tabuleiro.cidade(cidade).tesouros()) {
			nomes.add(t.name());
		}
		return nomes;
	}

	/** Cor de quem conquistou a cidade, ou null. */
	public String conquistadorDe(String cidade) {
		exigirPartida();
		Jogador j = tabuleiro.cidade(cidade).conquistador();
		return j == null ? null : j.cor();
	}

	public List<String> cidadesDisponiveis() {
		exigirPartida();
		List<String> nomes = new ArrayList<>();
		for (Cidade c : tabuleiro.cidadesReveladas()) {
			if (c.quantidadeDeTesouros() > 0) {
				nomes.add(c.nome());
			}
		}
		Collections.sort(nomes);
		return nomes;
	}

	public String localDoKhan() {
		exigirPartida();
		return khan.local();
	}

	// ---------- opções válidas agora (para preencher a tela) ----------

	/** Províncias com tributo vizinhas a paradas deste turno. */
	public List<String> provinciasParaTributo() {
		List<String> nomes = new ArrayList<>();
		if (turno != null) {
			for (Provincia p : tabuleiro.provincias()) {
				if (p.pecas() > 0 && vizinhaDeParadaVisitada(p, turno)) {
					nomes.add(p.nome());
				}
			}
		}
		Collections.sort(nomes);
		return nomes;
	}

	/** Para onde o Khan pode ir agora (nunca fica onde está). */
	public List<String> destinosDoKhan() {
		List<String> nomes = new ArrayList<>();
		if (turno != null) {
			for (Provincia p : tabuleiro.provinciasDoKhan()) {
				if (!p.nome().equals(khan.local())) {
					nomes.add(p.nome());
				}
			}
			Collections.sort(nomes);
			if (!Khan.AREA_DE_MELHORIAS.equals(khan.local())) {
				nomes.add(Khan.AREA_DE_MELHORIAS);
			}
		}
		return nomes;
	}

	/** Cidades com tesouro vizinhas a paradas deste turno. */
	public List<String> cidadesAtacaveis() {
		List<String> nomes = new ArrayList<>();
		if (turno != null) {
			for (Cidade c : tabuleiro.cidadesReveladas()) {
				if (c.quantidadeDeTesouros() > 0 && vizinhaDeParadaVisitada(c, turno)) {
					nomes.add(c.nome());
				}
			}
		}
		Collections.sort(nomes);
		return nomes;
	}

	/** Paradas onde o jogador da vez pode construir yurt agora. */
	public List<String> paradasParaYurt() {
		List<String> nomes = new ArrayList<>();
		if (turno != null) {
			Jogador j = turno.jogador();
			for (Parada p : tabuleiro.paradas()) {
				boolean alcancada = turno.visitou(p) || (j.herdeiro() == Herdeiro.TOLUI && j.posicao().ehVizinhaDe(p));
				if (alcancada && p.cabeYurt()) {
					nomes.add(p.nome());
				}
			}
		}
		Collections.sort(nomes);
		return nomes;
	}

	/** Paradas com conselheiro onde o peão esteve neste turno. */
	public List<String> conselheirosParaEntrega() {
		List<String> nomes = new ArrayList<>();
		if (turno != null) {
			for (String parada : tabuleiro.paradasComConselheiro()) {
				if (turno.visitou(tabuleiro.parada(parada))) {
					nomes.add(parada);
				}
			}
		}
		Collections.sort(nomes);
		return nomes;
	}

	// ---------- fim de jogo ----------

	/** O gatilho do fim (Kurultai cheio ou todas as cidades conquistadas) já disparou? */
	public boolean fimDePartidaDisparado() {
		exigirPartida();
		return gatilhoDoFim;
	}

	public boolean rodadaFinal() {
		return rodadaFinal;
	}

	public boolean partidaEncerrada() {
		return encerrada;
	}

	/** Votos depois da apuração final. */
	public int votosFinaisDe(String cor) {
		if (!encerrada) {
			throw new JogadaInvalidaException("A partida ainda não acabou");
		}
		return votosFinais.get(jogador(cor));
	}

	public List<String> vencedores() {
		List<String> cores = new ArrayList<>();
		for (Jogador j : vencedores) {
			cores.add(j.cor());
		}
		return cores;
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

	AreaDeMelhorias area() {
		return area;
	}

	List<String> abertosParaTeste(ConselheiroAberto... cartas) {
		abertos.clear();
		abertos.addAll(Arrays.asList(cartas));
		return conselheirosAbertos();
	}

	private void exigirPartida() {
		if (tabuleiro == null) {
			throw new JogadaInvalidaException("A partida ainda não foi iniciada");
		}
	}

	private void exigirPartidaEmAndamento() {
		exigirPartida();
		if (encerrada) {
			throw new JogadaInvalidaException("A partida já acabou");
		}
	}

	private Turno exigirTurno() {
		exigirPartidaEmAndamento();
		if (turno == null) {
			throw new JogadaInvalidaException("Nenhum turno em andamento: ative uma coluna");
		}
		return turno;
	}
}
