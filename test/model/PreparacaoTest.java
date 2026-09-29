package model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/** Preparação da partida (manual, p. 3 e 4). */
public class PreparacaoTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;

	@Before
	public void criaPartidaComTresJogadores() {
		jogo = new Jogo(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL", "VERDE");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void tresCidadesComecamReveladasEAtacaveis() {
		assertEquals("Cidades disponíveis no início", 3, jogo.cidadesDisponiveis().size());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void cidadeReveladaComecaComQuatroTesouros() {
		Cidade primeira = jogo.tabuleiro().cidadesReveladas().get(0);
		assertEquals("Tesouros numa cidade recém-revelada", 4, primeira.quantidadeDeTesouros());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void primeiroJogadorComecaComUmaMoeda() {
		assertEquals("Moedas do 1º jogador", 1, jogo.tributosDe("VERMELHO", "MOEDA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void terceiroJogadorComecaComDuasMoedas() {
		assertEquals("Moedas do 3º jogador", 2, jogo.tributosDe("VERDE", "MOEDA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void todosOsPeoesComecamEmKarakorum() {
		assertEquals("Posição inicial do peão", "Karakorum", jogo.posicaoDe("VERDE"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void cadaProvinciaComecaComUmaPecaDeTributo() {
		assertEquals("Peças iniciais na província", 1, jogo.pecasNaProvincia("Estepe"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void cadaJogadorComecaComDozeYurts() {
		assertEquals("Yurts guardados no início", 12, jogo.yurtsDisponiveisDe("AZUL"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void partidaComUmSoJogadorLancaExcecao() {
		new Jogo().iniciarPartida("VERMELHO");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void partidaComSeisJogadoresLancaExcecao() {
		new Jogo().iniciarPartida("A", "B", "C", "D", "E", "F");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void doisJogadoresComAMesmaCorLancaExcecao() {
		new Jogo().iniciarPartida("VERMELHO", "VERMELHO");
	}
}
