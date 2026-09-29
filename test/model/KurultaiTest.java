package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/** Votos e fim de partida pela trilha do Kurultai (manual, p. 14). */
public class KurultaiTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private TrilhaKurultai trilha;
	private Jogador vermelho;
	private Jogador azul;

	@Before
	public void trilhaParaTresJogadores() {
		trilha = new TrilhaKurultai(3);
		vermelho = new Jogador("VERMELHO", 1);
		azul = new Jogador("AZUL", 1);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void votoAvancaAFichaDoJogador() {
		trilha.registrarVotos(vermelho, 2);
		assertEquals("Votos do vermelho", 2, trilha.votosDe(vermelho));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void votoDeQualquerJogadorAvancaAFichaNeutra() {
		trilha.registrarVotos(vermelho, 2);
		trilha.registrarVotos(azul, 3);
		assertEquals("Ficha neutra", 5, trilha.fichaNeutra());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void fimNaoDisparaComTrezeVotosEmPartidaDeTres() {
		trilha.registrarVotos(vermelho, 7);
		trilha.registrarVotos(azul, 6);
		assertFalse("Fim disparado cedo demais", trilha.fimDisparado());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void fimDisparaComQuatorzeVotosEmPartidaDeTres() {
		trilha.registrarVotos(vermelho, 7);
		trilha.registrarVotos(azul, 7);
		assertTrue("Fim deveria ter sido disparado", trilha.fimDisparado());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void limiteParaCincoJogadoresEhDezessete() {
		assertEquals("Limite com 5 jogadores", 17, new TrilhaKurultai(5).limite());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void trilhaParaSeisJogadoresLancaExcecao() {
		new TrilhaKurultai(6);
	}
}
