package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Ação extra ATACAR CIDADES COM ESPADAS (manual, p. 10).
 * Samarcanda é vizinha de P1; Bagda só de P3.
 */
public class AtaqueCidadeTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador vermelho;

	@Before
	public void vermelhoEmP1AoLadoDeSamarcandaComFerroEPele() {
		jogo = new Jogo(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		vermelho = jogo.jogador("VERMELHO");
		jogo.tabuleiro().revelarCidade("Samarcanda", Arrays.asList(TipoTesouro.FERRO, TipoTesouro.PELE));
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P1");
	}

	private void daEspadas(int quantidade) {
		vermelho.receberTributo(TipoTributo.ESPADA, quantidade);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void primeiroTesouroDaCidadeNoTurnoCustaUmaEspada() {
		daEspadas(3);
		jogo.atacarCidade("Samarcanda", "FERRO");
		assertEquals("Espadas após o 1º tesouro", 2, jogo.tributosDe("VERMELHO", "ESPADA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void segundoTesouroDaMesmaCidadeNoTurnoCustaDuasEspadas() {
		daEspadas(3);
		jogo.atacarCidade("Samarcanda", "FERRO");
		jogo.atacarCidade("Samarcanda", "PELE");
		assertEquals("Espadas após 2 tesouros (1 + 2)", 0, jogo.tributosDe("VERMELHO", "ESPADA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void tesouroTomadoVaiParaOJogador() {
		daEspadas(1);
		jogo.atacarCidade("Samarcanda", "FERRO");
		assertEquals("Tesouros do jogador", 1, jogo.tesourosDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void atacarSemEspadaLancaExcecao() {
		jogo.atacarCidade("Samarcanda", "FERRO");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void tomarOUltimoTesouroConquistaACidade() {
		daEspadas(3);
		jogo.atacarCidade("Samarcanda", "FERRO");
		jogo.atacarCidade("Samarcanda", "PELE");
		assertSame("Conquistador de Samarcanda", vermelho, jogo.tabuleiro().cidade("Samarcanda").conquistador());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void conquistaGastaUmYurtDoJogadorParaOCentroDaCidade() {
		daEspadas(3);
		jogo.atacarCidade("Samarcanda", "FERRO");
		jogo.atacarCidade("Samarcanda", "PELE");
		assertEquals("Yurts guardados após a conquista", 11, jogo.yurtsDisponiveisDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void conquistaRevelaAProximaCidadeDaPilha() {
		int naPilhaAntes = jogo.tabuleiro().cidadesNaPilha();
		daEspadas(3);
		jogo.atacarCidade("Samarcanda", "FERRO");
		jogo.atacarCidade("Samarcanda", "PELE");
		assertEquals("Cidades na pilha após a conquista", naPilhaAntes - 1, jogo.tabuleiro().cidadesNaPilha());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void cidadeQueNaoEhVizinhaDeParadaVisitadaLancaExcecao() {
		jogo.tabuleiro().revelarCidade("Bagda", Arrays.asList(TipoTesouro.FERRO));
		daEspadas(1);
		jogo.atacarCidade("Bagda", "FERRO");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void pedirTesouroQueACidadeNaoTemLancaExcecao() {
		daEspadas(1);
		jogo.atacarCidade("Samarcanda", "TESOURO_3");
	}
}
