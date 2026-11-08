package controller;

import java.awt.Window;
import java.util.List;

import model.JogadaInvalidaException;
import model.Jogo;
import model.ObservadorIF;
import view.JanelaCartas;
import view.JanelaCartela;
import view.JanelaDoJogo;
import view.JanelaEscolha;
import view.JanelaInicial;

/**
 * Controller: organiza a sequência de eventos de uma partida.
 *
 * Singleton como o "Ctrl" do Cap. 17: os painéis da View se registram como
 * observadores do Jogo por aqui ({@link #registra}). Os tratadores de eventos
 * (um por tipo de evento, sem expressões lambda, Cap. 12) só repassam o
 * evento; quem decide o que fazer, em que ordem, é este controlador:
 * janela inicial → escolha dos herdeiros → para cada vez: conselheiro
 * secreto (1ª rodada) → ativar coluna → ações → encerrar → ... → apuração.
 */
public class ControladorDoJogo {
	private static ControladorDoJogo ctrl = null;

	private final Jogo jogo = Jogo.getJogo();
	private final TratadorDosBotoes tratadorDosBotoes = new TratadorDosBotoes(this);
	private JanelaInicial inicial;
	private JanelaEscolha escolha;
	private JanelaDoJogo janela;
	private JanelaCartela cartela;
	private JanelaCartas cartas;

	private ControladorDoJogo() {
	}

	public static ControladorDoJogo getControlador() {
		if (ctrl == null) {
			ctrl = new ControladorDoJogo();
		}
		return ctrl;
	}

	/** Registra um painel da View como observador do Jogo (como no slide do relógio). */
	public void registra(ObservadorIF o) {
		jogo.add(o);
	}

	// ================= janelas iniciais =================

	void iniciar() {
		inicial = new JanelaInicial();
		inicial.adicionarTratadorDosBotoes(tratadorDosBotoes);
		inicial.setVisible(true);
	}

	private void novoJogo() {
		inicial.dispose();
		escolha = new JanelaEscolha();
		escolha.adicionarTratadorDoBotao(tratadorDosBotoes);
		escolha.setVisible(true);
	}

	/** Cria as janelas da partida (que se registram como observadoras) e começa. */
	private void jogar() {
		List<String> cores = escolha.coresEscolhidas();
		if (cores.size() < 2) {
			escolha.mostrarMensagem("Marque pelo menos 2 herdeiros");
			return;
		}
		janela = new JanelaDoJogo();
		janela.adicionarTratadorDosBotoes(tratadorDosBotoes);
		janela.adicionarTratadorDoTabuleiro(new TratadorDoTabuleiro(this));
		TratadorDasJanelas tratadorDasJanelas = new TratadorDasJanelas(this);
		cartela = new JanelaCartela();
		cartela.adicionarTratadorDoClique(new TratadorDaCartela(this));
		cartela.adicionarTratadorDaJanela(tratadorDasJanelas);
		cartas = new JanelaCartas();
		cartas.adicionarTratadorDosBotoes(tratadorDosBotoes);
		cartas.adicionarTratadorDaJanela(tratadorDasJanelas);

		jogo.iniciarPartida(cores.toArray(new String[0]));
		escolha.dispose();
		janela.setLocation(0, 0);
		janela.setVisible(true);
		cartela.setLocation(200, 380);
		cartas.setLocation(250, 120);
		prepararVez();
	}

	/**
	 * Abre automaticamente o que o jogador da vez precisa: as cartas, se ainda
	 * tem de escolher o conselheiro secreto, ou a cartela, para ativar a coluna.
	 */
	private void prepararVez() {
		if (jogo.partidaEncerrada()) {
			jogo.exibirCartela(false);
			jogo.exibirCartas(false);
			janela.mostrarMensagem("Partida encerrada.");
			return;
		}
		boolean escolher = jogo.precisaEscolherSecreto(jogo.corDaVez());
		jogo.exibirCartas(escolher);
		jogo.exibirCartela(!escolher);
	}

	// ================= eventos =================

	/** Clique no mapa: tenta mover o peão da vez para a parada clicada. */
	void cliqueNoTabuleiro(int x, int y) {
		String parada = janela.paradaEm(x, y);
		if (parada == null) {
			return;
		}
		try {
			jogo.mover(parada);
			janela.mostrarMensagem(" ");
		} catch (JogadaInvalidaException e) {
			// deslocamento inválido: nada acontece e a vez não muda (2ª iteração)
		}
	}

