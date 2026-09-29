package model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/** Ação PEGAR PEÇAS DE TRIBUTO (manual, p. 8). Estepe (ESPADA) é vizinha de P1 e P4. */
public class TributoTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;

	@Before
	public void criaPartidaComDoisJogadores() {
		jogo = new Jogo(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
	}

	private void moveVermelhoParaP1(int acoesDeTributo) {
		jogo.iniciarTurno(1, acoesDeTributo, false);
		jogo.mover("P1");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void pegarTributoDeProvinciaVizinhaDaParadaVisitadaDaAPecaAoJogador() {
		moveVermelhoParaP1(1);
		jogo.pegarTributo("Estepe");
		assertEquals("Espadas do jogador", 1, jogo.tributosDe("VERMELHO", "ESPADA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void pegarTributoTiraAPecaDaProvincia() {
		moveVermelhoParaP1(1);
		jogo.pegarTributo("Estepe");
		assertEquals("Peças que sobraram na província", 0, jogo.pecasNaProvincia("Estepe"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void paradaOndeOTurnoComecaTambemContaComoVisitada() {
		jogo.jogador("VERMELHO").posicionarEm(jogo.tabuleiro().parada("P4"));
		jogo.iniciarTurno(0, 1, false);
		jogo.pegarTributo("Estepe");
		assertEquals("Espadas do jogador", 1, jogo.tributosDe("VERMELHO", "ESPADA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void provinciaSemParadaVisitadaAoLadoLancaExcecao() {
		jogo.iniciarTurno(0, 1, false); // parado em Karakorum
		jogo.pegarTributo("Estepe");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void pegarTributoSemAcaoRestanteLancaExcecao() {
		moveVermelhoParaP1(0);
		jogo.pegarTributo("Estepe");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void pegarDeProvinciaVaziaLancaExcecao() {
		moveVermelhoParaP1(2);
		jogo.pegarTributo("Estepe");
		jogo.pegarTributo("Estepe");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void tipoDeTributoDesconhecidoNaConsultaLancaExcecao() {
		jogo.tributosDe("VERMELHO", "OURO");
	}
}
