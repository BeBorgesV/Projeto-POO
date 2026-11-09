package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

/**
 * Habilidades especiais dos herdeiros (texto das cartelas e manual, p. 20).
 * Cores: Verde = Altani, Preto = Jochi, Vermelho = Chagatai,
 * Amarelo = Tolui, Azul = Ogedei.
 */
public class HerdeiroTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo partida(String... cores) {
		Jogo jogo = Jogo.reiniciarParaTeste(new Random(42));
		jogo.iniciarPartida(cores);
		return jogo;
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void altaniTemDoisPeoes() {
		Jogo jogo = partida("VERDE", "AZUL");
		assertEquals("Peões da Altani", 2, jogo.quantidadeDePeoes("VERDE"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void altaniMoveSoOPeaoEscolhido() {
		Jogo jogo = partida("VERDE", "AZUL");
		jogo.escolherPeao(1);
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P1");
		assertEquals("O 1º peão continua em Karakorum", "Karakorum", jogo.posicaoDoPeao("VERDE", 0));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void jochiComecaComUmaMelhoriaNaColunaBranca() {
		Jogo jogo = partida("PRETO", "AZUL");
		assertEquals("Melhorias na coluna branca do Jochi", 1, jogo.melhoriasDe("PRETO", "BRANCA").size());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void jochiPodePorAMelhoriaEmQualquerColunaColorida() {
		Jogo jogo = partida("PRETO", "AZUL");
		jogo.jogador("PRETO").escolherSecreto(0);
		jogo.jogador("PRETO").receberTributo(TipoTributo.MOEDA, 1);
		jogo.ativarColuna("AMARELA");
		Melhoria m = jogo.area().ver(4);
		Coluna outra = m.cor == Coluna.VERDE ? Coluna.AMARELA : Coluna.VERDE;
		int antes = jogo.melhoriasDe("PRETO", outra.name()).size();
		jogo.comprarMelhoria(4, outra.name());
		assertEquals("Melhorias na coluna escolhida", antes + 1, jogo.melhoriasDe("PRETO", outra.name()).size());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void chagataiTrocaUmCavaloPorUmaEspada() {
		Jogo jogo = partida("VERMELHO", "AZUL");
		jogo.iniciarTurno(1, 0, false);
		jogo.trocarCavaloPorEspada();
		assertEquals("Espadas do Chagatai", 1, jogo.tributosDe("VERMELHO", "ESPADA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void chagataiSoTrocaCavaloUmaVezPorTurno() {
		Jogo jogo = partida("VERMELHO", "AZUL");
		jogo.iniciarTurno(2, 0, false);
		jogo.trocarCavaloPorEspada();
		jogo.trocarCavaloPorEspada();
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void toluiComecaComUmYurtEmKarakorum() {
		Jogo jogo = partida("AMARELO", "AZUL");
		assertEquals("Yurts em Karakorum", Arrays.asList("AMARELO"), jogo.yurtsEm("Karakorum"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void toluiConstroiYurtEmParadaVizinha() {
		Jogo jogo = partida("AMARELO", "AZUL");
		jogo.jogador("AMARELO").receberTributo(TipoTributo.YURT, 1);
		jogo.iniciarTurno(0, 0, false);
		jogo.construirYurt("P4"); // vizinha de Karakorum, sem passar por ela
		assertTrue("Yurt do Tolui em P4", jogo.yurtsEm("P4").contains("AMARELO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void ogedeiPagaUmaMoedaPorUmTesouroDeQualquerCidade() {
		Jogo jogo = partida("AZUL", "VERMELHO");
		jogo.tabuleiro().revelarCidade("Kiev", Arrays.asList(TipoTesouro.PELE, TipoTesouro.BOI));
		jogo.iniciarTurno(0, 0, false);
		jogo.pegarTesouroComMoeda("Kiev", "PELE");
		assertEquals("Peles do Ogedei", 1, jogo.tesourosDoTipo("AZUL", "PELE"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void ogedeiNaoPegaOUltimoTesouroDaCidade() {
		Jogo jogo = partida("AZUL", "VERMELHO");
		jogo.tabuleiro().revelarCidade("Kiev", Arrays.asList(TipoTesouro.PELE));
		jogo.iniciarTurno(0, 0, false);
		jogo.pegarTesouroComMoeda("Kiev", "PELE");
	}
}