	/** Clique na cartela: ativa a coluna clicada. */
	void cliqueNaCartela(int x, int y) {
		String coluna = cartela.colunaEm(x, y);
		if (coluna != null) {
			executar("ATIVAR:" + coluna);
		}
	}

	/** Fechar a cartela ou a janela de cartas pelo "X" é o mesmo que o botão de esconder. */
	void fecharJanela(Window w) {
		if (w == cartela) {
			jogo.exibirCartela(false);
		} else if (w == cartas) {
			jogo.exibirCartas(false);
		}
	}

	/** Executa um comando de botão; jogada inválida vira mensagem no rodapé, sem diálogo. */
	void executar(String comando) {
		try {
			comandar(comando);
			if (janela != null) {
				janela.mostrarMensagem(" ");
			}
		} catch (JogadaInvalidaException e) {
			if (janela != null) {
				janela.mostrarMensagem(e.getMessage());
			} else if (escolha != null) {
				escolha.mostrarMensagem(e.getMessage());
			}
		}
	}

	private void comandar(String comando) {
		if (comando.startsWith("ATIVAR:")) {
			jogo.ativarColuna(comando.substring("ATIVAR:".length()));
			return;
		}
		switch (comando) {
		case JanelaInicial.NOVO_JOGO:
			novoJogo();
			break;
		case JanelaEscolha.JOGAR:
			jogar();
			break;
		case JanelaCartas.SECRETO_1:
		case JanelaCartas.SECRETO_2:
			jogo.escolherConselheiroSecreto(JanelaCartas.SECRETO_1.equals(comando) ? 0 : 1);
			prepararVez();
			break;
		case JanelaDoJogo.CARTELA:
			jogo.exibirCartela(!jogo.cartelaVisivel());
			break;
		case JanelaDoJogo.CARTAS:
			jogo.exibirCartas(!jogo.cartasVisiveis());
			break;
		case JanelaDoJogo.TROCAR_PEAO:
			String cor = jogo.corDaVez();
			jogo.escolherPeao((jogo.peaoAtivo(cor) + 1) % jogo.quantidadeDePeoes(cor));
			break;
		case JanelaDoJogo.CAVALO_POR_ESPADA:
			jogo.trocarCavaloPorEspada();
			break;
		case JanelaDoJogo.TRIBUTO:
			jogo.pegarTributo(exigir(janela.provinciaEscolhida(), "província"));
			break;
		case JanelaDoJogo.KHAN:
			jogo.moverKhan(exigir(janela.destinoDoKhanEscolhido(), "destino do Khan"));
			break;
		case JanelaDoJogo.ATACAR:
			jogo.atacarCidade(exigir(janela.cidadeEscolhida(), "cidade"), janela.tesouroEscolhido());
			break;
		case JanelaDoJogo.MOEDA_POR_TESOURO:
			jogo.pegarTesouroComMoeda(exigir(janela.cidadeEscolhida(), "cidade"), janela.tesouroEscolhido());
			break;
		case JanelaDoJogo.YURT:
			jogo.construirYurt(exigir(janela.paradaParaYurtEscolhida(), "parada"));
			break;
		case JanelaDoJogo.ENTREGAR:
			jogo.entregarTesouros(exigir(janela.conselheiroEscolhido(), "conselheiro"));
			break;
		case JanelaDoJogo.COMPRAR:
			jogo.comprarMelhoria(janela.espacoDeMelhoriaEscolhido(), janela.colunaEscolhida());
			break;
		case JanelaDoJogo.GRATIS:
			jogo.pegarMelhoriaGratis(janela.espacoDeMelhoriaEscolhido());
			break;
		case JanelaDoJogo.BONUS:
			jogo.usarBonus(exigir(janela.bonusEscolhido(), "peça de bônus"));
			break;
		case JanelaDoJogo.ENCERRAR:
			jogo.encerrarTurno();
			prepararVez();
			break;
		default:
			break;
		}
	}

	private String exigir(String escolhido, String oQue) {
		if (escolhido == null) {
			throw new JogadaInvalidaException("Nenhuma opção de " + oQue + " disponível agora");
		}
		return escolhido;
	}
}
