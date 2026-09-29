package model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/** Ação extra CONSTRUIR YURTS (manual, p. 11). */
public class YurtTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador vermelho;
	private Jogador azul;

	@Before
	public void vermelhoComUmaPecaDeYurt() {
		jogo = new Jogo(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		vermelho = jogo.jogador("VERMELHO");
		azul = jogo.jogador("AZUL");
		vermelho.receberTributo(TipoTributo.YURT, 1);
	}

	private void vermelhoVaiParaP1() {
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P1");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void construirYurtGastaAPecaDeTributoDeYurt() {
		vermelhoVaiParaP1();
		jogo.construirYurt("P1");
		assertEquals("Peças de tributo de yurt após construir", 0, jogo.tributosDe("VERMELHO", "YURT"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void construirYurtUsaUmDosDozeYurtsDoJogador() {
		vermelhoVaiParaP1();
		jogo.construirYurt("P1");
		assertEquals("Yurts guardados após construir", 11, jogo.yurtsDisponiveisDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void naoSePodeConstruirYurtEmKarakorum() {
		jogo.iniciarTurno(0, 0, false);
		jogo.construirYurt("Karakorum");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void naoSePodeConstruirOndeJaExisteYurt() {
		jogo.tabuleiro().parada("P1").colocarYurt(azul);
		vermelhoVaiParaP1();
		jogo.construirYurt("P1");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void paradaDuplaAceitaDoisYurts() {
		Parada p2 = jogo.tabuleiro().parada("P2");
		p2.colocarYurt(azul);
		vermelho.posicionarEm(p2);
		jogo.iniciarTurno(0, 0, false);
		jogo.construirYurt("P2");
		assertEquals("Yurts na parada dupla", 2, p2.quantidadeDeYurts());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void construirSemPecaDeTributoDeYurtLancaExcecao() {
		vermelho.gastarTributo(TipoTributo.YURT, 1);
		vermelhoVaiParaP1();
		jogo.construirYurt("P1");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void construirEmParadaNaoVisitadaNoTurnoLancaExcecao() {
		vermelhoVaiParaP1();
		jogo.construirYurt("P3");
	}
}
